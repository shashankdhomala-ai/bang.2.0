package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BangUiState
import com.example.ui.theme.BangAmberWarning
import com.example.ui.theme.BangBackground
import com.example.ui.theme.BangCyanAccent
import com.example.ui.theme.BangPrimaryNeon
import com.example.ui.theme.BangSosRed
import com.example.ui.theme.BangSurface
import com.example.ui.theme.BangSurfaceBorder
import com.example.ui.theme.BangSurfaceVariant
import com.example.ui.theme.BangTextMuted
import com.example.ui.theme.BangTextPrimary
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import com.example.model.ConnectivityMode
import com.example.model.UserPresenceMode
import com.example.ui.DesignShowcaseMode
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.theme.BangElectricBlue
import com.example.ui.theme.BangEmeraldMesh
import com.example.ui.theme.BangHyperCyan
import com.example.ui.theme.BangNeonPurple
import com.example.ui.theme.BangTextSecondary

@Composable
fun IdentitySecurityScreen(
    uiState: BangUiState,
    onToggleStrangerRelay: () -> Unit = {},
    onOpenRelaySimulator: () -> Unit = {},
    onToggleDozeMode: () -> Unit = {},
    onSimulateSpamFlood: () -> Unit = {},
    onSelectConnectivityMode: (ConnectivityMode) -> Unit = {},
    onOpenShowcaseMode: (DesignShowcaseMode) -> Unit = {},
    onSelectPresenceMode: (UserPresenceMode) -> Unit = {},
    onToggleAutoPresence: (Boolean) -> Unit = {},
    onSimulateIdle: (Boolean) -> Unit = {},
    onPurgeAccountData: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var selectedDutyMode by remember { mutableStateOf("Tactical") }
    var activeLegalDoc by remember { mutableStateOf<LegalDocumentType?>(null) }
    var showPurgeConfirmationDialog by remember { mutableStateOf(false) }
    var showBiometricPanicPrompt by remember { mutableStateOf(false) }
    var purgeSuccessMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BangBackground)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = BangCyanAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SETTINGS & IDENTITY",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        ),
                        color = BangTextPrimary
                    )
                    Text(
                        text = "Network routing modes (Mesh vs. Internet) & cryptographic privacy",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = BangTextSecondary
                    )
                }
            }
        }

        // Section: User Presence & Availability Status Modes (Settings)
        item {
            Text(
                text = "USER PRESENCE & STATUS MODES (SETTINGS)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BangCyanAccent
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_presence_card"),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangCyanAccent.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header with current active mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE STATUS MODE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = BangTextSecondary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.userPresenceMode.emoji,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = uiState.userPresenceMode.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = when (uiState.userPresenceMode) {
                                        UserPresenceMode.USING_APP -> BangCyanAccent
                                        UserPresenceMode.NOT_USING_APP -> BangTextSecondary
                                        UserPresenceMode.DO_NOT_DISTURB -> BangSosRed
                                        UserPresenceMode.SLEEP_MODE -> Color(0xFFB388FF)
                                        UserPresenceMode.INVISIBLE_MODE -> BangAmberWarning
                                    }
                                )
                            }
                        }

                        // Status badge showing auto vs manual
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (uiState.isAutoPresenceDetection) BangPrimaryNeon.copy(alpha = 0.15f)
                                    else Color(0xFF263248)
                                )
                                .border(
                                    1.dp,
                                    if (uiState.isAutoPresenceDetection) BangPrimaryNeon.copy(alpha = 0.6f)
                                    else Color(0xFF3B4860),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (uiState.isAutoPresenceDetection) "⚡ AUTO-PRESENCE ON" else "🔒 MANUAL OVERRIDE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.isAutoPresenceDetection) BangPrimaryNeon else BangTextSecondary
                                )
                            )
                        }
                    }

                    Text(
                        text = uiState.userPresenceMode.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = BangTextSecondary
                    )

                    // Dedicated Toggles for Status Modes (Invisible, DND, Sleep, Auto-Presence)
                    Text(
                        text = "STATUS MODE TOGGLES (USER CONTROL)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = BangCyanAccent
                    )

                    // 1. Invisible Mode Toggle
                    StatusModeToggleCard(
                        emoji = "🔲",
                        title = "Invisible Mode",
                        description = "Cloak BLE 5.2 radio beaconing. Receive encrypted packets without broadcasting presence to nearby mesh nodes.",
                        isChecked = uiState.userPresenceMode == UserPresenceMode.INVISIBLE_MODE,
                        accentColor = BangAmberWarning,
                        tag = "toggle_invisible_mode",
                        onCheckedChange = { checked ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSelectPresenceMode(if (checked) UserPresenceMode.INVISIBLE_MODE else UserPresenceMode.USING_APP)
                        }
                    )

                    // 2. Do Not Disturb Toggle
                    StatusModeToggleCard(
                        emoji = "🔴",
                        title = "Do Not Disturb (DND)",
                        description = "Mute non-urgent notifications & alerts. Critical Emergency SOS broadcasts still alert with high-priority audio tone.",
                        isChecked = uiState.userPresenceMode == UserPresenceMode.DO_NOT_DISTURB,
                        accentColor = BangSosRed,
                        tag = "toggle_dnd_mode",
                        onCheckedChange = { checked ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSelectPresenceMode(if (checked) UserPresenceMode.DO_NOT_DISTURB else UserPresenceMode.USING_APP)
                        }
                    )

                    // 3. Sleep Mode Toggle
                    StatusModeToggleCard(
                        emoji = "🌙",
                        title = "Sleep Mode",
                        description = "Power-save duty cycle. BLE scanning duty-cycled to 90s intervals; store-and-forward relays batched in background.",
                        isChecked = uiState.userPresenceMode == UserPresenceMode.SLEEP_MODE,
                        accentColor = Color(0xFFB388FF),
                        tag = "toggle_sleep_mode",
                        onCheckedChange = { checked ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSelectPresenceMode(if (checked) UserPresenceMode.SLEEP_MODE else UserPresenceMode.USING_APP)
                        }
                    )

                    // 4. Automatic App Presence Toggle
                    StatusModeToggleCard(
                        emoji = "⚡",
                        title = "Automatic App Presence",
                        description = "Automatically switch to 🌐 Using App when active, and ⭕ Not Using App when idle or in background.",
                        isChecked = uiState.isAutoPresenceDetection,
                        accentColor = BangPrimaryNeon,
                        tag = "switch_auto_presence",
                        onCheckedChange = { checked ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToggleAutoPresence(checked)
                        }
                    )

                    Text(
                        text = "QUICK PRESET SELECTOR:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = BangTextSecondary
                    )

                    // The 5 Modes listed
                    UserPresenceMode.entries.forEach { mode ->
                        val isSelected = uiState.userPresenceMode == mode
                        val accentColor = when (mode) {
                            UserPresenceMode.USING_APP -> BangCyanAccent
                            UserPresenceMode.NOT_USING_APP -> BangTextSecondary
                            UserPresenceMode.DO_NOT_DISTURB -> BangSosRed
                            UserPresenceMode.SLEEP_MODE -> Color(0xFFB388FF)
                            UserPresenceMode.INVISIBLE_MODE -> BangAmberWarning
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) BangSurfaceVariant else BangSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) accentColor else BangSurfaceBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectPresenceMode(mode) }
                                .padding(12.dp)
                                .testTag("presence_option_${mode.name.lowercase()}")
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
                                    Text(
                                        text = mode.emoji,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = mode.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) accentColor else BangTextPrimary
                                                )
                                            )
                                            if (mode.isAutoManaged) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(BangCyanAccent.copy(alpha = 0.15f))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = "AUTO",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 7.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = BangCyanAccent
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = mode.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 10.sp,
                                                color = BangTextSecondary
                                            )
                                        )
                                    }
                                }

                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSelectPresenceMode(mode) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = accentColor,
                                        unselectedColor = BangTextMuted
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Network & Routing Mode (Mesh vs. Internet Settings)
        item {
            Text(
                text = "NETWORK & ROUTING MODE (SETTINGS)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BangPrimaryNeon
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangPrimaryNeon.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SELECT OPERATIONAL ROUTING MODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = BangTextSecondary
                    )

                    // Quick Direct Switcher: Mesh vs. Internet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onSelectConnectivityMode(ConnectivityMode.OFFLINE_MESH) },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.connectivityMode == ConnectivityMode.OFFLINE_MESH) BangPrimaryNeon else BangSurface,
                                contentColor = if (uiState.connectivityMode == ConnectivityMode.OFFLINE_MESH) Color.Black else BangTextPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (uiState.connectivityMode == ConnectivityMode.OFFLINE_MESH) BangPrimaryNeon else BangSurfaceBorder
                            )
                        ) {
                            Text(
                                text = "📡 MESH MODE",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Button(
                            onClick = { onSelectConnectivityMode(ConnectivityMode.CLOUD_ONLINE) },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.connectivityMode == ConnectivityMode.CLOUD_ONLINE) Color(0xFF64B5F6) else BangSurface,
                                contentColor = if (uiState.connectivityMode == ConnectivityMode.CLOUD_ONLINE) Color.Black else BangTextPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (uiState.connectivityMode == ConnectivityMode.CLOUD_ONLINE) Color(0xFF64B5F6) else BangSurfaceBorder
                            )
                        ) {
                            Text(
                                text = "🌐 INTERNET MODE",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // 1. Off-Grid Mesh Mode
                    NetworkModeOptionItem(
                        title = "Off-Grid Mesh Mode",
                        badge = "OFFLINE MESH",
                        desc = "100% peer-to-peer. Zero internet or cellular needed. Packets hop via BLE & Wi-Fi Direct through nearby peer phones.",
                        icon = Icons.Default.Hub,
                        isSelected = uiState.connectivityMode == ConnectivityMode.OFFLINE_MESH,
                        accentColor = BangPrimaryNeon,
                        tag = "settings_mode_mesh",
                        onClick = { onSelectConnectivityMode(ConnectivityMode.OFFLINE_MESH) }
                    )

                    // 2. Internet / Cloud Online Mode
                    NetworkModeOptionItem(
                        title = "Internet / Cloud Gateway",
                        badge = "INTERNET ONLINE",
                        desc = "Direct low-latency cloud routing when cellular data (4G/5G) or Wi-Fi internet is accessible. Lowest ping.",
                        icon = Icons.Default.Cloud,
                        isSelected = uiState.connectivityMode == ConnectivityMode.CLOUD_ONLINE,
                        accentColor = Color(0xFF64B5F6),
                        tag = "settings_mode_internet",
                        onClick = { onSelectConnectivityMode(ConnectivityMode.CLOUD_ONLINE) }
                    )

                    // 3. Auto-Adaptive Hybrid Mode
                    NetworkModeOptionItem(
                        title = "Auto-Adaptive Hybrid (Recommended)",
                        badge = "AUTO-HANDOVER",
                        desc = "Intelligent auto-switching: relays via Mesh locally, auto-promotes to Wi-Fi Direct for media, and bridges to Internet when available.",
                        icon = Icons.Default.CellTower,
                        isSelected = uiState.connectivityMode == ConnectivityMode.HYBRID_AUTO,
                        accentColor = BangCyanAccent,
                        tag = "settings_mode_hybrid",
                        onClick = { onSelectConnectivityMode(ConnectivityMode.HYBRID_AUTO) }
                    )

                    // 4. Wi-Fi Direct P2P Mode
                    NetworkModeOptionItem(
                        title = "Wi-Fi Direct P2P Only",
                        badge = "HIGH-SPEED P2P",
                        desc = "High-throughput ad-hoc peer links (up to 250 Mbps) for heavy payload transfers without central routers.",
                        icon = Icons.Default.Wifi,
                        isSelected = uiState.connectivityMode == ConnectivityMode.WIFI_DIRECT_P2P,
                        accentColor = BangAmberWarning,
                        tag = "settings_mode_wifi_direct",
                        onClick = { onSelectConnectivityMode(ConnectivityMode.WIFI_DIRECT_P2P) }
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Status Bar inside card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BangSurface)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ACTIVE: ${uiState.connectivityMode.label.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = when (uiState.connectivityMode) {
                                ConnectivityMode.OFFLINE_MESH -> BangPrimaryNeon
                                ConnectivityMode.CLOUD_ONLINE -> Color(0xFF64B5F6)
                                ConnectivityMode.HYBRID_AUTO -> BangCyanAccent
                                ConnectivityMode.WIFI_DIRECT_P2P -> BangAmberWarning
                            }
                        )
                        Text(
                            text = if (uiState.connectivityMode == ConnectivityMode.OFFLINE_MESH) "Cellular Ignored" else "Gateway Ready",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = BangTextSecondary
                        )
                    }
                }
            }
        }

        // Section: High-Fidelity UI Design & Screen Presentation
        item {
            Text(
                text = "HIGH-FIDELITY UI DESIGN & AUTH SCREENS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BangElectricBlue
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangElectricBlue.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PREVIEW MODERN MESH SCREENS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = BangElectricBlue
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BangElectricBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "4 SCREENS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BangElectricBlue
                                )
                            )
                        }
                    }

                    Text(
                        text = "Obsidian/slate (#0B0E14) dark theme with glowing electric blue, neon purple gradients, emerald mesh indicators, and translucent glassmorphism inputs.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = BangTextSecondary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenShowcaseMode(DesignShowcaseMode.LOGIN) },
                            modifier = Modifier.weight(1f).testTag("btn_open_login_screen"),
                            colors = ButtonDefaults.buttonColors(containerColor = BangSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BangElectricBlue.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Sign-In", fontSize = 11.sp, color = BangElectricBlue, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onOpenShowcaseMode(DesignShowcaseMode.SIGNUP) },
                            modifier = Modifier.weight(1f).testTag("btn_open_signup_screen"),
                            colors = ButtonDefaults.buttonColors(containerColor = BangSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BangNeonPurple.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Sign-Up", fontSize = 11.sp, color = BangNeonPurple, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenShowcaseMode(DesignShowcaseMode.CONTACT_SYNC) },
                            modifier = Modifier.weight(1f).testTag("btn_open_contact_sync"),
                            colors = ButtonDefaults.buttonColors(containerColor = BangSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BangEmeraldMesh.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Contact Sync", fontSize = 10.5.sp, color = BangEmeraldMesh, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onOpenShowcaseMode(DesignShowcaseMode.PEER_DISCOVERY) },
                            modifier = Modifier.weight(1f).testTag("btn_open_peer_discovery"),
                            colors = ButtonDefaults.buttonColors(containerColor = BangSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BangElectricBlue.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Peer Verify", fontSize = 10.5.sp, color = BangElectricBlue, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onOpenShowcaseMode(DesignShowcaseMode.ON_DEVICE_AI_ARCHITECTURE) },
                        modifier = Modifier.fillMaxWidth().testTag("btn_open_ai_architecture"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C1929)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BangHyperCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = BangHyperCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🤖 On-Device AI Architecture (SLM)", fontSize = 11.sp, color = BangHyperCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: Cryptographic Identity Header
        item {
            Text(
                text = "CRYPTOGRAPHIC CREDENTIALS & KEYS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BangTextSecondary
            )
        }

        // Cryptographic Key Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangPrimaryNeon.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NODE ALIAS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = BangTextSecondary
                            )
                            Text(
                                text = uiState.identity.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = BangTextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BangSurface)
                                .border(1.dp, BangCyanAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint",
                                tint = BangCyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ED25519 IDENTITY FINGERPRINT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = BangTextSecondary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BangSurface)
                            .border(1.dp, BangSurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = uiState.identity.fingerprint,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            ),
                            color = BangPrimaryNeon
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Hardware KeyStore & Ratchet Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "HARDWARE ROOT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = BangTextSecondary
                            )
                            Text(
                                text = "Android StrongBox",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BangCyanAccent
                                )
                            )
                        }

                        Column {
                            Text(
                                text = "ACTIVE RATCHETS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = BangTextSecondary
                            )
                            Text(
                                text = "${uiState.identity.activeRatchetSessions} Double Ratchets",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BangPrimaryNeon
                                )
                            )
                        }
                    }
                }
            }
        }

        // Zero-Trust Stranger Relay & Privacy Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangAmberWarning.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ANONYMOUS STRANGER RELAY",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                ),
                                color = BangAmberWarning
                            )
                            Text(
                                text = "Allow nearby phones (strangers) to hop through your device blindly",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = BangTextSecondary
                            )
                        }

                        Switch(
                            checked = uiState.identity.strangerRelayAllowed,
                            onCheckedChange = { onToggleStrangerRelay() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BangBackground,
                                checkedTrackColor = BangPrimaryNeon,
                                uncheckedThumbColor = BangTextMuted,
                                uncheckedTrackColor = BangSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BangSurface)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "BLIND PACKETS FORWARDED",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = FontFamily.Monospace),
                                color = BangTextSecondary
                            )
                            Text(
                                text = "${uiState.identity.strangerPacketsForwarded} Packets",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = BangPrimaryNeon
                                )
                            )
                        }

                        Column {
                            Text(
                                text = "CONTENT LEAKED",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = FontFamily.Monospace),
                                color = BangTextSecondary
                            )
                            Text(
                                text = "0 Bytes (100% E2EE)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = BangCyanAccent
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onOpenRelaySimulator,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BangAmberWarning),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BangAmberWarning)
                    ) {
                        Text(
                            text = "Launch A ➔ B ➔ C Relay Simulator",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }
        }

        // Out-of-band Verification Action
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BangSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangSurfaceBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BangSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = BangPrimaryNeon,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Show Peer QR Code",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = BangTextPrimary
                            )
                            Text(
                                text = "Verify public key in-person via camera",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = BangTextSecondary
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { /* Display QR */ },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BangPrimaryNeon),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BangPrimaryNeon.copy(alpha = 0.5f))
                    ) {
                        Text("Show", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Radio Duty-Cycle Management
        item {
            Text(
                text = "RADIO DUTY-CYCLE & POWER PROFILE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BangTextSecondary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangSurfaceBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DutyCycleOption(
                        title = "Tactical Active (100% Duty Cycle)",
                        desc = "Continuous BLE scanning & 100ms advert beacon. Zero delay, higher battery.",
                        isSelected = selectedDutyMode == "Tactical",
                        onClick = { selectedDutyMode = "Tactical" }
                    )

                    DutyCycleOption(
                        title = "Balanced Relay (16% Duty Cycle)",
                        desc = "10s scan window every minute. Recommended for all-day festival use (<4% battery/day).",
                        isSelected = selectedDutyMode == "Balanced",
                        onClick = { selectedDutyMode = "Balanced" }
                    )

                    DutyCycleOption(
                        title = "Emergency Stealth (Passive Only)",
                        desc = "Passive radio listening only. Transmits beacon only when queuing outbox packets.",
                        isSelected = selectedDutyMode == "Stealth",
                        onClick = { selectedDutyMode = "Stealth" }
                    )
                }
            }
        }

        // Section: Decentralized Anti-DoS & Spectrum Abuse Protection
        item {
            Text(
                text = "ANTI-DOS & SPECTRUM ABUSE MITIGATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BangTextSecondary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangCyanAccent.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MICRO-HASHCASH PROOF-OF-WORK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = BangCyanAccent
                            )
                            Text(
                                text = "12-Bit Challenge Active",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BangTextPrimary
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BangCyanAccent.copy(alpha = 0.15f))
                                .border(1.dp, BangCyanAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "🛡️ PoW Rate Limiter",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BangCyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BangSurface)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "SPAM PACKETS DROPPED",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontFamily = FontFamily.Monospace),
                                color = BangTextSecondary
                            )
                            Text(
                                text = "${uiState.identity.antiDosMetrics.spamPacketsRejected} (Failed PoW)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BangSosRed
                                )
                            )
                        }

                        Column {
                            Text(
                                text = "SUPPRESSED BROADCASTS",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontFamily = FontFamily.Monospace),
                                color = BangTextSecondary
                            )
                            Text(
                                text = "${uiState.identity.antiDosMetrics.duplicateBroadcastsSuppressed} Redundant",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BangPrimaryNeon
                                )
                            )
                        }

                        Column {
                            Text(
                                text = "CIRCULAR BUFFER",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontFamily = FontFamily.Monospace),
                                color = BangTextSecondary
                            )
                            Text(
                                text = "${uiState.identity.antiDosMetrics.bufferBytesUsed / (1024 * 1024)}MB / 50MB",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BangTextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: Android Platform Compliance & Battery Optimization
        item {
            Text(
                text = "PLATFORM COMPLIANCE & DOZE OPTIMIZATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BangTextSecondary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangSurfaceBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ANDROID FOREGROUND SERVICE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = BangPrimaryNeon
                            )
                            Text(
                                text = "Type: ${uiState.identity.complianceStatus.serviceType}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = BangTextPrimary
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BangPrimaryNeon.copy(alpha = 0.15f))
                                .border(1.dp, BangPrimaryNeon.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { onToggleDozeMode() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("btn_doze_mode_status")
                        ) {
                            Text(
                                text = if (uiState.identity.complianceStatus.isDozeModeSimulated) "⚡ Doze Preserved" else "🟢 Adaptive Mode",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (uiState.identity.complianceStatus.isDozeModeSimulated) BangAmberWarning else BangPrimaryNeon,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Current Schedule: ${uiState.identity.complianceStatus.dutyCycleWindow}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = BangTextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "24h Battery Impact: <${uiState.identity.complianceStatus.batteryDrain24h}% (Preserved under Android 14+ Doze)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace,
                            color = BangPrimaryNeon
                        )
                    )
                }
            }
        }

        // Section: Legal & Compliance (Settings Tab)
        item {
            Text(
                text = "LEGAL & COMPLIANCE (SETTINGS)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BangCyanAccent
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LegalSettingsListItem(
                    icon = Icons.Default.Gavel,
                    title = "Terms of Service & EULA",
                    subtitle = "App Store & Google Play hybrid mesh & P2P network terms",
                    badge = "OCT 2026",
                    accentColor = BangElectricBlue,
                    tag = "settings_item_terms_of_service",
                    onClick = { activeLegalDoc = LegalDocumentType.TERMS_OF_SERVICE }
                )

                LegalSettingsListItem(
                    icon = Icons.Default.Security,
                    title = "Privacy Policy",
                    subtitle = "Zero-Knowledge keys, zero telemetry, local mesh packet rules",
                    badge = "ZERO-KNOWLEDGE",
                    accentColor = BangEmeraldMesh,
                    tag = "settings_item_privacy_policy",
                    onClick = { activeLegalDoc = LegalDocumentType.PRIVACY_POLICY }
                )

                LegalSettingsListItem(
                    icon = Icons.Default.Description,
                    title = "Open Source Licenses",
                    subtitle = "TweetNaCl, Ed25519/X25519, Jetpack Compose, Vico notices",
                    badge = "ATTRIBUTIONS",
                    accentColor = BangCyanAccent,
                    tag = "settings_item_open_source_licenses",
                    onClick = { activeLegalDoc = LegalDocumentType.OPEN_SOURCE_LICENSES }
                )

                LegalSettingsListItem(
                    icon = Icons.Default.SupportAgent,
                    title = "Customer Support",
                    subtitle = "Official App Store developer channel: support@bang-messenger.io",
                    badge = "24/7 SLA",
                    accentColor = BangEmeraldMesh,
                    tag = "settings_item_customer_support",
                    onClick = { activeLegalDoc = LegalDocumentType.CUSTOMER_SUPPORT }
                )

                LegalSettingsListItem(
                    icon = Icons.Default.Bolt,
                    title = "🤖 BANG AI Support Assistant",
                    subtitle = "Hybrid Cloud + On-Device SLM (Offline Mesh RAG)",
                    badge = "HYBRID AI",
                    accentColor = BangCyanAccent,
                    tag = "settings_item_ai_support_assistant",
                    onClick = { onOpenShowcaseMode(DesignShowcaseMode.AI_SUPPORT_ASSISTANT) }
                )
            }
        }

        // Section: In-App Account Deletion (Apple App Store Guideline 5.1.1(v) & Google Play Compliant)
        item {
            Text(
                text = "ACCOUNT & PRIVACY (SETTINGS -> ACCOUNT)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BangSosRed
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_account_section_card"),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangSosRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CRYPTOGRAPHIC ENCLAVE IDENTITY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = BangCyanAccent
                            )
                            Text(
                                text = "Node: ${uiState.identity.nodeId}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = BangTextPrimary
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BangEmeraldMesh.copy(alpha = 0.15f))
                                .border(1.dp, BangEmeraldMesh.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "StrongBox HSM Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    color = BangEmeraldMesh,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Text(
                        text = "Fingerprint: ${uiState.identity.fingerprint}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = BangTextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showBiometricPanicPrompt = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BangSosRed.copy(alpha = 0.15f),
                            contentColor = BangSosRed
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BangSosRed.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("settings_account_delete_account_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Delete Account & Wipe Keystore Keys",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Interactive Legal Modal Viewer for Terms, Privacy, or Licenses
    activeLegalDoc?.let { docType ->
        BangLegalModal(
            documentType = docType,
            onDismiss = { activeLegalDoc = null }
        )
    }

    // Biometric Invisible Authentication before Panic Keystore Wipe
    if (showBiometricPanicPrompt) {
        com.example.security.BiometricVerificationModal(
            isOpen = showBiometricPanicPrompt,
            title = "Biometric Verification Required",
            subtitle = "Touch sensor or look at screen to authorize zero-trust hardware keystore wipe",
            onSuccess = {
                showBiometricPanicPrompt = false
                showPurgeConfirmationDialog = true
            },
            onDismiss = { showBiometricPanicPrompt = false }
        )
    }

    // Account Data Deletion Confirmation Dialog
    if (showPurgeConfirmationDialog) {
        AccountDataDeletionDialog(
            onConfirmDelete = {
                onPurgeAccountData()
                purgeSuccessMessage = "All cryptographic keys and local cache were successfully wiped from StrongBox and storage."
            },
            onDismiss = { showPurgeConfirmationDialog = false }
        )
    }

    // Success confirmation dialog after data purge
    purgeSuccessMessage?.let { msg ->
        androidx.compose.ui.window.Dialog(onDismissRequest = { purgeSuccessMessage = null }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BangSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BangEmeraldMesh.copy(alpha = 0.6f)),
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("purge_success_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BangEmeraldMesh,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ACCOUNT DATA PURGED",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = BangEmeraldMesh
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = BangTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { purgeSuccessMessage = null },
                        colors = ButtonDefaults.buttonColors(containerColor = BangEmeraldMesh)
                    ) {
                        Text("Done", color = BangSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DutyCycleOption(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) BangSurface else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) BangPrimaryNeon.copy(alpha = 0.5f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = BangPrimaryNeon,
                unselectedColor = BangTextMuted
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                ),
                color = if (isSelected) BangPrimaryNeon else BangTextPrimary
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                color = BangTextSecondary
            )
        }
    }
}

@Composable
fun NetworkModeOptionItem(
    title: String,
    badge: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) BangSurface else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) accentColor.copy(alpha = 0.6f) else BangSurfaceBorder.copy(alpha = 0.5f),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = accentColor,
                unselectedColor = BangTextMuted
            )
        )
        Spacer(modifier = Modifier.width(6.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) accentColor.copy(alpha = 0.15f) else BangSurface)
                .border(1.dp, if (isSelected) accentColor.copy(alpha = 0.5f) else BangSurfaceBorder, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) accentColor else BangTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    ),
                    color = if (isSelected) BangTextPrimary else BangTextSecondary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) accentColor.copy(alpha = 0.2f) else BangSurface)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isSelected) accentColor else BangTextMuted
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                color = BangTextSecondary,
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * Dedicated toggle card for status modes (Invisible, DND, Sleep, Auto-Presence)
 */
@Composable
fun StatusModeToggleCard(
    emoji: String,
    title: String,
    description: String,
    isChecked: Boolean,
    accentColor: Color,
    tag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked) BangSurfaceVariant else BangSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isChecked) accentColor.copy(alpha = 0.7f) else BangSurfaceBorder
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = emoji,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isChecked) accentColor else BangTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.5.sp,
                            color = BangTextSecondary,
                            lineHeight = 14.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag("${tag}_switch"),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = BangTextMuted,
                    uncheckedTrackColor = BangSurface
                )
            )
        }
    }
}
