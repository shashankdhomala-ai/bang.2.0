package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.res.painterResource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BangUiState
import com.example.ui.DesignShowcaseMode
import com.example.ui.theme.BangElectricBlue
import com.example.ui.theme.BangEmeraldMesh
import com.example.ui.theme.BangGlassBorder
import com.example.ui.theme.BangGlassSurface
import com.example.ui.theme.BangNeonPurple
import com.example.ui.theme.BangObsidian
import com.example.ui.theme.BangPurpleDeep
import com.example.ui.theme.BangSurface
import com.example.ui.theme.BangSurfaceBorder
import com.example.ui.theme.BangSurfaceVariant
import com.example.ui.theme.BangTextMuted
import com.example.ui.theme.BangTextPrimary
import com.example.ui.theme.BangTextSecondary

@Composable
fun BangUiPresentationScreen(
    currentMode: DesignShowcaseMode,
    onSelectMode: (DesignShowcaseMode) -> Unit,
    onExitShowcase: () -> Unit,
    uiState: BangUiState = BangUiState(),
    aiAssistantEngine: com.example.ai.BangAiAssistantEngine = remember { com.example.ai.BangAiAssistantEngine() },
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidian)
            .statusBarsPadding()
            .testTag("bang_ui_presentation_screen")
    ) {
        // Showcase Top Header & Screen Switcher
        Card(
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = BangSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(BangElectricBlue.copy(alpha = 0.5f), BangNeonPurple.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                )
                .shadow(elevation = 12.dp, spotColor = BangElectricBlue)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.app_logo),
                            contentDescription = "BANG App Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, BangElectricBlue.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BANG UI/UX DESIGN SHOWCASE",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = BangElectricBlue,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "High-Fidelity Behance / Dribbble Presentation",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = BangTextMuted
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onExitShowcase,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(BangGlassSurface)
                            .border(1.dp, BangGlassBorder, CircleShape)
                            .testTag("btn_exit_showcase")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit to Mesh App",
                            tint = BangTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selector Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShowcaseTabPill(
                        label = "1. Splash / Welcome",
                        isSelected = currentMode == DesignShowcaseMode.WELCOME,
                        onClick = { onSelectMode(DesignShowcaseMode.WELCOME) },
                        testTag = "pill_showcase_welcome"
                    )
                    ShowcaseTabPill(
                        label = "2. Login / Sign-Up",
                        isSelected = currentMode == DesignShowcaseMode.LOGIN || currentMode == DesignShowcaseMode.SIGNUP,
                        onClick = { onSelectMode(DesignShowcaseMode.LOGIN) },
                        testTag = "pill_showcase_login"
                    )
                    ShowcaseTabPill(
                        label = "3. Chats Feed",
                        isSelected = currentMode == DesignShowcaseMode.CHATS,
                        onClick = { onSelectMode(DesignShowcaseMode.CHATS) },
                        testTag = "pill_showcase_chats"
                    )
                    ShowcaseTabPill(
                        label = "4. Active Chat & Delivery",
                        isSelected = currentMode == DesignShowcaseMode.ACTIVE_CHAT,
                        onClick = { onSelectMode(DesignShowcaseMode.ACTIVE_CHAT) },
                        testTag = "pill_showcase_active_chat"
                    )
                    ShowcaseTabPill(
                        label = "5. Mesh Network & Radar",
                        isSelected = currentMode == DesignShowcaseMode.MESH_NETWORK,
                        onClick = { onSelectMode(DesignShowcaseMode.MESH_NETWORK) },
                        testTag = "pill_showcase_mesh_network"
                    )
                    ShowcaseTabPill(
                        label = "6. Contacts & Directory",
                        isSelected = currentMode == DesignShowcaseMode.CONTACTS,
                        onClick = { onSelectMode(DesignShowcaseMode.CONTACTS) },
                        testTag = "pill_showcase_contacts"
                    )
                    ShowcaseTabPill(
                        label = "7. Identity & NFC Tap",
                        isSelected = currentMode == DesignShowcaseMode.QR_AND_NFC,
                        onClick = { onSelectMode(DesignShowcaseMode.QR_AND_NFC) },
                        testTag = "pill_showcase_qr_nfc"
                    )
                    ShowcaseTabPill(
                        label = "8. Profile & Settings",
                        isSelected = currentMode == DesignShowcaseMode.PROFILE_SETTINGS,
                        onClick = { onSelectMode(DesignShowcaseMode.PROFILE_SETTINGS) },
                        testTag = "pill_showcase_profile"
                    )
                    ShowcaseTabPill(
                        label = "9. 🤖 AI Assistant",
                        isSelected = currentMode == DesignShowcaseMode.AI_SUPPORT_ASSISTANT,
                        onClick = { onSelectMode(DesignShowcaseMode.AI_SUPPORT_ASSISTANT) },
                        testTag = "pill_showcase_ai_assistant"
                    )
                    ShowcaseTabPill(
                        label = "10. 🚨 SOS Emergency",
                        isSelected = currentMode == DesignShowcaseMode.SOS_EMERGENCY,
                        onClick = { onSelectMode(DesignShowcaseMode.SOS_EMERGENCY) },
                        testTag = "pill_showcase_sos"
                    )
                    ShowcaseTabPill(
                        label = "11. Local SLM Architecture",
                        isSelected = currentMode == DesignShowcaseMode.ON_DEVICE_AI_ARCHITECTURE,
                        onClick = { onSelectMode(DesignShowcaseMode.ON_DEVICE_AI_ARCHITECTURE) },
                        testTag = "pill_showcase_on_device_ai"
                    )
                    ShowcaseTabPill(
                        label = "12. ⚡ Battery Governor",
                        isSelected = currentMode == DesignShowcaseMode.MESH_INNOVATIONS,
                        onClick = { onSelectMode(DesignShowcaseMode.MESH_INNOVATIONS) },
                        testTag = "pill_showcase_mesh_innovations"
                    )
                }
            }
        }

        // Active Presentation Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (currentMode) {
                DesignShowcaseMode.WELCOME -> {
                    BangWelcomeScreen(
                        onGetStarted = { onSelectMode(DesignShowcaseMode.LOGIN) },
                        onLogInClick = { onSelectMode(DesignShowcaseMode.LOGIN) }
                    )
                }
                DesignShowcaseMode.LOGIN, DesignShowcaseMode.SIGNUP -> {
                    BangAuthScreen(
                        onAuthSuccess = { onSelectMode(DesignShowcaseMode.CHATS) },
                        onBackToWelcome = { onSelectMode(DesignShowcaseMode.WELCOME) }
                    )
                }
                DesignShowcaseMode.CHATS -> {
                    ChatListScreen(
                        uiState = uiState,
                        onOpenThread = { onSelectMode(DesignShowcaseMode.ACTIVE_CHAT) },
                        onOpenContactSync = { onSelectMode(DesignShowcaseMode.CONTACTS) },
                        onOpenStory = { },
                        onOpenAiAssistant = { onSelectMode(DesignShowcaseMode.AI_SUPPORT_ASSISTANT) }
                    )
                }
                DesignShowcaseMode.ACTIVE_CHAT -> {
                    val sampleThread = uiState.threads.firstOrNull() ?: com.example.model.ChatThread(
                        id = "sarah_offline",
                        title = "Sarah Jenkins",
                        lastMessage = "Signal dropping fast, stay safe!",
                        timestamp = System.currentTimeMillis() - 120_000L,
                        unreadCount = 0,
                        isGroup = false,
                        preferredTransport = com.example.model.TransportType.BLE_L2CAP,
                        activeHops = 2,
                        peerPublicKeyFingerprint = "ed25519:7a4f..9b12",
                        involvesStrangerRelay = true,
                        routingTag = "⚡ 2 Hops via Stranger Bob"
                    )
                    ChatDetailScreen(
                        thread = sampleThread,
                        messages = uiState.messagesByThread[sampleThread.id] ?: emptyList(),
                        onSendMessage = { _, _ -> },
                        onBack = { onSelectMode(DesignShowcaseMode.CHATS) }
                    )
                }
                DesignShowcaseMode.MESH_NETWORK -> {
                    MeshNetworkScreen(
                        uiState = uiState,
                        onBack = onExitShowcase,
                        onOpenSettings = { onSelectMode(DesignShowcaseMode.PROFILE_SETTINGS) },
                        onSelectPeer = { }
                    )
                }
                DesignShowcaseMode.CONTACTS -> {
                    ContactsScreen(
                        uiState = uiState,
                        onOpenChat = { onSelectMode(DesignShowcaseMode.ACTIVE_CHAT) },
                        onScanQr = { onSelectMode(DesignShowcaseMode.QR_AND_NFC) }
                    )
                }
                DesignShowcaseMode.QR_AND_NFC -> {
                    QrAndNfcScreen(
                        uiState = uiState,
                        onBack = onExitShowcase
                    )
                }
                DesignShowcaseMode.PROFILE_SETTINGS -> {
                    ProfileSettingsScreen(
                        uiState = uiState,
                        onBack = onExitShowcase,
                        onOpenQr = { onSelectMode(DesignShowcaseMode.QR_AND_NFC) }
                    )
                }
                DesignShowcaseMode.AI_SUPPORT_ASSISTANT -> {
                    BangAiSupportScreen(
                        engine = aiAssistantEngine,
                        onBack = onExitShowcase
                    )
                }
                DesignShowcaseMode.SOS_EMERGENCY -> {
                    SosCenterScreen(
                        uiState = uiState,
                        onTriggerSos = { },
                        onCancelSosCountdown = { },
                        onCancelActiveSos = { }
                    )
                }
                DesignShowcaseMode.CONTACT_SYNC -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MockChatBackground()
                        BangContactSyncModal(
                            onSyncClick = { },
                            onSkipClick = onExitShowcase
                        )
                    }
                }
                DesignShowcaseMode.PEER_DISCOVERY -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MockChatBackground()
                        BangDirectPeerDiscoveryModal(
                            displayName = "Alex Rivera",
                            usernameHandle = "@alex_rivera",
                            statusBadge = "● Direct BLE Node Verified",
                            keyInfo = "Node ID: 8f9a...32c1 (Curve25519 Verified)",
                            onConnectClick = { },
                            onDismissClick = onExitShowcase
                        )
                    }
                }
                DesignShowcaseMode.RADAR_DISCOVERY -> {
                    ScanAndNfcScreen(
                        uiState = uiState,
                        onSelectPeer = { },
                        onStartChatWithPeer = { }
                    )
                }
                DesignShowcaseMode.ON_DEVICE_AI_ARCHITECTURE -> {
                    OnDeviceAiArchitectureScreen(
                        uiState = uiState,
                        slmEngine = com.example.ai.LocalSlmEngine(),
                        onClose = onExitShowcase
                    )
                }
                DesignShowcaseMode.MESH_INNOVATIONS -> {
                    MeshInnovationsScreen(
                        onClose = onExitShowcase
                    )
                }
                DesignShowcaseMode.NONE -> {
                    BangWelcomeScreen(
                        onGetStarted = { onSelectMode(DesignShowcaseMode.LOGIN) },
                        onLogInClick = { onSelectMode(DesignShowcaseMode.LOGIN) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowcaseTabPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val backgroundBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(BangElectricBlue, BangNeonPurple))
    } else {
        Brush.horizontalGradient(listOf(BangGlassSurface, BangGlassSurface))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundBrush)
            .border(
                width = 1.dp,
                color = if (isSelected) BangElectricBlue else BangGlassBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp,
                color = if (isSelected) BangObsidian else BangTextSecondary
            )
        )
    }
}

@Composable
private fun MockChatBackground() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BangObsidian)
            .padding(16.dp)
    ) {
        // Simulated chat header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BangSurfaceVariant)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Box(
                    modifier = Modifier
                        .size(width = 120.dp, height = 12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(BangSurfaceBorder)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(BangSurfaceBorder.copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Simulated chat bubbles
        repeat(4) { idx ->
            val isMe = idx % 2 == 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(width = if (isMe) 200.dp else 240.dp, height = 48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isMe) BangPurpleDeep.copy(alpha = 0.3f) else BangSurfaceVariant)
                        .border(1.dp, BangGlassBorder.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                )
            }
        }
    }
}
