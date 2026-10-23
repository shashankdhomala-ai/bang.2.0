package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MeshPeerNode
import com.example.ui.BangUiState
import com.example.ui.theme.BangCharcoalBorder
import com.example.ui.theme.BangCharcoalSurface
import com.example.ui.theme.BangElectricKineticLime
import com.example.ui.theme.BangObsidianBg
import com.example.ui.theme.BangStatusOfflineMesh
import com.example.ui.theme.BangStatusOnline
import kotlin.math.cos
import kotlin.math.sin

/**
 * Screen 5: Mesh Network & Concentric Radar Screen
 * Matches Phone 5 of the production design layout.
 */
@Composable
fun MeshNetworkScreen(
    uiState: BangUiState,
    onBack: (() -> Unit)? = null,
    onOpenSettings: () -> Unit = {},
    onSelectPeer: (MeshPeerNode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedConnectionMode by remember { mutableStateOf("Bluetooth") } // Bluetooth, Wi-Fi Direct, Mesh Relay

    // Animated radar sweep
    val infiniteTransition = rememberInfiniteTransition(label = "radarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    val rippleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleProgress"
    )

    val displayDevices = remember(uiState.meshPeers) {
        listOf(
            MeshDeviceRow(id = "alex-1", name = "Alex", distance = "2m", status = "Online", statusColor = BangStatusOnline, signalBars = 4),
            MeshDeviceRow(id = "rohan-2", name = "Rohan", distance = "5m", status = "Online", statusColor = BangStatusOnline, signalBars = 4),
            MeshDeviceRow(id = "sarah-3", name = "Sarah", distance = "12m", status = "Relaying", statusColor = BangStatusOfflineMesh, signalBars = 3),
            MeshDeviceRow(id = "device-4", name = "Device-04", distance = "18m", status = "Offline", statusColor = Color(0xFF64748B), signalBars = 1)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidianBg)
            .testTag("mesh_network_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                    Text(
                        text = "Mesh Network",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(start = if (onBack != null) 0.dp else 12.dp)
                    )
                }

                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 2. Concentric Radar Visualizer Card
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BangCharcoalSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Radar Canvas
                            Box(
                                modifier = Modifier
                                    .size(230.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF070B11)),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val maxRadius = size.width / 2f * 0.9f

                                    // Expanding ripple
                                    val rippleRadius = maxRadius * rippleProgress
                                    val rippleAlpha = (1f - rippleProgress).coerceIn(0f, 0.4f)
                                    drawCircle(
                                        color = BangElectricKineticLime.copy(alpha = rippleAlpha),
                                        radius = rippleRadius,
                                        center = center,
                                        style = Stroke(width = 1.dp.toPx())
                                    )

                                    // Concentric circles: 10m, 20m, 40m
                                    val rings = listOf(0.33f, 0.66f, 1.0f)
                                    rings.forEach { frac ->
                                        drawCircle(
                                            color = BangElectricKineticLime.copy(alpha = 0.25f),
                                            radius = maxRadius * frac,
                                            center = center,
                                            style = Stroke(
                                                width = 1.dp.toPx(),
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                            )
                                        )
                                    }

                                    // Sweep ray
                                    val rad = Math.toRadians(sweepAngle.toDouble())
                                    val endX = center.x + (maxRadius * cos(rad)).toFloat()
                                    val endY = center.y + (maxRadius * sin(rad)).toFloat()
                                    drawLine(
                                        brush = Brush.radialGradient(
                                            colors = listOf(BangElectricKineticLime, Color.Transparent),
                                            center = center,
                                            radius = maxRadius
                                        ),
                                        start = center,
                                        end = Offset(endX, endY),
                                        strokeWidth = 2.dp.toPx()
                                    )

                                    // Orbiting peer blips
                                    val peerPositions = listOf(
                                        Triple(0.45f, -30.0, BangStatusOnline), // Alex
                                        Triple(0.70f, 160.0, BangStatusOnline), // Rohan
                                        Triple(0.85f, 50.0, BangStatusOfflineMesh), // Sarah
                                        Triple(0.92f, -120.0, Color(0xFF64748B))  // Device-04
                                    )
                                    peerPositions.forEach { (distFrac, deg, color) ->
                                        val pRad = Math.toRadians(deg)
                                        val px = center.x + (maxRadius * distFrac * cos(pRad)).toFloat()
                                        val py = center.y + (maxRadius * distFrac * sin(pRad)).toFloat()
                                        drawCircle(color = color.copy(alpha = 0.3f), radius = 9.dp.toPx(), center = Offset(px, py))
                                        drawCircle(color = color, radius = 5.dp.toPx(), center = Offset(px, py))
                                    }
                                }

                                // Center user node: Purple circle with lightning bolt
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF7B2CBF))
                                        .border(2.dp, Color(0xFF9D4EDD), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "My Node",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Counter summary text
                            Text(
                                text = "3 nearby devices",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "2 relaying • 1 offline",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }

                // 3. Nearby Devices Section
                item {
                    Text(
                        text = "Nearby Devices",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                items(displayDevices, key = { it.id }) { dev ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BangCharcoalSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1B2433)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dev.name.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dev.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${dev.distance} • ${dev.status}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = dev.statusColor
                                    )
                                )
                            }

                            // Signal Strength Bars
                            SignalBars(bars = dev.signalBars, activeColor = dev.statusColor)
                        }
                    }
                }
            }

            // 4. Bottom Connection Mode Bar: [ Bluetooth | Wi-Fi Direct | Mesh Relay ]
            Surface(
                color = BangCharcoalSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Connection Mode",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = listOf(
                            Triple("Bluetooth", Icons.Default.Bluetooth, "Bluetooth"),
                            Triple("Wi-Fi Direct", Icons.Default.Wifi, "Wi-Fi Direct"),
                            Triple("Mesh Relay", Icons.Default.Hub, "Mesh Relay")
                        )

                        modes.forEach { (mode, icon, label) ->
                            val isSelected = selectedConnectionMode == mode
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF142718) else Color(0xFF0B1017),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) BangElectricKineticLime else BangCharcoalBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedConnectionMode = mode }
                                    .testTag("conn_mode_$mode")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) BangElectricKineticLime else Color(0xFF64748B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) BangElectricKineticLime else Color(0xFF94A3B8)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class MeshDeviceRow(
    val id: String,
    val name: String,
    val distance: String,
    val status: String,
    val statusColor: Color,
    val signalBars: Int
)

@Composable
private fun SignalBars(bars: Int, activeColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(14.dp)
    ) {
        val heights = listOf(4.dp, 7.dp, 10.dp, 14.dp)
        heights.forEachIndexed { idx, h ->
            val isActive = idx < bars
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(h)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isActive) activeColor else Color(0xFF334155))
            )
        }
    }
}
