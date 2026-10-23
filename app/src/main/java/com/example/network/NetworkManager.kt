package com.example.network

import com.example.model.AutoSwitchEvent
import com.example.model.ConnectivityMode
import com.example.model.TransportType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Real-time network telemetry stats.
 */
data class NetworkTelemetry(
    val pingMs: Int = 18,
    val throughputGauge: String = "250 Mbps • P2P",
    val bleSignalAvgDbm: Int = -58,
    val activePeersInRange: Int = 8,
    val activeMeshHops: Int = 3,
    val packetsForwardedTotal: Long = 1420L
)

/**
 * NetworkManager
 *
 * Handles state logic and automatic switching between Cloud/Internet mode
 * and Mesh (BLE / Wi-Fi Direct) mode, as well as explicit user configuration in Settings.
 */
class NetworkManager(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    // Current Connectivity Mode
    private val _connectivityMode = MutableStateFlow(ConnectivityMode.HYBRID_AUTO)
    val connectivityMode: StateFlow<ConnectivityMode> = _connectivityMode.asStateFlow()

    // Internet reachability status (simulated or real hardware check)
    private val _isInternetReachable = MutableStateFlow(true)
    val isInternetReachable: StateFlow<Boolean> = _isInternetReachable.asStateFlow()

    // Simulated Infrastructure Outage (Emergency/Blackout scenario)
    private val _isSimulatedOutage = MutableStateFlow(false)
    val isSimulatedOutage: StateFlow<Boolean> = _isSimulatedOutage.asStateFlow()

    // Mesh Subsystem Status
    private val _isBleMeshActive = MutableStateFlow(true)
    val isBleMeshActive: StateFlow<Boolean> = _isBleMeshActive.asStateFlow()

    private val _isWifiDirectActive = MutableStateFlow(true)
    val isWifiDirectActive: StateFlow<Boolean> = _isWifiDirectActive.asStateFlow()

    // Live Auto-Switch Notification Event
    private val _latestAutoSwitch = MutableStateFlow<AutoSwitchEvent?>(null)
    val latestAutoSwitch: StateFlow<AutoSwitchEvent?> = _latestAutoSwitch.asStateFlow()

    // Telemetry
    private val _telemetry = MutableStateFlow(NetworkTelemetry())
    val telemetry: StateFlow<NetworkTelemetry> = _telemetry.asStateFlow()

    /**
     * Change operational mode from User Settings or quick toggle.
     * Options:
     * - OFFLINE_MESH: Exclusively peer-to-peer (BLE + Wi-Fi Direct)
     * - CLOUD_ONLINE: Exclusively Internet / Cloud Gateway
     * - HYBRID_AUTO: Automatic handover between Cloud, Wi-Fi Direct, and BLE Mesh
     * - WIFI_DIRECT_P2P: Ad-hoc direct Wi-Fi links
     */
    fun setMode(mode: ConnectivityMode) {
        val previousMode = _connectivityMode.value
        _connectivityMode.value = mode

        // Trigger auto-switch notification when mode transition occurs
        if (previousMode != mode) {
            val fromTransport = when (previousMode) {
                ConnectivityMode.CLOUD_ONLINE -> TransportType.CLOUD_GRPC
                ConnectivityMode.WIFI_DIRECT_P2P -> TransportType.WIFI_DIRECT
                else -> TransportType.BLE_L2CAP
            }
            val toTransport = when (mode) {
                ConnectivityMode.CLOUD_ONLINE -> TransportType.CLOUD_GRPC
                ConnectivityMode.WIFI_DIRECT_P2P -> TransportType.WIFI_DIRECT
                else -> TransportType.BLE_L2CAP
            }

            val reason = when (mode) {
                ConnectivityMode.OFFLINE_MESH -> "User enabled Off-Grid Mesh Mode in Settings. Cloud radio halted."
                ConnectivityMode.CLOUD_ONLINE -> "User switched to Internet / Cloud Gateway in Settings. Low-latency cloud pipe active."
                ConnectivityMode.HYBRID_AUTO -> "User enabled Auto-Adaptive Hybrid Mode. Automatic failover active."
                ConnectivityMode.WIFI_DIRECT_P2P -> "User locked connection to Wi-Fi Direct P2P mode."
            }

            val event = AutoSwitchEvent(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                fromTransport = fromTransport,
                toTransport = toTransport,
                reason = reason,
                throughputGauge = if (mode == ConnectivityMode.CLOUD_ONLINE) "Cloud • 12ms RTT" else "P2P Mesh • 802.11ax"
            )
            _latestAutoSwitch.value = event
        }
    }

    /**
     * User shortcut to switch into Mesh mode.
     */
    fun switchToMeshMode() {
        setMode(ConnectivityMode.OFFLINE_MESH)
    }

    /**
     * User shortcut to switch into Internet mode.
     */
    fun switchToInternetMode() {
        setMode(ConnectivityMode.CLOUD_ONLINE)
    }

    /**
     * User shortcut to switch into Hybrid Auto mode.
     */
    fun switchToHybridMode() {
        setMode(ConnectivityMode.HYBRID_AUTO)
    }

    /**
     * Simulate an infrastructure outage (e.g. disaster, cellular tower blackout, offline area).
     * If currently in Cloud or Hybrid mode, automatically switches to Mesh mode.
     */
    fun toggleSimulatedOutage() {
        val nextOutage = !_isSimulatedOutage.value
        _isSimulatedOutage.value = nextOutage
        _isInternetReachable.value = !nextOutage

        if (nextOutage) {
            // Outage occurred! Auto-switch immediately to OFFLINE_MESH
            _connectivityMode.value = ConnectivityMode.OFFLINE_MESH
            val event = AutoSwitchEvent(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                fromTransport = TransportType.CLOUD_GRPC,
                toTransport = TransportType.BLE_L2CAP,
                reason = "⚠️ Internet / Cloud unreachable! Seamlessly auto-failed over to Offline BLE Mesh.",
                throughputGauge = "128 kbps • Multi-Hop Active"
            )
            _latestAutoSwitch.value = event
        } else {
            // Outage resolved! If user was in Hybrid mode, restore Hybrid Auto
            _connectivityMode.value = ConnectivityMode.HYBRID_AUTO
            val event = AutoSwitchEvent(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                fromTransport = TransportType.BLE_L2CAP,
                toTransport = TransportType.CLOUD_GRPC,
                reason = "🌐 Internet connectivity restored. Hybrid Cloud Bridge re-engaged.",
                throughputGauge = "Gigabit Cloud • 12ms RTT"
            )
            _latestAutoSwitch.value = event
        }
    }

    /**
     * Determines whether an outgoing message or media payload warrants automatic
     * transport switching (e.g. promoting from BLE to Wi-Fi Direct for media/voice).
     */
    fun determineTransportForPayload(
        isMedia: Boolean,
        textLength: Int
    ): Pair<TransportType, AutoSwitchEvent?> {
        val currentMode = _connectivityMode.value
        val isOffline = currentMode == ConnectivityMode.OFFLINE_MESH || _isSimulatedOutage.value

        return when {
            !isOffline && currentMode == ConnectivityMode.CLOUD_ONLINE -> {
                Pair(TransportType.CLOUD_GRPC, null)
            }
            !isOffline && currentMode == ConnectivityMode.HYBRID_AUTO && !isMedia -> {
                Pair(TransportType.CLOUD_GRPC, null)
            }
            isMedia || textLength > 140 -> {
                // Large payload: Auto-switch from BLE to high-speed Wi-Fi Direct P2P
                val event = AutoSwitchEvent(
                    id = UUID.randomUUID().toString(),
                    timestamp = System.currentTimeMillis(),
                    fromTransport = TransportType.BLE_L2CAP,
                    toTransport = TransportType.WIFI_DIRECT,
                    reason = "High payload detected. Auto-promoted to Wi-Fi Direct P2P (802.11ax • 250 Mbps).",
                    throughputGauge = "250 Mbps • 14ms RTT"
                )
                _latestAutoSwitch.value = event
                Pair(TransportType.WIFI_DIRECT, event)
            }
            else -> {
                // Small message fits within BLE L2CAP MTU
                Pair(TransportType.BLE_L2CAP, null)
            }
        }
    }

    fun dismissAutoSwitchBanner() {
        _latestAutoSwitch.value = null
    }

    fun updateTelemetry(peerCount: Int, avgRssi: Int) {
        _telemetry.update {
            it.copy(
                activePeersInRange = peerCount,
                bleSignalAvgDbm = avgRssi
            )
        }
    }
}
