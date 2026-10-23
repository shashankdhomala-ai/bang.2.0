package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.MeshPeerNode
import com.example.ui.BangUiState
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// -----------------------------------------------------------------------------
// Cyber-Obsidian Design Tokens
// -----------------------------------------------------------------------------
private val ObsidianBg = Color(0xFF080C14)
private val SurfaceDark = Color(0xFF0F1724)
private val SurfaceVariant = Color(0xFF131D2E)
private val BorderMuted = Color(0xFF1E2B3E)
private val NeonCyan = Color(0xFF00E5FF)
private val KineticLime = Color(0xFF7FFF00)
private val NeonPink = Color(0xFFFF007F)
private val NeonPurple = Color(0xFF9D4EDD)
private val TextHigh = Color(0xFFF1F5F9)
private val TextMedium = Color(0xFF94A3B8)
private val TextLow = Color(0xFF475569)

@Composable
fun MeshRadarScreen(
    uiState: BangUiState,
    onSelectPeer: (MeshPeerNode?) -> Unit,
    onStartChatWithPeer: (MeshPeerNode) -> Unit,
    onOpenDirectDiscoveryModal: (MeshPeerNode) -> Unit = {},
    onScanNfc: () -> Unit = {},
    onShowQrCode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showLocalNfcDialog by remember { mutableStateOf(false) }
    var showLocalQrDialog by remember { mutableStateOf(false) }

    // Smooth Infinite Animations
    val infiniteTransition = rememberInfiniteTransition(label = "meshRadarAnim")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val rippleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleProgress"
    )

    val peerCount = uiState.meshPeers.size.coerceAtLeast(8)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState())
            .testTag("mesh_radar_screen")
    ) {
        // ---------------------------------------------------------------------
        // 1. Header: Streamlined Title & Active Peers Capsule
        // ---------------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(KineticLime.copy(alpha = 0.15f))
                        .border(0.8.dp, KineticLime.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Radar",
                        tint = KineticLime,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "MESH RADAR",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            fontSize = 15.sp
                        ),
                        color = TextHigh
                    )
                    Text(
                        text = "Local RF Range • 40m Radius",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            color = TextLow
                        )
                    )
                }
            }

            // Compact Active Peers Pill
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceDark,
                border = BorderStroke(0.8.dp, KineticLime.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(KineticLime)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$peerCount Nodes",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = KineticLime
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ---------------------------------------------------------------------
        // 2. High-Performance Animated Radar Graphic Canvas
        // ---------------------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(Color(0xFF060A10))
                .border(
                    width = 1.2.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            KineticLime.copy(alpha = 0.7f),
                            NeonCyan.copy(alpha = 0.7f),
                            NeonPurple.copy(alpha = 0.3f),
                            KineticLime.copy(alpha = 0.7f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(uiState.meshPeers) {
                        detectTapGestures { tapOffset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val maxRadius = size.width / 2f * 0.86f
                            var nearestPeer: MeshPeerNode? = null
                            var minDistance = Float.MAX_VALUE

                            uiState.meshPeers.forEach { peer ->
                                val r = (peer.distanceMeters / 40f).coerceIn(0.12f, 0.95f) * maxRadius
                                val rad = Math.toRadians(peer.angleDegrees.toDouble() - 90.0)
                                val x = center.x + (r * cos(rad)).toFloat()
                                val y = center.y + (r * sin(rad)).toFloat()
                                val dist = sqrt((tapOffset.x - x) * (tapOffset.x - x) + (tapOffset.y - y) * (tapOffset.y - y))
                                if (dist < 36.dp.toPx() && dist < minDistance) {
                                    minDistance = dist
                                    nearestPeer = peer
                                }
                            }
                            onSelectPeer(nearestPeer)
                            if (nearestPeer != null && nearestPeer.displayName.contains("Alex Rivera")) {
                                onOpenDirectDiscoveryModal(nearestPeer)
                            }
                        }
                    }
                    .testTag("animated_radar_canvas")
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.width / 2f * 0.86f

                // Expanding Sonar Wave
                val rippleRadius = maxRadius * rippleProgress
                val rippleAlpha = (1f - rippleProgress).coerceIn(0f, 0.45f)
                drawCircle(
                    color = KineticLime.copy(alpha = rippleAlpha),
                    radius = rippleRadius,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )

                // Concentric Range Rings: 10m (25%), 20m (50%), 40m (100%)
                val rangeRings = listOf(0.25f to "10m", 0.5f to "20m", 1f to "40m")
                rangeRings.forEachIndexed { idx, (frac, _) ->
                    val r = maxRadius * frac
                    drawCircle(
                        color = (if (idx % 2 == 0) KineticLime else NeonCyan).copy(alpha = if (frac == 1f) 0.45f else 0.22f),
                        radius = r,
                        center = center,
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                        )
                    )
                }

                // Crosshairs
                drawLine(
                    color = NeonCyan.copy(alpha = 0.15f),
                    start = Offset(center.x, center.y - maxRadius),
                    end = Offset(center.x, center.y + maxRadius),
                    strokeWidth = 0.8.dp.toPx()
                )
                drawLine(
                    color = NeonCyan.copy(alpha = 0.15f),
                    start = Offset(center.x - maxRadius, center.y),
                    end = Offset(center.x + maxRadius, center.y),
                    strokeWidth = 0.8.dp.toPx()
                )

                // Sweeping Radar Ray
                val sweepRad = Math.toRadians((sweepAngle - 90).toDouble())
                val endX = center.x + (maxRadius * cos(sweepRad)).toFloat()
                val endY = center.y + (maxRadius * sin(sweepRad)).toFloat()
                drawLine(
                    brush = Brush.radialGradient(
                        colors = listOf(KineticLime, NeonCyan.copy(alpha = 0.05f)),
                        center = center,
                        radius = maxRadius
                    ),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Render Discovered Peer Blips
                uiState.meshPeers.forEach { peer ->
                    val r = (peer.distanceMeters / 40f).coerceIn(0.12f, 0.95f) * maxRadius
                    val rad = Math.toRadians(peer.angleDegrees.toDouble() - 90.0)
                    val blipCenter = Offset(
                        center.x + (r * cos(rad)).toFloat(),
                        center.y + (r * sin(rad)).toFloat()
                    )
                    val isSelected = peer.id == uiState.selectedPeer?.id
                    val isDirect = peer.hopsAway == 1
                    val dotColor = when {
                        isSelected -> NeonPink
                        isDirect -> KineticLime
                        else -> NeonCyan
                    }

                    // Pulsing Halo
                    drawCircle(
                        color = dotColor.copy(alpha = if (isSelected) 0.5f else 0.22f),
                        radius = (if (isSelected) 12.dp else 7.dp).toPx() * (if (isSelected) pulseScale else 1f),
                        center = blipCenter
                    )
                    // Core Dot
                    drawCircle(
                        color = dotColor,
                        radius = (if (isSelected) 4.5.dp else 3.5.dp).toPx(),
                        center = blipCenter
                    )
                }

                // Center Node [YOU]
                drawCircle(color = NeonPink.copy(alpha = 0.25f), radius = 16.dp.toPx() * pulseScale, center = center)
                drawCircle(color = NeonPink, radius = 5.dp.toPx(), center = center)
            }

            // Overlay for Peer @handle Labels
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val boxWidthPx = constraints.maxWidth.toFloat()
                val boxHeightPx = constraints.maxHeight.toFloat()
                val centerPx = Offset(boxWidthPx / 2f, boxHeightPx / 2f)
                val maxRadiusPx = boxWidthPx / 2f * 0.86f

                Text(
                    text = "[YOU]",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.sp,
                        color = NeonPink
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 13.dp)
                )

                uiState.meshPeers.forEach { peer ->
                    val r = (peer.distanceMeters / 40f).coerceIn(0.12f, 0.95f) * maxRadiusPx
                    val rad = Math.toRadians(peer.angleDegrees.toDouble() - 90.0)
                    val x = centerPx.x + (r * cos(rad)).toFloat()
                    val y = centerPx.y + (r * sin(rad)).toFloat()
                    val xFraction = (x / boxWidthPx).coerceIn(0.06f, 0.84f)
                    val yFraction = (y / boxHeightPx).coerceIn(0.06f, 0.88f)
                    val isSelected = peer.id == uiState.selectedPeer?.id

                    Box(
                        modifier = Modifier
                            .offset(
                                x = (boxWidthPx * xFraction / 2.7f).dp - 16.dp,
                                y = (boxHeightPx * yFraction / 2.7f).dp - 5.dp
                            )
                            .clickable {
                                onSelectPeer(peer)
                                if (peer.displayName.contains("Alex Rivera")) {
                                    onOpenDirectDiscoveryModal(peer)
                                }
                            }
                    ) {
                        Text(
                            text = peer.handle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 7.8.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) NeonPink else if (peer.hopsAway == 1) KineticLime else NeonCyan
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Cardinal Compass Labels: N, S, W, E
            Text(
                text = "N",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.5.sp
                ),
                color = NeonCyan.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
            )
            Text(
                text = "S",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.5.sp
                ),
                color = NeonCyan.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
            )
            Text(
                text = "W",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.5.sp
                ),
                color = NeonCyan.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 6.dp)
            )
            Text(
                text = "E",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.5.sp
                ),
                color = NeonCyan.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 6.dp)
            )

            // Range Markers: 10m, 20m, 40m
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0A101A).copy(alpha = 0.85f))
                    .border(0.5.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "10m • 20m • 40m",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 7.8.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan.copy(alpha = 0.9f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ---------------------------------------------------------------------
        // 3. High-Density Local Node Inspector Card
        // ---------------------------------------------------------------------
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(0.8.dp, BorderMuted),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LOCAL NODE INSPECTOR (${uiState.meshPeers.size} NODES)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.5.sp,
                            color = KineticLime,
                            letterSpacing = 0.8.sp
                        )
                    )

                    Text(
                        text = "REAL-TIME RSSI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            color = NeonCyan
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // List of peer nodes with exact connectivity stats
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    uiState.meshPeers.forEach { peer ->
                        val isSelected = peer.id == uiState.selectedPeer?.id
                        val signalStrength = when {
                            peer.rssi >= -60 -> "Strong"
                            peer.rssi >= -75 -> "Good"
                            peer.rssi >= -85 -> "Moderate"
                            else -> "Weak"
                        }
                        val linkType = if (peer.hopsAway == 1) "Direct" else "${peer.hopsAway} Hops"

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonPink.copy(alpha = 0.12f) else SurfaceVariant)
                                .border(
                                    width = 0.8.dp,
                                    color = if (isSelected) NeonPink.copy(alpha = 0.8f) else BorderMuted,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onSelectPeer(peer)
                                    if (peer.displayName.contains("Alex Rivera")) {
                                        onOpenDirectDiscoveryModal(peer)
                                    }
                                }
                                .padding(horizontal = 9.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(if (peer.hopsAway == 1) KineticLime else NeonCyan)
                                    )
                                    Spacer(modifier = Modifier.width(7.dp))
                                    Text(
                                        text = "${peer.handle} • ${peer.rssi} dBm • ${if (peer.hopsAway == 1) "Direct BLE" else "${peer.hopsAway} Hops Relay"}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) TextHigh else TextMedium
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (peer.isFriend) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(KineticLime.copy(alpha = 0.15f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "FRIEND",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = KineticLime
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // If a peer is selected, show an inline action drawer
                uiState.selectedPeer?.let { peer ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF090E17))
                            .border(0.9.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(9.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SELECTED: ${peer.displayName} (${peer.handle})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan,
                                        fontSize = 9.5.sp
                                    )
                                )
                                IconButton(
                                    onClick = { onSelectPeer(null) },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Deselect",
                                        tint = TextMedium,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }

                            Text(
                                text = peer.keyInfo,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.5.sp,
                                    color = TextMedium
                                )
                            )

                            Spacer(modifier = Modifier.height(7.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onOpenDirectDiscoveryModal(peer) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                                    border = BorderStroke(0.8.dp, NeonCyan)
                                ) {
                                    Text("⚡ Key Discovery", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp))
                                }

                                Button(
                                    onClick = { onStartChatWithPeer(peer) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = KineticLime,
                                        contentColor = Color(0xFF060A10)
                                    )
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Start Chat", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ---------------------------------------------------------------------
        // 4. Bottom Action Buttons: NFC Bump & Show QR Code
        // ---------------------------------------------------------------------
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    onScanNfc()
                    showLocalNfcDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.2.dp,
                        brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPink, NeonPurple)),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .shadow(elevation = 10.dp, spotColor = NeonPink)
                    .testTag("btn_scan_nfc"),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPink, NeonPurple))),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Contactless,
                            contentDescription = "NFC",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "⚡ SCAN / CONNECT VIA NFC",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(
                onClick = {
                    onShowQrCode()
                    showLocalQrDialog = true
                },
                modifier = Modifier.testTag("btn_show_my_qr")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "QR",
                        tint = NeonCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Show My QR Code",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 12.5.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    // -------------------------------------------------------------------------
    // NFC Bump Dialog
    // -------------------------------------------------------------------------
    if (showLocalNfcDialog) {
        Dialog(onDismissRequest = { showLocalNfcDialog = false }) {
            BangNfcBumpModal(
                onConnectSuccess = {
                    showLocalNfcDialog = false
                    val alexPeer = uiState.meshPeers.find { it.displayName.contains("Alex Rivera") }
                    if (alexPeer != null) {
                        onSelectPeer(alexPeer)
                        onOpenDirectDiscoveryModal(alexPeer)
                    }
                },
                onDismiss = { showLocalNfcDialog = false }
            )
        }
    }

    // -------------------------------------------------------------------------
    // QR Code Display Dialog
    // -------------------------------------------------------------------------
    if (showLocalQrDialog) {
        Dialog(onDismissRequest = { showLocalQrDialog = false }) {
            BangQrIdentityModal(
                nodeId = uiState.identity.nodeId,
                handle = "@atlas_01",
                onDismiss = { showLocalQrDialog = false }
            )
        }
    }
}

/**
 * High-Fidelity NFC Bump Key-Exchange Modal
 */
@Composable
fun BangNfcBumpModal(
    onConnectSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var isSimulatingBump by remember { mutableStateOf(false) }
    var bumpCompleted by remember { mutableStateOf(false) }

    val infinitePulse = rememberInfiniteTransition(label = "nfcPulse")
    val pulseSize by infinitePulse.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nfcPulseSize"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPink)),
                shape = RoundedCornerShape(20.dp)
            )
            .shadow(elevation = 16.dp, spotColor = NeonCyan)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NFC ZERO-KNOWLEDGE BUMP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = NeonCyan,
                        fontSize = 10.5.sp,
                        letterSpacing = 0.8.sp
                    )
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(22.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMedium, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pulsing NFC Antenna Icon Graphic
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(NeonPink.copy(alpha = 0.15f))
                    .border(1.2.dp, NeonCyan.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size((52 * pulseSize).dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.1f))
                )
                Icon(
                    imageVector = if (bumpCompleted) Icons.Default.CheckCircle else Icons.Default.Contactless,
                    contentDescription = null,
                    tint = if (bumpCompleted) KineticLime else NeonPink,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (bumpCompleted) "NFC Identity Verified!" else "Hold Phones Back-to-Back",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = TextHigh,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = if (bumpCompleted)
                    "Curve25519 public key & Noise XK handshake parameters verified. Added to trusted mesh contacts."
                else
                    "Bring two BANG devices together. Device hardware performs zero-knowledge out-of-band key exchange without cellular or Wi-Fi.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMedium,
                    textAlign = TextAlign.Center,
                    fontSize = 10.5.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isSimulatingBump = true
                    bumpCompleted = true
                    onConnectSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KineticLime,
                    contentColor = Color(0xFF060A10)
                )
            ) {
                Text(
                    text = "⚡ Simulate NFC Bump (@alex_rivera)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp
                    )
                )
            }
        }
    }
}

/**
 * High-Fidelity Stylized QR Identity Matrix Modal
 */
@Composable
fun BangQrIdentityModal(
    nodeId: String,
    handle: String,
    onDismiss: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(listOf(KineticLime, NeonCyan)),
                shape = RoundedCornerShape(20.dp)
            )
            .shadow(elevation = 16.dp, spotColor = KineticLime)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MESH IDENTITY QR CODE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = KineticLime,
                        fontSize = 10.5.sp,
                        letterSpacing = 0.8.sp
                    )
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(22.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMedium, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stylized QR Matrix Canvas with Finder Patterns in Kinetic Lime & Cyan
            Box(
                modifier = Modifier
                    .size(175.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF040608))
                    .border(0.8.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cols = 17
                    val cellSize = size.width / cols

                    // 1. Draw top-left finder pattern
                    drawFinderPattern(0f, 0f, cellSize)
                    // 2. Draw top-right finder pattern
                    drawFinderPattern((cols - 5) * cellSize, 0f, cellSize)
                    // 3. Draw bottom-left finder pattern
                    drawFinderPattern(0f, (cols - 5) * cellSize, cellSize)

                    // 4. Fill simulated data blocks
                    for (r in 0 until cols) {
                        for (c in 0 until cols) {
                            val inTL = r < 5 && c < 5
                            val inTR = r < 5 && c >= cols - 5
                            val inBL = r >= cols - 5 && c < 5

                            if (!inTL && !inTR && !inBL) {
                                if ((r * 7 + c * 13 + (r * c)) % 3 != 0) {
                                    val blockColor = if ((r + c) % 2 == 0) KineticLime else NeonCyan
                                    drawRect(
                                        color = blockColor,
                                        topLeft = Offset(c * cellSize + 1f, r * cellSize + 1f),
                                        size = Size(cellSize - 2f, cellSize - 2f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$handle (Your Node)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextHigh,
                    fontSize = 14.sp
                )
            )

            Text(
                text = "Node ID: $nodeId • Curve25519 PK",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    color = NeonCyan
                )
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Scan with another BANG camera to exchange public keys out-of-band and verify peer without cellular or internet.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMedium,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color(0xFF060A10)
                )
            ) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black)
                )
            }
        }
    }
}

private fun DrawScope.drawFinderPattern(
    x: Float,
    y: Float,
    cellSize: Float
) {
    val size = cellSize * 5
    drawRect(
        color = KineticLime,
        topLeft = Offset(x, y),
        size = Size(size, size),
        style = Stroke(width = cellSize)
    )
    drawRect(
        color = NeonCyan,
        topLeft = Offset(x + cellSize * 1.5f, y + cellSize * 1.5f),
        size = Size(cellSize * 2f, cellSize * 2f)
    )
}

/**
 * Dynamic Mesh Topology Radar View (Jetpack Compose).
 * Real-time peer visualization component.
 */
@Composable
fun MeshRadarView(
    activePeersCount: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Canvas(modifier = modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2

        drawCircle(Color(0xFF1E293B), radius = radius, style = Stroke(width = 2f))
        drawCircle(Color(0xFF1E293B), radius = radius * 0.6f, style = Stroke(width = 2f))
        drawCircle(Color(0xFF1E293B), radius = radius * 0.3f, style = Stroke(width = 1.5f))

        val rad = Math.toRadians(angle.toDouble())
        val sweepEnd = Offset(
            x = center.x + radius * cos(rad).toFloat(),
            y = center.y + radius * sin(rad).toFloat()
        )
        drawLine(
            color = NeonCyan,
            start = center,
            end = sweepEnd,
            strokeWidth = 2.5f
        )

        val sampleOffsets = listOf(
            Offset(center.x + 40f, center.y - 50f),
            Offset(center.x - 55f, center.y + 35f),
            Offset(center.x + 65f, center.y + 45f),
            Offset(center.x - 30f, center.y - 65f),
            Offset(center.x + 20f, center.y + 70f),
            Offset(center.x - 70f, center.y - 20f)
        )

        val displayCount = activePeersCount.coerceIn(1, sampleOffsets.size)
        for (i in 0 until displayCount) {
            val pt = sampleOffsets[i]
            drawCircle(color = NeonCyan.copy(alpha = 0.2f), radius = 9.dp.toPx(), center = pt)
            drawCircle(color = NeonCyan, radius = 4.5.dp.toPx(), center = pt)
        }

        drawCircle(color = KineticLime, radius = 5.5.dp.toPx(), center = center)
    }
}
