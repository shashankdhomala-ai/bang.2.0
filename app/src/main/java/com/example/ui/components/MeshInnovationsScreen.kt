package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.MeshBatteryGovernor
import com.example.network.MeshFileTransferEngine
import com.example.network.PacketChunk
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

// Cyber & Obsidian tokens
private val ObsidianBg = Color(0xFF080C14)
private val CardSurface = Color(0xFF101724)
private val CardBorder = Color(0xFF1E2B3D)
private val ElectricCyan = Color(0xFF00E5FF)
private val NeonLime = Color(0xFF00FF87)
private val UltraViolet = Color(0xFF9D4EDD)
private val AmberWarn = Color(0xFFFFB300)
private val TextWhite = Color(0xFFF1F5F9)
private val TextMuted = Color(0xFF64748B)

/**
 * High-fidelity Showcase Screen demonstrating:
 * 1. Chunked P2P File Transfer Protocol (Fragmentation, PacketChunk, MD5 Verification)
 * 2. Adaptive Battery Saver & Hop Throttling (MeshBatteryGovernor, dynamic scan intervals, relay gating)
 */
@Composable
fun MeshInnovationsScreen(
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val transferEngine = remember { MeshFileTransferEngine() }
    val governor = remember { MeshBatteryGovernor(context) }
    val systemMetrics = remember { governor.getGovernorMetrics() }
    val hapticHelper = remember { com.example.haptics.HapticHelper(context) }
    var lastHapticPlayed by remember { mutableStateOf<String?>(null) }

    // Interactive File Chunking Demo State
    var selectedFileIndex by remember { mutableIntStateOf(0) }
    val sampleFiles = listOf(
        Triple("voice_memo_emergency.opus", 14336, "14.0 KB • Opus Voice Memo"),
        Triple("mesh_tactical_grid_map.bin", 32768, "32.0 KB • Offline Vector Map"),
        Triple("curve25519_id_cert.json", 6144, "6.0 KB • Cryptographic ID Card")
    )

    var generatedChunks by remember { mutableStateOf<List<PacketChunk>>(emptyList()) }
    var reassembledBytes by remember { mutableStateOf<ByteArray?>(null) }
    var isSimulatingTransfer by remember { mutableStateOf(false) }
    var transferProgressPct by remember { mutableFloatStateOf(0f) }
    var verificationStatusText by remember { mutableStateOf<String?>(null) }

    var simulatedBatteryLevel by remember { mutableFloatStateOf(systemMetrics.batteryPct.toFloat()) }
    var simulatedIsCharging by remember { mutableStateOf(systemMetrics.isCharging) }

    // Intelligent Auto-Gzip Packet Compressor State
    var gzipInputText by remember { mutableStateOf("EMERGENCY TACTICAL ROUTE: Node Charlie coordinates lat=37.7749 lon=-122.4194 battery=84% hops=3 relayKey=ED25519-CHARLIE-88B1 timestamp=1718902140 payload=Cellular infrastructure down; zero-trust mesh operational.") }
    var gzipCompressedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var gzipDecompressedText by remember { mutableStateOf<String?>(null) }

    // Compute Governor recommendation dynamically based on slider
    val computedProfile = remember(simulatedBatteryLevel, simulatedIsCharging) {
        val level = simulatedBatteryLevel.toInt()
        when {
            simulatedIsCharging || level > 50 -> MeshBatteryGovernor.MeshPowerProfile.HIGH_PERFORMANCE
            level in 20..50 -> MeshBatteryGovernor.MeshPowerProfile.BALANCED
            else -> MeshBatteryGovernor.MeshPowerProfile.BATTERY_SAVER
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseGlow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("mesh_innovations_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(ElectricCyan, UltraViolet))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = ObsidianBg,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MESH CONNECTIVITY INNOVATIONS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = ElectricCyan,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Chunked P2P Protocol & Adaptive Battery Governor",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CardSurface)
                            .border(1.dp, CardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // =================================================================
            // SECTION 1: Chunked P2P File Transfer Protocol
            // =================================================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = glowPulse)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, spotColor = ElectricCyan)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Extension,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1. CHUNKED P2P FILE TRANSFER PROTOCOL",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "BLE MTUs are restricted (typically 20–512 bytes), while Wi-Fi Direct supports larger frames. BANG breaks payloads into indexed 1024-byte fragments, routes them over intermediate nodes with store-and-forward tolerance, and reassembles with MD5 integrity validation at the destination.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Visual Pipeline Diagram
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF09101A))
                                .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "File Bytes ➔ Split (1024B chunks) ➔ [FileID|Idx|Total|MD5] ➔ BLE/Wi-Fi Hop ➔ Target Reassembly",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.5.sp,
                                    color = NeonLime
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // File Selector Buttons
                        Text(
                            text = "SELECT SAMPLE PAYLOAD TO FRAGMENT:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sampleFiles.forEachIndexed { index, item ->
                                val isSelected = selectedFileIndex == index
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ElectricCyan.copy(alpha = 0.15f) else Color(0xFF0D1522))
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) ElectricCyan else CardBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedFileIndex = index
                                            generatedChunks = emptyList()
                                            reassembledBytes = null
                                            verificationStatusText = null
                                        }
                                        .padding(vertical = 8.dp, horizontal = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = item.first.take(12),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) ElectricCyan else TextWhite,
                                                fontSize = 9.sp
                                            ),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${item.second / 1024} KB",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextMuted,
                                                fontSize = 8.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions: Split Chunks & Simulate Transfer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val file = sampleFiles[selectedFileIndex]
                                    val fakeBytes = ByteArray(file.second) { (it % 256).toByte() }
                                    val fileId = UUID.randomUUID().toString().take(8)
                                    generatedChunks = transferEngine.splitFileToChunks(fileId, fakeBytes, chunkSize = 1024)
                                    reassembledBytes = null
                                    verificationStatusText = null
                                    transferProgressPct = 0f
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("btn_split_chunks")
                            ) {
                                Text(
                                    text = "Split 1024B Chunks",
                                    color = ObsidianBg,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Button(
                                onClick = {
                                    if (generatedChunks.isNotEmpty()) {
                                        isSimulatingTransfer = true
                                        scope.launch {
                                            transferProgressPct = 0f
                                            verificationStatusText = null
                                            for (i in generatedChunks.indices) {
                                                delay(120)
                                                val chunk = generatedChunks[i]
                                                val assembled = transferEngine.processIncomingChunk(chunk)
                                                transferProgressPct = ((i + 1).toFloat() / generatedChunks.size.toFloat())
                                                if (assembled != null) {
                                                    reassembledBytes = assembled
                                                    verificationStatusText = "MD5 VALIDATED: 100% MATCH (${assembled.size} bytes reassembled)"
                                                }
                                            }
                                            isSimulatingTransfer = false
                                        }
                                    }
                                },
                                enabled = generatedChunks.isNotEmpty() && !isSimulatingTransfer,
                                colors = ButtonDefaults.buttonColors(containerColor = UltraViolet),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("btn_simulate_mesh_transfer")
                            ) {
                                Text(
                                    text = if (isSimulatingTransfer) "Routing..." else "Relay & Assemble",
                                    color = TextWhite,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Transfer progress indicator
                        if (isSimulatingTransfer || transferProgressPct > 0f) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { transferProgressPct },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NeonLime,
                                trackColor = Color(0xFF09101A)
                            )
                        }

                        // Success banner if reassembled
                        if (verificationStatusText != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonLime.copy(alpha = 0.12f))
                                    .border(1.dp, NeonLime, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NeonLime,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = verificationStatusText!!,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonLime,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Visual list of generated fragments
                        if (generatedChunks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "FRAGMENT PACKETS (${generatedChunks.size} CHUNKS CREATED):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                generatedChunks.take(4).forEach { chunk ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF09101B))
                                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "#${chunk.chunkIndex + 1}/${chunk.totalChunks}",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold,
                                                        color = ElectricCyan,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "ID:${chunk.fileId} • ${chunk.payload.size}B",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 9.sp
                                                    )
                                                )
                                            }

                                            Text(
                                                text = "MD5: ${chunk.checksum.take(8)}...",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    color = UltraViolet,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    }
                                }
                                if (generatedChunks.size > 4) {
                                    Text(
                                        text = "+ ${generatedChunks.size - 4} remaining 1024B chunks queued for multi-hop relay...",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // =================================================================
            // SECTION 1B: Intelligent Auto-Gzip Packet Compression
            // =================================================================
            item {
                val compressionMetrics = remember(gzipInputText) {
                    com.example.network.PacketCompressor.analyzeCompression(gzipInputText)
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, NeonLime.copy(alpha = glowPulse)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, spotColor = NeonLime)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = NeonLime,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1B. INTELLIGENT AUTO-GZIP PACKET COMPRESSION",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonLime,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "BLE MTUs are restricted (~512 bytes). PacketCompressor transparently streams raw string payloads through GZIPOutputStream before hop relaying, preserving battery and shrinking radio transmit duty cycles.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Live Bandwidth Savings Metrics Gauge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF09101A))
                                .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "RAW PAYLOAD",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                    )
                                    Text(
                                        text = "${compressionMetrics.originalBytes} Bytes",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite,
                                            fontSize = 14.sp
                                        )
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "SAVINGS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = NeonLime,
                                            fontSize = 9.sp
                                        )
                                    )
                                    Text(
                                        text = "${compressionMetrics.savingsPct.toInt()}% LESS",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            color = NeonLime,
                                            fontSize = 15.sp
                                        )
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "GZIP COMPRESSED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = ElectricCyan,
                                            fontSize = 9.sp
                                        )
                                    )
                                    Text(
                                        text = "${compressionMetrics.compressedBytes} Bytes",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricCyan,
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interactive Compress & Decompress Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    gzipCompressedBytes = com.example.network.PacketCompressor.compress(gzipInputText)
                                    gzipDecompressedText = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("btn_compress_gzip")
                            ) {
                                Text(
                                    text = "Compress GZIP",
                                    color = ObsidianBg,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }

                            Button(
                                onClick = {
                                    val bytes = gzipCompressedBytes ?: com.example.network.PacketCompressor.compress(gzipInputText)
                                    gzipDecompressedText = com.example.network.PacketCompressor.decompress(bytes)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("btn_decompress_gzip")
                            ) {
                                Text(
                                    text = "Decompress & Verify",
                                    color = ElectricCyan,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        if (gzipCompressedBytes != null || gzipDecompressedText != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF09101B))
                                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    if (gzipCompressedBytes != null) {
                                        Text(
                                            text = "GZIP HEX STREAM: ${gzipCompressedBytes!!.take(16).joinToString("") { "%02x".format(it) }}... (${gzipCompressedBytes!!.size} bytes total)",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp,
                                                color = ElectricCyan
                                            )
                                        )
                                    }
                                    if (gzipDecompressedText != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "DECOMPRESSED VERIFIED: \"${gzipDecompressedText!!.take(65)}...\"",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                color = NeonLime
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =================================================================
            // SECTION 2: Adaptive Battery Saver & Hop Throttling
            // =================================================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, AmberWarn.copy(alpha = glowPulse)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, spotColor = AmberWarn)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = AmberWarn,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2. ADAPTIVE BATTERY SAVER & HOP THROTTLING",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWarn,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "To prevent offline BLE scanning and mesh packet relaying from draining user batteries, MeshBatteryGovernor dynamically throttles duty cycles and maximum hop TTLs based on battery level and charging broadcasts.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Active Computed Profile Banner
                        val profileColor = when (computedProfile) {
                            MeshBatteryGovernor.MeshPowerProfile.HIGH_PERFORMANCE -> ElectricCyan
                            MeshBatteryGovernor.MeshPowerProfile.BALANCED -> NeonLime
                            MeshBatteryGovernor.MeshPowerProfile.BATTERY_SAVER -> AmberWarn
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(profileColor.copy(alpha = 0.12f))
                                .border(1.dp, profileColor, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = computedProfile.displayName,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = profileColor,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = "TTL: Max ${computedProfile.maxHops} Hops",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = profileColor,
                                            fontSize = 10.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = computedProfile.dutyCycleDescription,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextWhite,
                                        fontSize = 10.5.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Scan Interval: ${computedProfile.scanIntervalMs}ms",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 9.5.sp
                                        )
                                    )
                                    Text(
                                        text = "Stranger Relay: ${if (computedProfile.allowRelay) "ACTIVE" else "PAUSED"}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (computedProfile.allowRelay) NeonLime else AmberWarn,
                                            fontSize = 9.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Battery Slider & Charging Toggle
                        Text(
                            text = "SIMULATE BATTERY & CHARGING CONDITIONS:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Battery Level: ${simulatedBatteryLevel.toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (simulatedBatteryLevel > 50) NeonLime else if (simulatedBatteryLevel > 20) AmberWarn else Color(0xFFFF1744),
                                    fontSize = 11.sp
                                )
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AC Charging",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextWhite,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = simulatedIsCharging,
                                    onCheckedChange = { simulatedIsCharging = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ObsidianBg,
                                        checkedTrackColor = ElectricCyan,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = Color(0xFF09101A)
                                    ),
                                    modifier = Modifier.testTag("switch_charging")
                                )
                            }
                        }

                        Slider(
                            value = simulatedBatteryLevel,
                            onValueChange = { simulatedBatteryLevel = it },
                            valueRange = 1f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricCyan,
                                activeTrackColor = ElectricCyan,
                                inactiveTrackColor = Color(0xFF09101A)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("slider_battery_level")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3 Profiles Summary Comparison Matrix
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ProfileMatrixRow(
                                title = "⚡ High (>50% or Charging)",
                                subtitle = "1s scan window • 7 max hops • Full relay",
                                isCurrent = computedProfile == MeshBatteryGovernor.MeshPowerProfile.HIGH_PERFORMANCE
                            )
                            ProfileMatrixRow(
                                title = "⚖️ Balanced (20% - 50%)",
                                subtitle = "5s scan window • 4 max hops • Power-save relay",
                                isCurrent = computedProfile == MeshBatteryGovernor.MeshPowerProfile.BALANCED
                            )
                            ProfileMatrixRow(
                                title = "🔋 Battery Saver (<20%)",
                                subtitle = "15s duty cycle • 1 hop direct • Relay suspended",
                                isCurrent = computedProfile == MeshBatteryGovernor.MeshPowerProfile.BATTERY_SAVER
                            )
                        }
                    }
                }
            }

            // =================================================================
            // SECTION 3: Tactile Haptic Engine & Vibration Profiles
            // =================================================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, UltraViolet.copy(alpha = glowPulse)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, spotColor = UltraViolet)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = UltraViolet,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "3. TACTILE HAPTIC ENGINE (HAPTICHELPER)",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = UltraViolet,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "BANG uses custom haptic waveforms to give users physical tactile intuition for network state without looking at the screen. Distinguishes offline store-and-forward mesh hops from cloud messages and urgent SOS broadcasts.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Active test feedback badge
                        if (lastHapticPlayed != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElectricCyan.copy(alpha = 0.12f))
                                    .border(1.dp, ElectricCyan, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "VIBRATED: $lastHapticPlayed",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricCyan,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Profile 1: BLE Mesh Packet
                        HapticProfileCard(
                            emoji = "⚡",
                            title = "BLE Mesh Messages (Offline Hop)",
                            feeling = "Crisp, metallic double-tap with node hop ripple.",
                            composition = "PRIMITIVE_CLICK (0.8f) + 60ms delay + PRIMITIVE_TICK (0.5f). Adds +1 tick echo for multi-hop routes (>2 hops).",
                            buttonText = "Play Mesh Hop (2 Hops)",
                            accentColor = ElectricCyan,
                            onPlay = {
                                hapticHelper.triggerMessageHaptic(isOfflineMesh = true, hopCount = 2)
                                lastHapticPlayed = "⚡ BLE Mesh Double-Tap (2 Hops: Click + Tick)"
                            },
                            secondaryButtonText = "Multi-Hop (4 Hops)",
                            onPlaySecondary = {
                                hapticHelper.triggerMessageHaptic(isOfflineMesh = true, hopCount = 4)
                                lastHapticPlayed = "⚡ BLE Mesh Multi-Hop (4 Hops: Click + Tick + Echo)"
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Profile 2: Cloud Messages
                        HapticProfileCard(
                            emoji = "🌐",
                            title = "Cloud Messages (Online Standard)",
                            feeling = "Smooth, soft single pulse.",
                            composition = "PRIMITIVE_QUICK_RISE (0.6f) or createOneShot(40ms, amp 120). Low amplitude single bump.",
                            buttonText = "Play Cloud Single Pulse",
                            accentColor = NeonLime,
                            onPlay = {
                                hapticHelper.triggerMessageHaptic(isOfflineMesh = false)
                                lastHapticPlayed = "🌐 Cloud Smooth Single Pulse (Quick Rise)"
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Profile 3: SOS Emergency Broadcast
                        HapticProfileCard(
                            emoji = "🆘",
                            title = "SOS Emergency Broadcast",
                            feeling = "High-intensity urgent triple-staccato.",
                            composition = "Waveform: [0ms off, 100ms on, 50ms off, 100ms on, 50ms off, 200ms on] @ Amplitude 255.",
                            buttonText = "Play SOS Urgent Alarm Haptic",
                            accentColor = Color(0xFFFF1744),
                            onPlay = {
                                hapticHelper.playSOSPriorityHaptic()
                                lastHapticPlayed = "🆘 SOS Urgent Triple-Staccato (Amp 255 Max)"
                            }
                        )
                    }
                }
            }

            // =================================================================
            // SECTION 4: Dynamic Mesh Topology Radar View
            // =================================================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = glowPulse)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, spotColor = ElectricCyan)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4. DYNAMIC MESH TOPOLOGY RADAR VIEW",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Real-time physical proximity scanner. Sweeping 360-degree radar detects neighboring nodes over BLE 5.3 L2CAP without requiring cellular towers or GPS.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Render the Dynamic Mesh Radar View
                        MeshRadarView(
                            activePeersCount = 5,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00FF87)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Host Node (You)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = TextWhite,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00E5FF)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "5 Detected BLE Peers",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = ElectricCyan,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileMatrixRow(
    title: String,
    subtitle: String,
    isCurrent: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCurrent) ElectricCyan.copy(alpha = 0.15f) else Color(0xFF09101A))
            .border(
                width = 1.dp,
                color = if (isCurrent) ElectricCyan else CardBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) ElectricCyan else TextWhite,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 8.5.sp
                    )
                )
            }

            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ElectricCyan)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ACTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = ObsidianBg,
                            fontSize = 8.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun HapticProfileCard(
    emoji: String,
    title: String,
    feeling: String,
    composition: String,
    buttonText: String,
    accentColor: Color,
    onPlay: () -> Unit,
    secondaryButtonText: String? = null,
    onPlaySecondary: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF09101B))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        fontSize = 11.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tactile Feeling: $feeling",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextWhite,
                    fontSize = 10.5.sp
                )
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Waveform Spec: $composition",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF94A3B8),
                    fontSize = 9.sp,
                    lineHeight = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text(
                        text = buttonText,
                        color = ObsidianBg,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                if (secondaryButtonText != null && onPlaySecondary != null) {
                    Button(
                        onClick = onPlaySecondary,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Text(
                            text = secondaryButtonText,
                            color = accentColor,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
