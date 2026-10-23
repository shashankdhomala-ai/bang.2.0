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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SosBeacon
import com.example.ui.BangUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// -----------------------------------------------------------------------------
// Cyber-Obsidian Design Tokens
// -----------------------------------------------------------------------------
private val ObsidianBg = Color(0xFF080C14)
private val CardSurface = Color(0xFF0F1724)
private val SurfaceVariant = Color(0xFF131D2E)
private val BorderMuted = Color(0xFF1E2B3E)
private val NeonCrimson = Color(0xFFFF1744)
private val NeonCyan = Color(0xFF00E5FF)
private val KineticLime = Color(0xFF7FFF00)
private val NeonAmber = Color(0xFFFFB800)
private val TextHigh = Color(0xFFF1F5F9)
private val TextMedium = Color(0xFF94A3B8)
private val TextLow = Color(0xFF475569)

@Composable
fun SosCenterScreen(
    uiState: BangUiState,
    onTriggerSos: () -> Unit,
    onCancelSosCountdown: () -> Unit,
    onCancelActiveSos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sosPulse")
    val sosPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sosPulseScale"
    )

    val rippleProgress1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sosRipple1"
    )

    val rippleProgress2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sosRipple2"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("sos_center_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ---------------------------------------------------------------------
        // 1. Streamlined High-Priority Distress Banner Header
        // ---------------------------------------------------------------------
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardSurface,
                border = BorderStroke(0.8.dp, NeonCrimson.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(NeonCrimson.copy(alpha = 0.18f))
                            .border(0.8.dp, NeonCrimson, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Distress Alert",
                            tint = NeonCrimson,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "EMERGENCY SOS CENTER",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.1.sp,
                                    fontSize = 13.5.sp
                                ),
                                color = TextHigh
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonCrimson.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "PRIORITY 1",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = NeonCrimson
                                    )
                                )
                            }
                        }

                        Text(
                            text = "Unblockable flood relay across physical BLE & Wi-Fi Direct antennas.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.5.sp,
                                color = TextMedium
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // Active SOS Beacon (if broadcasting) OR 3-Second Hold Circular Trigger
        // ---------------------------------------------------------------------
        uiState.activeSosBeacon?.let { beacon ->
            item {
                ActiveSosBroadcastCard(
                    beacon = beacon,
                    onCancel = onCancelActiveSos,
                    pulseScale = sosPulseScale
                )
            }
        } ?: run {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(0.8.dp, BorderMuted),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DISTRESS BEACON TRIGGER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.sp
                            ),
                            color = TextMedium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3-Second Hold Circular Trigger with Animated Radar Ripples
                        Box(
                            modifier = Modifier
                                .size(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Animated Radar Ripples
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val maxR = size.width / 2f

                                val r1 = maxR * rippleProgress1
                                val alpha1 = (1f - rippleProgress1).coerceIn(0f, 0.45f)
                                drawCircle(
                                    color = NeonCrimson.copy(alpha = alpha1),
                                    radius = r1,
                                    center = center,
                                    style = Stroke(width = 1.2.dp.toPx())
                                )

                                val r2 = maxR * rippleProgress2
                                val alpha2 = (1f - rippleProgress2).coerceIn(0f, 0.4f)
                                drawCircle(
                                    color = NeonCrimson.copy(alpha = alpha2),
                                    radius = r2,
                                    center = center,
                                    style = Stroke(width = 1.2.dp.toPx())
                                )
                            }

                            // Circular Hold Button in Neon Crimson (#FF1744)
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .scale(if (uiState.isSosCountingDown) sosPulseScale else 1f)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(NeonCrimson, Color(0xFFC00C2E))
                                        )
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = if (uiState.isSosCountingDown) Color.White else NeonCrimson.copy(alpha = 0.8f),
                                        shape = CircleShape
                                    )
                                    .shadow(elevation = 14.dp, spotColor = NeonCrimson, shape = CircleShape)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                onTriggerSos()
                                                val released = tryAwaitRelease()
                                                if (!released) {
                                                    onCancelSosCountdown()
                                                }
                                            }
                                        )
                                    }
                                    .clickable { onTriggerSos() }
                                    .testTag("trigger_sos_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "SOS",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (uiState.isSosCountingDown) "${uiState.sosCountdownSeconds}s" else "HOLD SOS",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = if (uiState.isSosCountingDown) 20.sp else 11.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.isSosCountingDown) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonCrimson.copy(alpha = 0.15f))
                                    .border(0.8.dp, NeonCrimson.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "HOLDING TO BROADCAST (${uiState.sosCountdownSeconds}s) • RELEASE CANCELS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = NeonCrimson
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = onCancelSosCountdown,
                                    modifier = Modifier.height(26.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextHigh),
                                    border = BorderStroke(0.8.dp, BorderMuted),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text("Abort", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
                                }
                            }
                        } else {
                            Text(
                                text = "Intentional 3-second hold circular trigger prevents accidental misfires.\nTransmits live GPS fix & battery state across all 7 hops.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = TextMedium,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // 2. Compact Telemetry Cards (GPS Coordinates, Battery, TTL Hop Quota)
        // ---------------------------------------------------------------------
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. GPS Coordinates
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardSurface,
                    border = BorderStroke(0.8.dp, BorderMuted),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "GPS FIX",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextLow
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "37.7749° N",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextHigh
                            )
                        )
                        Text(
                            text = "122.4194° W",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextMedium
                            )
                        )
                    }
                }

                // 2. Battery Level
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardSurface,
                    border = BorderStroke(0.8.dp, BorderMuted),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BatteryFull,
                                contentDescription = null,
                                tint = KineticLime,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "BATTERY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextLow
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "87%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = KineticLime
                            )
                        )
                        Text(
                            text = "Opt Duty Cycle",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 8.5.sp,
                                color = TextMedium
                            )
                        )
                    }
                }

                // 3. TTL Hop Quota
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardSurface,
                    border = BorderStroke(0.8.dp, BorderMuted),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "HOP QUOTA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextLow
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "7 Hops Max",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = NeonAmber
                            )
                        )
                        Text(
                            text = "Mesh TTL Flood",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 8.5.sp,
                                color = TextMedium
                            )
                        )
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // 3. Incoming Distress Calls Feed
        // ---------------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INCOMING DISTRESS ALERTS (${uiState.incomingSosBeacons.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    ),
                    color = NeonCrimson
                )

                Text(
                    text = "HARDWARE RELAY ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        color = KineticLime
                    )
                )
            }
        }

        items(uiState.incomingSosBeacons, key = { it.id }) { beacon ->
            IncomingSosCard(beacon = beacon)
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ActiveSosBroadcastCard(
    beacon: SosBeacon,
    onCancel: () -> Unit,
    pulseScale: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NeonCrimson.copy(alpha = 0.12f)),
        border = BorderStroke(1.2.dp, NeonCrimson),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(NeonCrimson)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "EMERGENCY BEACON ACTIVE",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.5.sp
                        ),
                        color = NeonCrimson
                    )
                }

                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CardSurface,
                        contentColor = NeonCrimson
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.8.dp, NeonCrimson),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Deactivate", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = beacon.statusText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp
                ),
                color = TextHigh
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stats Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardSurface)
                    .border(0.5.dp, BorderMuted, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "REPLICATED HOPS",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontFamily = FontFamily.Monospace),
                        color = TextMedium
                    )
                    Text(
                        text = "${beacon.hopsReplicated} Hops (${beacon.hopsReplicated * 3} Nodes)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            color = KineticLime
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "BROADCAST TIME",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontFamily = FontFamily.Monospace),
                        color = TextMedium
                    )
                    Text(
                        text = formatTimestamp(beacon.timestamp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            color = TextHigh
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun IncomingSosCard(beacon: SosBeacon) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(0.8.dp, NeonCrimson.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = NeonCrimson,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = beacon.senderName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = TextHigh
                    )
                }

                Text(
                    text = "${beacon.hopsReplicated} Hops Away",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = NeonAmber
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = beacon.statusText,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                color = TextHigh
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GPS: ${beacon.latitude}, ${beacon.longitude}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.5.sp
                    ),
                    color = NeonCyan
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = KineticLime,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Relaying across mesh",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            color = KineticLime
                        )
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(millis: Long): String {
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formatter.format(Date(millis))
}
