package com.example.network

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.util.Collections

/**
 * Zero-Internet APK Sideloading & Distribution Server.
 *
 * Runs an ad-hoc local HTTP server directly on device without cellular or internet connectivity.
 * Nearby peers connecting to this device's Wi-Fi hotspot or local mesh Wi-Fi can scan a QR code
 * and download the exact running "bang.apk" straight over the airwaves.
 */
class ApkDistributionServer(private val context: Context) {

    companion object {
        private const val TAG = "ApkDistributionServer"
        const val DEFAULT_PORT = 8080
    }

    data class ServerStatus(
        val isRunning: Boolean = false,
        val localIp: String = "127.0.0.1",
        val port: Int = DEFAULT_PORT,
        val downloadUrl: String = "",
        val downloadsCompleted: Int = 0,
        val bytesTransferred: Long = 0L,
        val apkSizeBytes: Long = 0L
    )

    private val _status = MutableStateFlow(ServerStatus())
    val status: StateFlow<ServerStatus> = _status.asStateFlow()

    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Starts the local ad-hoc HTTP APK distribution server.
     */
    @Synchronized
    fun start(): Boolean {
        if (_status.value.isRunning) return true

        return try {
            val apkFile = File(context.applicationInfo.sourceDir)
            val apkSize = if (apkFile.exists()) apkFile.length() else 0L

            val socket = try {
                ServerSocket(DEFAULT_PORT)
            } catch (_: Exception) {
                ServerSocket(0) // dynamic fallback port if 8080 is bound
            }
            serverSocket = socket
            val boundPort = socket.localPort
            val localIp = getDeviceIpAddress()
            val url = "http://$localIp:$boundPort/"

            _status.value = ServerStatus(
                isRunning = true,
                localIp = localIp,
                port = boundPort,
                downloadUrl = url,
                apkSizeBytes = apkSize
            )

            serverJob = scope.launch {
                Log.i(TAG, "Ad-hoc APK Sideload Server listening on $url (APK: ${apkSize / 1024 / 1024} MB)")
                while (_status.value.isRunning && !socket.isClosed) {
                    try {
                        val clientSocket = socket.accept()
                        launch { handleClient(clientSocket, apkFile) }
                    } catch (e: Exception) {
                        if (!socket.isClosed) {
                            Log.w(TAG, "Error accepting client connection: ${e.message}")
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start APK Sideload Server: ${e.message}", e)
            _status.value = ServerStatus(isRunning = false)
            false
        }
    }

    /**
     * Stops the local server.
     */
    @Synchronized
    fun stop() {
        try {
            serverSocket?.close()
            serverSocket = null
            serverJob?.cancel()
            serverJob = null
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping server socket: ${e.message}")
        } finally {
            _status.value = _status.value.copy(isRunning = false)
            Log.i(TAG, "Ad-hoc APK Sideload Server stopped.")
        }
    }

    private fun handleClient(socket: Socket, apkFile: File) {
        try {
            socket.use { client ->
                val reader = BufferedReader(InputStreamReader(client.getInputStream()))
                val output = BufferedOutputStream(client.getOutputStream())

                val requestLine = reader.readLine() ?: return
                val parts = requestLine.split(" ")
                if (parts.size < 2) return

                val method = parts[0]
                val path = parts[1]

                Log.d(TAG, "Incoming HTTP request: $method $path from ${client.inetAddress.hostAddress}")

                when {
                    path == "/bang.apk" || path.startsWith("/bang.apk?") -> {
                        serveApkFile(output, apkFile)
                    }
                    else -> {
                        serveLandingPage(output, apkFile.length())
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Client socket error: ${e.message}")
        }
    }

    private fun serveLandingPage(output: OutputStream, apkSize: Long) {
        val sizeMb = "%.1f".format(apkSize / (1024.0 * 1024.0))
        val html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>BANG • Offline APK Direct Transfer</title>
                <style>
                    body {
                        background-color: #070B11;
                        color: #F1F5F9;
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                        margin: 0;
                        padding: 24px;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        min-height: 100vh;
                        box-sizing: border-box;
                    }
                    .card {
                        background: #0F1722;
                        border: 1px solid #1E2A3A;
                        border-radius: 20px;
                        padding: 32px 24px;
                        max-width: 440px;
                        width: 100%;
                        text-align: center;
                        box-shadow: 0 12px 32px rgba(0,0,0,0.6);
                    }
                    .bolt {
                        font-size: 48px;
                        margin-bottom: 8px;
                    }
                    h1 {
                        color: #99FF00;
                        font-size: 32px;
                        letter-spacing: 2px;
                        margin: 0 0 6px 0;
                        font-weight: 900;
                    }
                    .tagline {
                        color: #94A3B8;
                        font-size: 14px;
                        margin-bottom: 24px;
                    }
                    .btn {
                        display: block;
                        background: #99FF00;
                        color: #070B11;
                        text-decoration: none;
                        font-weight: bold;
                        font-size: 16px;
                        padding: 16px 24px;
                        border-radius: 12px;
                        margin: 20px 0;
                        transition: opacity 0.2s;
                    }
                    .btn:hover {
                        opacity: 0.9;
                    }
                    .meta {
                        font-size: 12px;
                        color: #64748B;
                        line-height: 1.6;
                        border-top: 1px solid #1E2A3A;
                        padding-top: 16px;
                        margin-top: 16px;
                    }
                    .badge {
                        display: inline-block;
                        background: #1E293B;
                        color: #00E676;
                        padding: 4px 10px;
                        border-radius: 16px;
                        font-size: 11px;
                        font-weight: bold;
                        margin-bottom: 12px;
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="bolt">⚡</div>
                    <div class="badge">● LOCAL MESH AIRWAVES</div>
                    <h1>BANG</h1>
                    <div class="tagline">No Internet. No Limits. / Connect Beyond The Internet</div>
                    <p style="font-size: 14px; color: #CBD5E1;">Download the full, authentic BANG P2P Mesh Android package directly from this device without cellular or internet access.</p>
                    <a href="/bang.apk" class="btn">⚡ Download BANG.apk ($sizeMb MB)</a>
                    <div class="meta">
                        <strong>Cryptographic Security Verified</strong><br>
                        Ed25519 Identity • Curve25519 AEAD • Offline BLE/Wi-Fi Mesh<br>
                        Install and join the local peer relay instantly.
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        val response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: ${html.toByteArray(Charsets.UTF_8).size}\r\n" +
                "Connection: close\r\n\r\n" +
                html

        output.write(response.toByteArray(Charsets.UTF_8))
        output.flush()
    }

    private fun serveApkFile(output: OutputStream, apkFile: File) {
        if (!apkFile.exists() || !apkFile.canRead()) {
            val notFound = "HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\nConnection: close\r\n\r\n"
            output.write(notFound.toByteArray(Charsets.UTF_8))
            output.flush()
            return
        }

        val fileLength = apkFile.length()
        val header = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/vnd.android.package-archive\r\n" +
                "Content-Disposition: attachment; filename=\"bang.apk\"\r\n" +
                "Content-Length: $fileLength\r\n" +
                "Connection: close\r\n\r\n"

        output.write(header.toByteArray(Charsets.UTF_8))
        output.flush()

        val buffer = ByteArray(16 * 1024)
        var totalBytesSent = 0L
        BufferedInputStream(FileInputStream(apkFile)).use { fis ->
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
                totalBytesSent += bytesRead
            }
        }
        output.flush()

        _status.value = _status.value.copy(
            downloadsCompleted = _status.value.downloadsCompleted + 1,
            bytesTransferred = _status.value.bytesTransferred + totalBytesSent
        )
        Log.i(TAG, "Completed direct APK sideload transfer of $totalBytesSent bytes to client")
    }

    private fun getDeviceIpAddress(): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            // First check Wi-Fi or tethering interface
            for (intf in interfaces) {
                if (intf.name.contains("wlan") || intf.name.contains("ap") || intf.name.contains("rndis")) {
                    val addrs = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress && addr.hostAddress?.contains(":") == false) {
                            return addr.hostAddress ?: "192.168.43.1"
                        }
                    }
                }
            }
            // General fallback
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr.hostAddress?.contains(":") == false) {
                        return addr.hostAddress ?: "192.168.43.1"
                    }
                }
            }
        } catch (_: Exception) {}
        return "192.168.43.1" // standard Android Wi-Fi hotspot gateway IP
    }
}
