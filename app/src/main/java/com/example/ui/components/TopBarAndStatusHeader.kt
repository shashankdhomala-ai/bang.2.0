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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectivityMode
import com.example.model.UserPresenceMode
import com.example.ui.BangUiState

// -----------------------------------------------------------------------------
// Cyber-Obsidian Design Tokens
// -----------------------------------------------------------------------------
private val ObsidianBg = Color(0xFF080C14)
private val CapsuleBg = Color(0xFF0F1724)
private val BorderMuted = Color(0xFF1E2A3C)
private val NeonCyan = Color(0xFF00E5FF)
private val KineticEmerald = Color(0xFF00FF87)
private val NeonAmber = Color(0xFFFFB800)
private val SosRed = Color(0xFFFF1744)
private val TextHigh = Color(0xFFF1F5F9)
private val TextMedium = Color(0xFF94A3B8)
private val TextLow = Color(0xFF475569)

@Composable
fun TopBarAndStatusHeader(
    uiState: BangUiState,
    onModeSelect: (ConnectivityMode) -> Unit,
    onToggleOutage: () -> Unit,
    onDismissAutoSwitch: () -> Unit = {},
    onOpenShowcase: () -> Unit = {},
    onOpenAiArchitecture: () -> Unit = {},
    onOpenAiAssistant: () -> Unit = {},
    onOpenMeshInnovations: () -> Unit = {},
    selectedFilter: String = "All",
    onFilterChange: (String) -> Unit = {},
    onSelectPresenceMode: (UserPresenceMode) -> Unit = {},
    onToggleAutoPresence: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val isOutage = uiState.isSimulatedOutage
    val peerCount = uiState.meshPeers.size.coerceAtLeast(8)

    val (networkStatusText, networkStatusColor) = when {
        isOutage -> Pair("🚨 BLACKOUT", SosRed)
        uiState.connectivityMode == ConnectivityMode.OFFLINE_MESH -> Pair("⚡ $peerCount Peers", KineticEmerald)
        uiState.connectivityMode == ConnectivityMode.CLOUD_ONLINE -> Pair("🌐 Cloud", NeonCyan)
        uiState.connectivityMode == ConnectivityMode.WIFI_DIRECT_P2P -> Pair("📶 P2P Active", NeonAmber)
        else -> Pair("⚡ $peerCount Peers", NeonCyan)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianBg)
            .statusBarsPadding()
            .testTag("bang_top_header")
    ) {
        // ---------------------------------------------------------------------
        // Row 1: The Main Cyber Capsule Header (~46dp)
        // ---------------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Clean Cyber Logo with App Logo Thumbnail
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showModeDialog = true }
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.app_logo),
                    contentDescription = "BANG App Logo",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(0.8.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "B",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = NeonCyan
                )
                Text(
                    text = "ANG",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = TextHigh
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(KineticEmerald)
                )
            }

            // Center: Interactive Dual Cyber Capsule Pill (Network State + Status)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CapsuleBg,
                border = BorderStroke(
                    0.9.dp,
                    if (isOutage) SosRed.copy(alpha = 0.8f) else networkStatusColor.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { showModeDialog = true }
                    .testTag("network_status_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pulsing Status Dot
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(networkStatusColor.copy(alpha = pulseAlpha))
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    // Transport State Tag
                    Text(
                        text = networkStatusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = networkStatusColor
                        )
                    )

                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(10.dp)
                            .background(BorderMuted)
                    )
                    Spacer(modifier = Modifier.width(5.dp))

                    // User Presence Emoji Indicator
                    Text(
                        text = uiState.userPresenceMode.emoji,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = uiState.userPresenceMode.title.split(" ").first(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 9.5.sp,
                            color = TextMedium
                        )
                    )
                }
            }

            // Right: Contextual Quick Actions (AI Assistant + Dropdown Menu)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenAiAssistant,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("topbar_ai_assistant_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "AI Assistant",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("topbar_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextMedium,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .background(Color(0xFF0F1726))
                            .border(1.dp, BorderMuted, RoundedCornerShape(8.dp))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "⚡ BANG AI Assistant",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            },
                            onClick = {
                                showMenu = false
                                onOpenAiAssistant()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "UI Showcase",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextHigh)
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Palette, null, tint = NeonCyan, modifier = Modifier.size(15.dp))
                            },
                            onClick = {
                                showMenu = false
                                onOpenShowcase()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "On-Device AI Architecture",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextHigh)
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Psychology, null, tint = NeonCyan, modifier = Modifier.size(15.dp))
                            },
                            onClick = {
                                showMenu = false
                                onOpenAiArchitecture()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "⚡ Mesh Innovations & Battery",
                                    style = MaterialTheme.typography.bodySmall.copy(color = NeonCyan, fontWeight = FontWeight.Bold)
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Bolt, null, tint = NeonCyan, modifier = Modifier.size(15.dp))
                            },
                            onClick = {
                                showMenu = false
                                onOpenMeshInnovations()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (isOutage) "End Blackout Mode" else "Simulate Blackout",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isOutage) SosRed else TextHigh
                                    )
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.WifiOff, null, tint = if (isOutage) SosRed else NeonAmber, modifier = Modifier.size(15.dp))
                            },
                            onClick = {
                                showMenu = false
                                onToggleOutage()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Routing & Modes",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextHigh)
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Hub, null, tint = KineticEmerald, modifier = Modifier.size(15.dp))
                            },
                            onClick = {
                                showMenu = false
                                showModeDialog = true
                            }
                        )
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // Row 2: Slim Category Filter Chips (All, Unread, Mesh P2P, Groups)
        // ---------------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val filters = listOf("All", "Unread", "Mesh P2P", "Groups")
            filters.forEach { filter ->
                val isSelected = filter == selectedFilter
                val chipBg = if (isSelected) Color(0xFF131F33) else CapsuleBg
                val chipBorder = if (isSelected) NeonCyan.copy(alpha = 0.6f) else BorderMuted
                val textColor = if (isSelected) NeonCyan else TextMedium

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(chipBg)
                        .border(0.8.dp, chipBorder, RoundedCornerShape(14.dp))
                        .clickable { onFilterChange(filter) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("filter_chip_$filter"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            color = textColor
                        )
                    )
                }
            }
        }

        // ---------------------------------------------------------------------
        // Live Auto-Switch & Blackout Notice Toast
        // ---------------------------------------------------------------------
        AnimatedVisibility(
            visible = uiState.latestAutoSwitch != null || isOutage,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val bannerColor = if (isOutage) SosRed else NeonCyan
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bannerColor.copy(alpha = 0.12f))
                    .border(width = 0.5.dp, color = bannerColor.copy(alpha = 0.35f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = if (isOutage) Icons.Default.WifiOff else Icons.Default.Hub,
                            contentDescription = null,
                            tint = bannerColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOutage)
                                "🚨 Grid Blackout Active: Operating 100% via offline BLE & Wi-Fi Direct mesh."
                            else
                                (uiState.latestAutoSwitch?.reason ?: "Auto-switched transport seamlessly."),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.5.sp,
                                color = TextHigh,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                    }
                    if (!isOutage) {
                        IconButton(onClick = onDismissAutoSwitch, modifier = Modifier.size(18.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMedium, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }

        // Bottom 1px Border Stroke
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.6.dp)
                .background(BorderMuted)
        )
    }

    // -------------------------------------------------------------------------
    // Mode & Presence Selection Modal
    // -------------------------------------------------------------------------
    if (showModeDialog) {
        var dialogTab by remember { mutableStateOf(0) }
        val scrollState = rememberScrollState()

        androidx.compose.ui.window.Dialog(onDismissRequest = { showModeDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1726),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (dialogTab == 0) "User Presence Status" else "Network Routing Modes",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextHigh
                            )
                        )
                        IconButton(onClick = { showModeDialog = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMedium, modifier = Modifier.size(15.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090E17))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (dialogTab == 0) NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { dialogTab = 0 }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PRESENCE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.5.sp,
                                    color = if (dialogTab == 0) NeonCyan else TextMedium
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (dialogTab == 1) NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { dialogTab = 1 }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ROUTING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.5.sp,
                                    color = if (dialogTab == 1) NeonCyan else TextMedium
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (dialogTab == 0) {
                        // TAB 0: USER PRESENCE STATUS
                        Text(
                            text = "Set how your node presence is broadcast to local mesh peers and cloud contacts.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMedium,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Automatic App Presence Switch
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF090E17))
                                .border(0.8.dp, BorderMuted, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "⚡ Automatic App Presence",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextHigh
                                        )
                                    )
                                    Text(
                                        text = "Switches automatically between 🌐 Using App and ⭕ Not Using App.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.5.sp,
                                            color = TextMedium
                                        )
                                    )
                                }
                                Switch(
                                    checked = uiState.isAutoPresenceDetection,
                                    onCheckedChange = { onToggleAutoPresence(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF080C14),
                                        checkedTrackColor = KineticEmerald,
                                        uncheckedThumbColor = TextMedium,
                                        uncheckedTrackColor = CapsuleBg
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "MANUAL STATUS MODES:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TextLow
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        UserPresenceMode.entries.forEach { mode ->
                            val isCurrent = mode == uiState.userPresenceMode
                            val accentColor = when (mode) {
                                UserPresenceMode.USING_APP -> NeonCyan
                                UserPresenceMode.NOT_USING_APP -> TextMedium
                                UserPresenceMode.DO_NOT_DISTURB -> SosRed
                                UserPresenceMode.SLEEP_MODE -> Color(0xFFB388FF)
                                UserPresenceMode.INVISIBLE_MODE -> NeonAmber
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent) Color(0xFF131F33) else Color(0xFF090E17))
                                    .border(0.8.dp, if (isCurrent) accentColor else BorderMuted, RoundedCornerShape(10.dp))
                                    .clickable {
                                        onSelectPresenceMode(mode)
                                        showModeDialog = false
                                    }
                                    .padding(10.dp)
                                    .testTag("dialog_presence_${mode.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = mode.emoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = mode.title,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = if (isCurrent) accentColor else TextHigh
                                                    )
                                                )
                                                if (mode.isAutoManaged) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(NeonCyan.copy(alpha = 0.15f))
                                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                                    ) {
                                                        Text(
                                                            text = "AUTO",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 7.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = NeonCyan
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = mode.description,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = TextMedium,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                    if (isCurrent) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Active",
                                            tint = accentColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    } else {
                        // TAB 1: NETWORK ROUTING MODES
                        Text(
                            text = "Switch between decentralized off-grid mesh and internet cloud pipes.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMedium,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        ConnectivityMode.entries.forEach { mode ->
                            val isCurrent = mode == uiState.connectivityMode
                            val modeAccent = when (mode) {
                                ConnectivityMode.CLOUD_ONLINE -> NeonCyan
                                ConnectivityMode.OFFLINE_MESH -> KineticEmerald
                                ConnectivityMode.HYBRID_AUTO -> NeonCyan
                                ConnectivityMode.WIFI_DIRECT_P2P -> NeonAmber
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent) Color(0xFF131F33) else Color(0xFF090E17))
                                    .border(0.8.dp, if (isCurrent) modeAccent else BorderMuted, RoundedCornerShape(10.dp))
                                    .clickable {
                                        onModeSelect(mode)
                                        showModeDialog = false
                                    }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mode.label,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isCurrent) modeAccent else TextHigh
                                            )
                                        )
                                        Text(
                                            text = mode.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextMedium,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    if (isCurrent) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Active",
                                            tint = modeAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}
