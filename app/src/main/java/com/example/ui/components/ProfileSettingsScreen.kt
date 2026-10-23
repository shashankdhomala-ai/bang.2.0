package com.example.ui.components

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserPresenceMode
import com.example.ui.BangUiState
import com.example.ui.theme.BangCharcoalBorder
import com.example.ui.theme.BangCharcoalSurface
import com.example.ui.theme.BangElectricKineticLime
import com.example.ui.theme.BangObsidianBg
import com.example.ui.theme.BangStatusOnline
import com.example.ui.theme.BangStatusSos

/**
 * Screen 8: Profile & Settings Screen
 * Matches Phone 8 of the production design layout.
 */
@Composable
fun ProfileSettingsScreen(
    uiState: BangUiState,
    onBack: (() -> Unit)? = null,
    onOpenQr: () -> Unit = {},
    onSelectPresence: (UserPresenceMode) -> Unit = {},
    onToggleStrangerRelay: () -> Unit = {},
    onToggleApkServer: () -> Unit = {},
    onPanicWipe: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPanicConfirm by remember { mutableStateOf(false) }
    var showStorageDialog by remember { mutableStateOf(false) }
    var storageQuotaMb by remember { mutableFloatStateOf(50f) }
    var showPresencePicker by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidianBg)
            .testTag("profile_settings_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Bar if needed
            if (onBack != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Profile & Settings",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            // 2. Profile Identity Hero Card (Phone 8)
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Sunset Mountain Stylized Avatar
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFFE056FD), Color(0xFFF0932B), Color(0xFF2C3E50))
                                    )
                                )
                                .border(2.5.dp, Color(0xFF99FF00), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw stylized mountain silhouettes
                                val path = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(0f, size.height)
                                    lineTo(size.width * 0.35f, size.height * 0.5f)
                                    lineTo(size.width * 0.65f, size.height * 0.75f)
                                    lineTo(size.width * 0.85f, size.height * 0.45f)
                                    lineTo(size.width, size.height)
                                    close()
                                }
                                drawPath(path, Color(0xFF1E152A))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Uma",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "@uma_bang",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Status dot: Online • Nearby
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF070B11))
                                .clickable { showPresencePicker = true }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(BangStatusOnline)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Online • Nearby",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BangStatusOnline,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Row of circular action buttons: [ Edit ] [ Share QR ] [ Settings ]
                        Row(
                            modifier = Modifier.fillMaxWidth(0.85f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileRoundAction(icon = Icons.Default.Edit, label = "Edit", onClick = { feedbackMessage = "Profile editor active" })
                            ProfileRoundAction(icon = Icons.Default.QrCode, label = "Share QR", onClick = onOpenQr)
                            ProfileRoundAction(icon = Icons.Default.Settings, label = "Settings", onClick = { feedbackMessage = "Node settings synced" })
                        }
                    }
                }
            }

            // 3. Settings Menu Group
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BangCharcoalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        SettingsRow(icon = Icons.Default.Shield, title = "Privacy & Security", subtitle = "Zero-Trust Ed25519 & Curve25519 Keys") {
                            feedbackMessage = "Hardware KeyStore is active and enforced."
                        }
                        SettingsRow(icon = Icons.Default.Notifications, title = "Notifications", subtitle = "High-priority mesh pulse alerts") {
                            feedbackMessage = "Haptic alerts enabled."
                        }
                        SettingsRow(icon = Icons.Default.Storage, title = "Storage & Data", subtitle = "Local store-and-forward quota: ${storageQuotaMb.toInt()} MB") {
                            showStorageDialog = true
                        }
                        SettingsRow(icon = Icons.AutoMirrored.Filled.HelpOutline, title = "Help & Support", subtitle = "FAQ & On-device AI Guide") {
                            feedbackMessage = "Offline guide ready in BANG AI tab."
                        }
                    }
                }
            }

            // 4. Zero-Internet APK Sideloading Engine (New capability)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A28)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1B324B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WifiTethering,
                                        contentDescription = null,
                                        tint = BangElectricKineticLime,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Share BANG APK (Zero Internet)",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = if (uiState.apkServerStatus.isRunning) "Ad-hoc Hotspot HTTP Server: Active" else "Distribute APK over local Wi-Fi",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (uiState.apkServerStatus.isRunning) BangElectricKineticLime else Color(0xFF94A3B8),
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                            Switch(
                                checked = uiState.apkServerStatus.isRunning,
                                onCheckedChange = { onToggleApkServer() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BangElectricKineticLime,
                                    checkedTrackColor = Color(0xFF14301A)
                                )
                            )
                        }

                        if (uiState.apkServerStatus.isRunning) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF070B11),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Nearby friends can open this link in any browser:",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                                    )
                                    Text(
                                        text = uiState.apkServerStatus.downloadUrl,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = BangElectricKineticLime
                                        )
                                    )
                                    Text(
                                        text = "Completed Downloads: ${uiState.apkServerStatus.downloadsCompleted}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Panic Wipe (EULA Clause 5 Compliance)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F0D14)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A1523)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPanicConfirm = true }
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Panic Wipe",
                                tint = BangStatusSos,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Panic Wipe (EULA Clause 5)",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BangStatusSos
                                    )
                                )
                                Text(
                                    text = "Instantly shred database, keys, and session data",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFE28B9B),
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = BangStatusSos,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            if (feedbackMessage != null) {
                item {
                    Text(
                        text = feedbackMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BangElectricKineticLime,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }

    // Panic Wipe Confirmation Dialog
    if (showPanicConfirm) {
        AlertDialog(
            onDismissRequest = { showPanicConfirm = false },
            containerColor = BangCharcoalSurface,
            title = {
                Text(
                    text = "🚨 Confirm Emergency Panic Wipe?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BangStatusSos
                    )
                )
            },
            text = {
                Text(
                    text = "This will immediately overwrite and delete all encrypted Room messages (bang_mesh_vault.db), destroy KeyStore Ed25519/Curve25519 identity pairs, and reset the application state. This action cannot be reversed.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFE2E8F0))
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPanicConfirm = false
                        onPanicWipe()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BangStatusSos)
                ) {
                    Text("SHRED ALL DATA NOW", color = Color.White, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPanicConfirm = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    // Storage Quota Dialog
    if (showStorageDialog) {
        AlertDialog(
            onDismissRequest = { showStorageDialog = false },
            containerColor = BangCharcoalSurface,
            title = {
                Text(
                    text = "Store-and-Forward Quota",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "Configured local cache allocation for offline relay packets: ${storageQuotaMb.toInt()} MB",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8))
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(
                        value = storageQuotaMb,
                        onValueChange = { storageQuotaMb = it },
                        valueRange = 10f..200f,
                        steps = 18,
                        colors = SliderDefaults.colors(
                            thumbColor = BangElectricKineticLime,
                            activeTrackColor = BangElectricKineticLime
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStorageDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BangElectricKineticLime)
                ) {
                    Text("Save Quota", color = Color(0xFF070B11), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun ProfileRoundAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFF162130))
                .border(1.dp, BangCharcoalBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = BangElectricKineticLime,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8)
            )
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF475569),
            modifier = Modifier.size(13.dp)
        )
    }
}
