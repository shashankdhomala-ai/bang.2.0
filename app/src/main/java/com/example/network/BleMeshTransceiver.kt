package com.example.network

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothStatusCodes
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Native Android Bluetooth Low Energy transceiver for BANG decentralized mesh networking.
 *
 * Implements:
 * 1. 128-bit custom Service (fa87c0d0-afac-11de-8a39-0800200c9a66) and Characteristic (fa87c0d1-afac-11de-8a39-0800200c9a66).
 * 2. Light-Weight Low-Latency Beacon Advertising (broadcasting 4-byte Rotating Node ID / Handle only).
 * 3. Native GATT Server (BluetoothGattServer) accepting high-MTU (up to 512 bytes) encrypted mesh envelopes.
 * 4. Native GATT Client (BluetoothGatt) connecting, negotiating MTU 512, writing binary envelopes, and receiving physical delivery ACKs.
 */
class BleMeshTransceiver(private val context: Context) {

    companion object {
        private const val TAG = "BleMeshTransceiver"
        val BANG_SERVICE_UUID: UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66")
        val BANG_CHAR_UUID: UUID    = UUID.fromString("fa87c0d1-afac-11de-8a39-0800200c9a66")
        val BANG_SERVICE_PARCEL_UUID: ParcelUuid = ParcelUuid(BANG_SERVICE_UUID)
        private const val MAX_BEACON_BYTES = 4
    }

    private val bluetoothManager: BluetoothManager? by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter?
        get() = bluetoothManager?.adapter

    private var advertiseCallback: AdvertiseCallback? = null
    private var scanCallback: ScanCallback? = null

    // GATT Server state
    private var gattServer: BluetoothGattServer? = null
    private var onPayloadReceivedCallback: ((ByteArray) -> Unit)? = null

    // Cache of discovered peer devices by node ID or physical MAC address
    private val discoveredDeviceMap = ConcurrentHashMap<String, BluetoothDevice>()

    private var isBroadcasting: Boolean = false
    private var isScanning: Boolean = false

    val isBroadcastingActive: Boolean
        get() = isBroadcasting

    val isScanningActive: Boolean
        get() = isScanning

    val isGattServerActive: Boolean
        get() = gattServer != null

    /**
     * Checks if runtime/manifest permissions required for BLE scanning are granted.
     */
    fun hasScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Checks if runtime/manifest permissions required for BLE advertising are granted.
     */
    fun hasAdvertisePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Checks if runtime/manifest permissions required for BLE GATT connections are granted.
     */
    fun hasConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // -------------------------------------------------------------------------
    // 1. Light-Weight Beacon Advertising (4-byte presence discovery only)
    // -------------------------------------------------------------------------

    /**
     * Starts broadcasting a light-weight 4-byte Rotating Node ID or Peer Handle.
     * Large payloads are never transmitted in advertising frames; GATT client/server is used instead.
     */
    @SuppressLint("MissingPermission")
    fun startBroadcast(payload: ByteArray): Boolean {
        if (!hasAdvertisePermission()) {
            Log.w(TAG, "Cannot start broadcasting: missing BLUETOOTH_ADVERTISE permission")
            return false
        }

        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            Log.w(TAG, "Bluetooth adapter unavailable or disabled")
            return false
        }

        val advertiser: BluetoothLeAdvertiser? = adapter.bluetoothLeAdvertiser
        if (advertiser == null) {
            Log.w(TAG, "BluetoothLeAdvertiser not supported on this device")
            return false
        }

        // Stop any previous active broadcast
        stopBroadcasting()

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(true) // Must be connectable for incoming GATT clients
            .setTimeout(0)
            .build()

        // Restrict to MAX_BEACON_BYTES (4 bytes) for presence discovery
        val beaconData = if (payload.size > MAX_BEACON_BYTES) payload.copyOfRange(0, MAX_BEACON_BYTES) else payload

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .setIncludeTxPowerLevel(false)
            .addServiceUuid(BANG_SERVICE_PARCEL_UUID)
            .addServiceData(BANG_SERVICE_PARCEL_UUID, beaconData)
            .build()

        val callback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                Log.i(TAG, "BLE Mesh Beacon Broadcast started (4-byte presence token: ${beaconData.joinToString("") { "%02x".format(it) }})")
                isBroadcasting = true
            }

            override fun onStartFailure(errorCode: Int) {
                Log.e(TAG, "BLE Mesh Broadcast failed with error code: $errorCode")
                isBroadcasting = false
                advertiseCallback = null
            }
        }

        return try {
            advertiser.startAdvertising(settings, data, callback)
            advertiseCallback = callback
            true
        } catch (e: Exception) {
            Log.e(TAG, "Exception starting BLE advertisement", e)
            false
        }
    }

    /**
     * Starts broadcasting this node's short handle/ID over BLE advertising beacon.
     */
    @SuppressLint("MissingPermission")
    fun startBroadcasting(nodeId: String): Boolean {
        val shortId = nodeId.filter { it.isLetterOrDigit() }.take(MAX_BEACON_BYTES).padEnd(MAX_BEACON_BYTES, '0')
        return startBroadcast(shortId.toByteArray(Charsets.UTF_8))
    }

    // -------------------------------------------------------------------------
    // 2. Native GATT Server (BluetoothGattServer)
    // -------------------------------------------------------------------------

    /**
     * Starts the native GATT Server to receive incoming encrypted envelopes up to 512 bytes.
     */
    @SuppressLint("MissingPermission")
    fun startGattServer(onPayloadReceived: (ByteArray) -> Unit): Boolean {
        onPayloadReceivedCallback = onPayloadReceived

        if (!hasConnectPermission()) {
            Log.w(TAG, "Cannot start GATT Server: missing BLUETOOTH_CONNECT permission")
            return false
        }

        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            Log.i(TAG, "Cannot start GATT Server: Bluetooth adapter is disabled or unavailable")
            return false
        }

        val manager = bluetoothManager
        if (manager == null) {
            Log.w(TAG, "BluetoothManager unavailable")
            return false
        }

        stopGattServer()

        val serverCallback = object : BluetoothGattServerCallback() {
            override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
                super.onConnectionStateChange(device, status, newState)
                if (device != null) {
                    discoveredDeviceMap[device.address] = device
                }
                Log.d(TAG, "GATT Server client state change: ${device?.address}, status=$status, newState=$newState")
            }

            override fun onCharacteristicWriteRequest(
                device: BluetoothDevice?,
                requestId: Int,
                characteristic: BluetoothGattCharacteristic?,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray?
            ) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
                if (characteristic?.uuid == BANG_CHAR_UUID) {
                    if (value != null && value.isNotEmpty()) {
                        Log.i(TAG, "GATT Server received ${value.size} bytes from ${device?.address}")
                        onPayloadReceivedCallback?.invoke(value)
                    }
                    if (responseNeeded && device != null) {
                        try {
                            gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error sending GATT success response", e)
                        }
                    }
                } else {
                    if (responseNeeded && device != null) {
                        try {
                            gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, offset, null)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error sending GATT failure response", e)
                        }
                    }
                }
            }

            override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
                super.onMtuChanged(device, mtu)
                Log.d(TAG, "GATT Server MTU negotiated to $mtu with ${device?.address}")
            }
        }

        return try {
            val server = manager.openGattServer(context, serverCallback)
            if (server == null) {
                Log.w(TAG, "BluetoothGattServer unavailable on this hardware")
                return false
            }

            val gattService = BluetoothGattService(BANG_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
            val gattChar = BluetoothGattCharacteristic(
                BANG_CHAR_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE or
                        BluetoothGattCharacteristic.PROPERTY_READ or
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY or
                        BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                BluetoothGattCharacteristic.PERMISSION_WRITE or BluetoothGattCharacteristic.PERMISSION_READ
            )
            gattService.addCharacteristic(gattChar)
            server.addService(gattService)
            gattServer = server
            Log.i(TAG, "Native GATT Server started with Service $BANG_SERVICE_UUID and Char $BANG_CHAR_UUID")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Exception starting GATT Server", e)
            false
        }
    }

    /**
     * Stops the GATT Server cleanly.
     */
    @SuppressLint("MissingPermission")
    fun stopGattServer() {
        try {
            if (hasConnectPermission()) {
                gattServer?.clearServices()
                gattServer?.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error closing GATT Server", e)
        } finally {
            gattServer = null
        }
    }

    // -------------------------------------------------------------------------
    // 3. Native GATT Client (BluetoothGatt)
    // -------------------------------------------------------------------------

    /**
     * Connects to a target peer device over BLE GATT, requests MTU 512, writes the full
     * encrypted binary envelope to BANG_CHAR_UUID, and triggers onDeliveryAck on completion.
     */
    @SuppressLint("MissingPermission")
    fun sendPayloadViaGatt(
        peerDeviceAddress: String,
        payload: ByteArray,
        onDeliveryAck: (Boolean) -> Unit
    ) {
        if (!hasConnectPermission()) {
            Log.w(TAG, "Missing BLUETOOTH_CONNECT permission for GATT send to $peerDeviceAddress")
            onDeliveryAck(false)
            return
        }

        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            Log.w(TAG, "Bluetooth adapter disabled or unavailable for GATT send")
            onDeliveryAck(false)
            return
        }

        val targetDevice: BluetoothDevice = try {
            discoveredDeviceMap[peerDeviceAddress]
                ?: adapter.getRemoteDevice(peerDeviceAddress)
        } catch (e: Exception) {
            Log.w(TAG, "Could not resolve device for address $peerDeviceAddress", e)
            onDeliveryAck(false)
            return
        }

        var ackTriggered = false
        fun triggerAckOnce(success: Boolean) {
            if (!ackTriggered) {
                ackTriggered = true
                onDeliveryAck(success)
            }
        }

        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (gatt == null) return
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.w(TAG, "GATT client connection failed with status $status")
                    triggerAckOnce(false)
                    cleanDisconnect(gatt)
                    return
                }

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "GATT Client connected to ${targetDevice.address}, requesting MTU 512")
                    val mtuRequested = gatt.requestMtu(512)
                    if (!mtuRequested) {
                        Log.d(TAG, "requestMtu returned false, discovering services immediately")
                        gatt.discoverServices()
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG, "GATT Client disconnected from ${targetDevice.address}")
                    triggerAckOnce(false)
                    cleanDisconnect(gatt)
                }
            }

            override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
                Log.d(TAG, "GATT Client negotiated MTU: $mtu (status=$status). Discovering services...")
                gatt?.discoverServices()
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                if (gatt == null || status != BluetoothGatt.GATT_SUCCESS) {
                    Log.w(TAG, "GATT Service discovery failed with status $status")
                    triggerAckOnce(false)
                    cleanDisconnect(gatt)
                    return
                }

                val service = gatt.getService(BANG_SERVICE_UUID)
                val characteristic = service?.getCharacteristic(BANG_CHAR_UUID)
                if (characteristic == null) {
                    Log.w(TAG, "BANG Characteristic not found on peer ${targetDevice.address}")
                    triggerAckOnce(false)
                    cleanDisconnect(gatt)
                    return
                }

                Log.i(TAG, "Writing ${payload.size} byte envelope to ${targetDevice.address}")
                val writeSuccess = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val res = gatt.writeCharacteristic(
                        characteristic,
                        payload,
                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                    )
                    res == BluetoothStatusCodes.SUCCESS
                } else {
                    @Suppress("DEPRECATION")
                    characteristic.value = payload
                    @Suppress("DEPRECATION")
                    characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                    @Suppress("DEPRECATION")
                    gatt.writeCharacteristic(characteristic)
                }

                if (!writeSuccess) {
                    Log.e(TAG, "writeCharacteristic returned failure")
                    triggerAckOnce(false)
                    cleanDisconnect(gatt)
                }
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                val success = (status == BluetoothGatt.GATT_SUCCESS)
                Log.i(TAG, "GATT write to ${targetDevice.address} completed (success=$success, status=$status)")
                triggerAckOnce(success)
                cleanDisconnect(gatt)
            }

            private fun cleanDisconnect(gatt: BluetoothGatt?) {
                try {
                    gatt?.disconnect()
                    gatt?.close()
                } catch (_: Exception) {}
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                targetDevice.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
            } else {
                targetDevice.connectGatt(context, false, gattCallback)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in connectGatt to ${targetDevice.address}", e)
            triggerAckOnce(false)
        }
    }

    /**
     * Broadcasts or sends an envelope via GATT client writes.
     * If targetNodeId is provided, retrieves the BluetoothDevice from discoveredDeviceMap by targetNodeId
     * (or matches physical address), initiates device.connectGatt(), requests MTU 512, writes the payload,
     * and closes the connection cleanly upon completion.
     * If targetNodeId is null or not found in cache, broadcasts to discovered peers.
     */
    fun broadcastPayloadViaGatt(
        payload: ByteArray,
        targetNodeId: String? = null,
        onAnyAck: ((Boolean) -> Unit)? = null
    ) {
        val targetDevice = if (targetNodeId != null) {
            discoveredDeviceMap[targetNodeId]
                ?: discoveredDeviceMap.entries.firstOrNull { 
                    it.key.contains(targetNodeId, ignoreCase = true) || targetNodeId.contains(it.key, ignoreCase = true)
                }?.value
        } else null

        if (targetDevice != null) {
            Log.i(TAG, "Targeting direct peer device ${targetDevice.address} for node '$targetNodeId'")
            sendPayloadViaGatt(targetDevice.address, payload) { success ->
                onAnyAck?.invoke(success)
            }
            return
        }

        val targets = discoveredDeviceMap.values.distinctBy { it.address }
        if (targets.isEmpty()) {
            onAnyAck?.invoke(false)
            return
        }

        var anyAck = false
        targets.forEach { device ->
            sendPayloadViaGatt(device.address, payload) { success ->
                if (success && !anyAck) {
                    anyAck = true
                    onAnyAck?.invoke(true)
                }
            }
        }
    }

    /**
     * Overload for backwards compatibility.
     */
    fun broadcastPayloadViaGatt(payload: ByteArray, onAnyAck: ((Boolean) -> Unit)?) {
        broadcastPayloadViaGatt(payload, targetNodeId = null, onAnyAck = onAnyAck)
    }

    // -------------------------------------------------------------------------
    // 4. Scanning & Peer Discovery
    // -------------------------------------------------------------------------

    /**
     * Starts scanning for neighboring BANG mesh nodes matching BANG_SERVICE_UUID.
     */
    @SuppressLint("MissingPermission")
    fun startScanning(
        onPeerDiscovered: (nodeId: String, rssi: Int) -> Unit = { _, _ -> },
        onPacketReceived: ((payload: ByteArray, rssi: Int) -> Unit)? = null
    ): Boolean {
        if (!hasScanPermission()) {
            Log.w(TAG, "Cannot start scan: missing BLUETOOTH_SCAN permission")
            return false
        }

        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            Log.w(TAG, "Bluetooth adapter unavailable or disabled")
            return false
        }

        val scanner: BluetoothLeScanner? = adapter.bluetoothLeScanner
        if (scanner == null) {
            Log.w(TAG, "BluetoothLeScanner not supported or unavailable")
            return false
        }

        // Stop any previous active scan
        stopScanning()

        val scanFilter = try {
            ScanFilter.Builder()
                .setServiceUuid(BANG_SERVICE_PARCEL_UUID)
                .build()
        } catch (e: Exception) {
            Log.w(TAG, "Error setting service UUID scan filter, using wildcard", e)
            ScanFilter.Builder().build()
        }

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result ?: return
                val device = result.device
                discoveredDeviceMap[device.address] = device

                val serviceData = result.scanRecord?.getServiceData(BANG_SERVICE_PARCEL_UUID)
                val peerId = if (serviceData != null && serviceData.isNotEmpty()) {
                    onPacketReceived?.invoke(serviceData, result.rssi)
                    String(serviceData, Charsets.UTF_8).trim()
                } else {
                    device.address
                }

                if (peerId.isNotEmpty()) {
                    discoveredDeviceMap[peerId] = device
                    onPeerDiscovered(peerId, result.rssi)
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                results?.forEach { result ->
                    val device = result.device
                    discoveredDeviceMap[device.address] = device
                    val serviceData = result.scanRecord?.getServiceData(BANG_SERVICE_PARCEL_UUID)
                    val peerId = if (serviceData != null && serviceData.isNotEmpty()) {
                        onPacketReceived?.invoke(serviceData, result.rssi)
                        String(serviceData, Charsets.UTF_8).trim()
                    } else {
                        device.address
                    }
                    if (peerId.isNotEmpty()) {
                        discoveredDeviceMap[peerId] = device
                        onPeerDiscovered(peerId, result.rssi)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e(TAG, "BLE Scan failed with error code: $errorCode")
                isScanning = false
                scanCallback = null
            }
        }

        return try {
            scanner.startScan(listOf(scanFilter), scanSettings, callback)
            scanCallback = callback
            isScanning = true
            Log.i(TAG, "BLE Mesh Scanner started in LOW_LATENCY mode for $BANG_SERVICE_UUID")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Exception starting BLE scan", e)
            isScanning = false
            false
        }
    }

    /**
     * Cleanly stops active BLE broadcasting.
     */
    @SuppressLint("MissingPermission")
    fun stopBroadcasting() {
        if (!isBroadcasting && advertiseCallback == null) return

        try {
            if (hasAdvertisePermission()) {
                advertiseCallback?.let { callback ->
                    bluetoothAdapter?.bluetoothLeAdvertiser?.stopAdvertising(callback)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping BLE advertising", e)
        } finally {
            advertiseCallback = null
            isBroadcasting = false
        }
    }

    /**
     * Cleanly stops active BLE scanning.
     */
    @SuppressLint("MissingPermission")
    fun stopScanning() {
        if (!isScanning && scanCallback == null) return

        try {
            if (hasScanPermission()) {
                scanCallback?.let { callback ->
                    bluetoothAdapter?.bluetoothLeScanner?.stopScan(callback)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping BLE scan", e)
        } finally {
            scanCallback = null
            isScanning = false
        }
    }

    /**
     * Stops broadcasting, scanning, and GATT server cleanly.
     */
    fun stop() {
        stopBroadcasting()
        stopScanning()
        stopGattServer()
        discoveredDeviceMap.clear()
    }
}
