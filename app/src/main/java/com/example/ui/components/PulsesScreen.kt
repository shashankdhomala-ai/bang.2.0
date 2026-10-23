package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.MeshPulseStory
import com.example.ui.BangUiState
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.CyberElectricCyan
import com.example.ui.theme.CyberHyperPink
import com.example.ui.theme.CyberKineticEmerald
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberSlateBg
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

/**
 * Dedicated Screen for "Pulses" in BANG (Navigation Tab 1 - ⚡ Pulses)
 *
 * Implements:
 * 1. Visual Stories / Moments: Photos, audio, and text updates with auto 24-hour expiration.
 * 2. Cloud vs. Offline Mesh distinction:
 *    - 🌐 Cloud Pulses: Sent over cellular/Wi-Fi to contacts (Gradient Magenta Rings).
 *    - ⚡ Mesh Pulses (Offline): Broadcasted locally over BLE to devices within ~100m without cell/internet (Glowing Amber Rings).
 * 3. My Pulse Heartbeat card & New Pulse composer modal.
 * 4. Interactive Pulse cards with 1-tap view in MeshStoryViewerModal and lightweight RF Heartbeat Ping.
 */
@Composable
fun PulsesScreen(
    uiState: BangUiState,
    onOpenStory: (MeshPulseStory) -> Unit,
    onBroadcastPulse: (caption: String, isLocalMesh: Boolean) -> Unit,
    onShowToast: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") } // "All", "⚡ Mesh (BLE)", "🌐 Cloud"
    var showCreatePulseDialog by remember { mutableStateOf(false) }

    val filteredPulses = remember(uiState.meshStories, selectedFilter) {
        when (selectedFilter) {
            "⚡ Mesh (BLE)" -> uiState.meshStories.filter { it.isLocalMesh }
            "🌐 Cloud" -> uiState.meshStories.filter { !it.isLocalMesh }
            else -> uiState.meshStories
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseRingAnim")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberSlateBg)
            .testTag("pulses_screen")
    ) {
        // =====================================================================
        // 1. TOP HEADER & TELEMETRY
        // =====================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⚡ PULSES",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = CyberElectricCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF261224))
                                .border(1.dp, CyberHyperPink.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "24H EPHEMERAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberHyperPink
                                )
                            )
                        }
                    }
                    Text(
                        text = "Broadcast your heartbeat signal & offline moments",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = CyberTextSecondary
                        )
                    )
                }

                // New Pulse Floating Trigger Action
                Button(
                    onClick = { showCreatePulseDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberHyperPink,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("btn_broadcast_pulse_trigger")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "NEW PULSE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterOptions = listOf("All", "⚡ Mesh (BLE)", "🌐 Cloud")
                filterOptions.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    val chipBg = if (isSelected) Color(0xFF162338) else CyberCardSurface
                    val chipBorder = if (isSelected) CyberElectricCyan else CyberCardBorder
                    val textColor = if (isSelected) CyberElectricCyan else CyberTextSecondary

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(chipBg)
                            .border(1.dp, chipBorder, RoundedCornerShape(16.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                            .testTag("pulse_filter_$filter"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.5.sp,
                                color = textColor
                            )
                        )
                    }
                }
            }
        }

        // =====================================================================
        // 2. MAIN SCROLLABLE CONTENT
        // =====================================================================
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // MY ACTIVE PULSE / HEARTBEAT CARD
            item {
                MyPulseHeartbeatCard(
                    myPulse = uiState.myActivePulse,
                    pulseGlow = pulseGlow,
                    onCreatePulse = { showCreatePulseDialog = true },
                    onViewMyPulse = {
                        uiState.myActivePulse?.let { onOpenStory(it) } ?: run { showCreatePulseDialog = true }
                    }
                )
            }

            // HORIZONTAL PULSE STORY RINGS CAROUSEL
            item {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "RECENT MOMENTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = CyberTextMuted,
                            letterSpacing = 0.8.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(uiState.meshStories, key = { it.id }) { story ->
                            PulseStoryRingItem(
                                story = story,
                                onClick = { onOpenStory(story) }
                            )
                        }
                    }
                }
            }

            // SECTION TITLE: MESH VS CLOUD
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (selectedFilter) {
                            "⚡ Mesh (BLE)" -> "OFFLINE MESH PULSES (NEARBY ~100M)"
                            "🌐 Cloud" -> "CLOUD PULSES (ADDED CONTACTS)"
                            else -> "ALL BROADCAST SIGNALS & MOMENTS"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.5.sp,
                            color = CyberElectricCyan,
                            letterSpacing = 0.8.sp
                        )
                    )

                    Text(
                        text = "${filteredPulses.size} Active",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = CyberTextMuted
                        )
                    )
                }
            }

            // DETAILED PULSE MOMENT CARDS
            items(filteredPulses, key = { it.id }) { story ->
                PulseDetailedCard(
                    story = story,
                    onOpenStory = { onOpenStory(story) },
                    onSendHeartbeatPing = {
                        onShowToast("⚡ Sent Heartbeat Ping to ${story.authorName} over BLE Mesh")
                    }
                )
            }

            // BOTTOM INFO NOTICE
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF090E17)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = CyberNeonAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "How Pulses Work in BANG",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CyberTextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• ⚡ Mesh Pulses broadcast offline via BLE to devices physically near you (~100m) without internet.\n• 🌐 Cloud Pulses sync over cellular/Wi-Fi to your added contacts.\n• All Pulses automatically expire after 24 hours.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.5.sp,
                                color = CyberTextSecondary,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // =========================================================================
    // MODAL DIALOG: BROADCAST NEW PULSE COMPOSER
    // =========================================================================
    if (showCreatePulseDialog) {
        CreatePulseDialog(
            onDismiss = { showCreatePulseDialog = false },
            onBroadcast = { text, isLocal ->
                showCreatePulseDialog = false
                onBroadcastPulse(text, isLocal)
                onShowToast(
                    if (isLocal) "⚡ Broadcasted Mesh Pulse to nearby devices within 100m!"
                    else "🌐 Synced Cloud Pulse to added contacts!"
                )
            }
        )
    }
}

/**
 * My Pulse Active Signal / Heartbeat Card
 */
@Composable
private fun MyPulseHeartbeatCard(
    myPulse: MeshPulseStory?,
    pulseGlow: Float,
    onCreatePulse: () -> Unit,
    onViewMyPulse: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberElectricCyan.copy(alpha = pulseGlow)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("my_pulse_heartbeat_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pulsing User Avatar Ring
                    Box(
                        modifier = Modifier.size(46.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = CyberElectricCyan.copy(alpha = 0.3f * pulseGlow),
                                radius = size.width / 2,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(CyberElectricCyan, CyberHyperPink))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AT",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "YOUR PULSE HEARTBEAT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp,
                                    color = CyberElectricCyan,
                                    letterSpacing = 0.8.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(CyberKineticEmerald)
                            )
                        }

                        Text(
                            text = if (myPulse != null) "Broadcasting Signal" else "No Active Pulse",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = CyberTextPrimary
                            )
                        )
                    }
                }

                // 24H TTL Pill Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF162338))
                        .border(1.dp, CyberElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = null,
                            tint = CyberElectricCyan,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (myPulse != null) "TTL 23h 48m" else "24H EXPIRY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberElectricCyan
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Caption text or placeholder
            Text(
                text = myPulse?.caption ?: "Broadcasting your heartbeat lets nearby peers and cloud contacts see your active status and coordinates.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    color = CyberTextSecondary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (myPulse != null) {
                    Button(
                        onClick = onViewMyPulse,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF162338),
                            contentColor = CyberElectricCyan
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Text("VIEW ACTIVE PULSE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onCreatePulse,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberElectricCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("btn_broadcast_new_pulse_card")
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (myPulse != null) "UPDATE SIGNAL" else "BROADCAST PULSE",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

/**
 * Story Ring Avatar for Pulses Carousel
 */
@Composable
private fun PulseStoryRingItem(
    story: MeshPulseStory,
    onClick: () -> Unit
) {
    val ringBrush = if (story.isLocalMesh) {
        // Glowing Amber Ring (BLE ~100m offline pulse)
        Brush.sweepGradient(listOf(CyberNeonAmber, Color(0xFFFF6D00), CyberNeonAmber))
    } else {
        // Gradient Magenta Ring (Cloud contacts)
        Brush.sweepGradient(listOf(CyberHyperPink, Color(0xFF9D00FF), CyberHyperPink))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("carousel_pulse_${story.id}")
    ) {
        Box(
            modifier = Modifier.size(58.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(ringBrush)
                    .padding(2.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF0F1722))
                        .border(1.5.dp, CyberSlateBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = story.avatarInitials,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (story.isLocalMesh) CyberNeonAmber else CyberHyperPink
                        )
                    )
                }
            }

            // Transport type mini badge
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (story.isLocalMesh) CyberNeonAmber else CyberHyperPink)
                    .border(1.5.dp, CyberSlateBg, CircleShape)
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (story.isLocalMesh) Icons.Default.Bolt else Icons.Default.Cloud,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = story.authorName.split(" ").firstOrNull() ?: story.authorName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = CyberTextPrimary,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Detailed Pulse Card Component in Feed
 */
@Composable
private fun PulseDetailedCard(
    story: MeshPulseStory,
    onOpenStory: () -> Unit,
    onSendHeartbeatPing: () -> Unit
) {
    val isLocal = story.isLocalMesh
    val borderColor = if (isLocal) CyberNeonAmber.copy(alpha = 0.6f) else CyberHyperPink.copy(alpha = 0.5f)
    val accentColor = if (isLocal) CyberNeonAmber else CyberHyperPink

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenStory() }
            .testTag("pulse_card_${story.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Card Top Row: Author, Badges & Expire Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F1722))
                            .border(1.8.dp, accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = story.avatarInitials,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = accentColor
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = story.authorName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = CyberTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(CyberKineticEmerald)
                            )
                        }
                        Text(
                            text = "${story.authorHandle} • ${story.timestampText}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = CyberTextMuted
                            )
                        )
                    }
                }

                // Transport Type Badge (BLE ~100m vs Cloud)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isLocal) Color(0xFF261D09) else Color(0xFF261224))
                        .border(0.8.dp, accentColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isLocal) "⚡ BLE 100M MESH" else "🌐 CLOUD SYNC",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = accentColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Caption Moment Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF090D15))
                    .border(0.8.dp, CyberCardBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = story.caption,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.5.sp,
                        color = CyberTextPrimary,
                        lineHeight = 17.sp
                    )
                )
            }

            // Optional Interactive Element Indicator (Poll, Voice Note)
            if (story.pollQuestion != null || story.hasVoiceNote) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (story.pollQuestion != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF162338))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Poll, contentDescription = null, tint = CyberElectricCyan, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Interactive Poll",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberElectricCyan
                                    )
                                )
                            }
                        }
                    }

                    if (story.hasVoiceNote) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF261E0A))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = CyberNeonAmber, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Opus Audio (${story.voiceDurationSec}s)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberNeonAmber
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HourglassBottom,
                        contentDescription = null,
                        tint = CyberTextMuted,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Disappears in 24h",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = CyberTextMuted
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // RF Heartbeat Ping
                    IconButton(
                        onClick = onSendHeartbeatPing,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Ping Heartbeat",
                            tint = CyberHyperPink,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Open Story Viewer
                    Button(
                        onClick = onOpenStory,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF162338),
                            contentColor = CyberElectricCyan
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("VIEW MOMENT", fontSize = 9.5.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

/**
 * Broadcast New Pulse Composer Dialog
 */
@Composable
private fun CreatePulseDialog(
    onDismiss: () -> Unit,
    onBroadcast: (caption: String, isLocalMesh: Boolean) -> Unit
) {
    var pulseText by remember { mutableStateOf("") }
    var isLocalMeshMode by remember { mutableStateOf(true) } // true = Mesh BLE 100m, false = Cloud

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSlateBg),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberHyperPink),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("create_pulse_dialog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BROADCAST PULSE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = CyberHyperPink,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF261224))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "24H",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberHyperPink
                                )
                            }
                        }
                        Text(
                            text = "Ephemeral signal broadcast to your network",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = CyberTextMuted
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = CyberTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Transport Mode Selector (Mesh vs Cloud)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Option 1: Mesh Pulse (Offline BLE 100m)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isLocalMeshMode) Color(0xFF261E0A) else CyberCardSurface)
                            .border(
                                1.dp,
                                if (isLocalMeshMode) CyberNeonAmber else CyberCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { isLocalMeshMode = true }
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = if (isLocalMeshMode) CyberNeonAmber else CyberTextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "⚡ Mesh (BLE)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (isLocalMeshMode) CyberNeonAmber else CyberTextSecondary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Broadcast to ~100m offline (no internet)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 8.5.sp,
                                    color = CyberTextMuted
                                )
                            )
                        }
                    }

                    // Option 2: Cloud Pulse (Internet / Contacts)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isLocalMeshMode) Color(0xFF261224) else CyberCardSurface)
                            .border(
                                1.dp,
                                if (!isLocalMeshMode) CyberHyperPink else CyberCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { isLocalMeshMode = false }
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Cloud,
                                    contentDescription = null,
                                    tint = if (!isLocalMeshMode) CyberHyperPink else CyberTextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "🌐 Cloud Sync",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (!isLocalMeshMode) CyberHyperPink else CyberTextSecondary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "E2EE sync to all contacts via Wi-Fi/Cellular",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 8.5.sp,
                                    color = CyberTextMuted
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Caption Text Field
                OutlinedTextField(
                    value = pulseText,
                    onValueChange = { pulseText = it },
                    placeholder = {
                        Text(
                            text = if (isLocalMeshMode)
                                "What's happening nearby? (e.g. Signal good at Gate 2, coffee ready!)"
                            else
                                "Share a moment with your contacts…",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = CyberTextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("input_pulse_caption"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberCardSurface,
                        unfocusedContainerColor = CyberCardSurface,
                        focusedBorderColor = if (isLocalMeshMode) CyberNeonAmber else CyberHyperPink,
                        unfocusedBorderColor = CyberCardBorder,
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Status Template Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val templates = listOf(
                        "⚡ Active & Relaying Packets",
                        "☕ Coffee Stop Beacon",
                        "🔋 Power Generator Online",
                        "📍 Field Ops Station 1",
                        "📶 Strong LoRa Signal"
                    )
                    templates.forEach { t ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF131D2A))
                                .clickable { pulseText = t }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = t,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    color = CyberElectricCyan
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Broadcast CTA
                Button(
                    onClick = {
                        val text = pulseText.ifBlank { "Signal heartbeat active • Connected to local BANG mesh." }
                        onBroadcast(text, isLocalMeshMode)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLocalMeshMode) CyberNeonAmber else CyberHyperPink,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_submit_pulse_broadcast")
                ) {
                    Icon(
                        imageVector = if (isLocalMeshMode) Icons.Default.Bolt else Icons.Default.Cloud,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLocalMeshMode) "BROADCAST MESH PULSE (BLE 100M)" else "SEND CLOUD PULSE (E2EE)",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
