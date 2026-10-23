package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ChatThread
import com.example.ui.BangViewModel
import com.example.ui.DesignShowcaseMode
import com.example.ui.components.BangAiSupportScreen
import com.example.ui.components.BangContactSyncModal
import com.example.ui.components.BangDirectPeerDiscoveryModal
import com.example.ui.components.BangUiPresentationScreen
import com.example.ui.components.ChatDetailScreen
import com.example.ui.components.ChatListScreen
import com.example.ui.components.ContactsScreen
import com.example.ui.components.IdentitySecurityScreen
import com.example.ui.components.MeshNetworkScreen
import com.example.ui.components.MeshStoryViewerModal
import com.example.ui.components.ProfileSettingsScreen
import com.example.ui.components.PulsesScreen
import com.example.ui.components.QrAndNfcScreen
import com.example.ui.components.ScanAndIdBottomSheetModal
import com.example.ui.components.ScanAndNfcScreen
import com.example.ui.components.SosCenterScreen
import com.example.ui.components.TopBarAndStatusHeader
import com.example.ui.components.ZeroTrustRelayScreen
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
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BangViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.hapticHelper = com.example.haptics.HapticHelper(this)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = this@MainActivity

                // BLE Mesh Hardware Permissions Request Launcher
                val permissionsToRequest = remember {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } else {
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) {
                    // Once granted (or evaluated), invoke startRealBleMesh so physical radio scanning and advertising start
                    viewModel.startRealBleMesh(context)
                }

                LaunchedEffect(Unit) {
                    permissionLauncher.launch(permissionsToRequest)
                }

                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                var activeFilter by remember { mutableStateOf("All") }

                val activeThread: ChatThread? = uiState.selectedThreadId?.let { id ->
                    uiState.threads.find { it.id == id }
                }

                if (uiState.activeShowcaseMode != DesignShowcaseMode.NONE) {
                    BangUiPresentationScreen(
                        currentMode = uiState.activeShowcaseMode,
                        onSelectMode = { viewModel.setDesignShowcaseMode(it) },
                        onExitShowcase = { viewModel.setDesignShowcaseMode(DesignShowcaseMode.NONE) },
                        uiState = uiState,
                        aiAssistantEngine = viewModel.aiAssistantEngine
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CyberSlateBg),
                            topBar = {
                                // Main topbar with live telemetry, mode switcher, and auto-switch banners
                                if (activeThread == null) {
                                    TopBarAndStatusHeader(
                                        uiState = uiState,
                                        onModeSelect = { viewModel.setConnectivityMode(it) },
                                        onToggleOutage = { viewModel.toggleSimulatedOutage() },
                                        onDismissAutoSwitch = { viewModel.dismissAutoSwitchBanner() },
                                        onOpenShowcase = { viewModel.setDesignShowcaseMode(DesignShowcaseMode.LOGIN) },
                                        onOpenAiArchitecture = { viewModel.setDesignShowcaseMode(DesignShowcaseMode.ON_DEVICE_AI_ARCHITECTURE) },
                                        onOpenAiAssistant = { viewModel.setDesignShowcaseMode(DesignShowcaseMode.AI_SUPPORT_ASSISTANT) },
                                        onOpenMeshInnovations = { viewModel.setDesignShowcaseMode(DesignShowcaseMode.MESH_INNOVATIONS) },
                                        selectedFilter = activeFilter,
                                        onFilterChange = { activeFilter = it },
                                        onSelectPresenceMode = { viewModel.setUserPresenceMode(it) },
                                        onToggleAutoPresence = { viewModel.toggleAutoPresenceDetection(it) }
                                    )
                                }
                            },
                            bottomBar = {
                                if (activeThread == null) {
                                    com.example.ui.components.BangBottomNavigationBar(
                                        selectedTab = uiState.currentTab,
                                        unreadTotal = uiState.threads.sumOf { it.unreadCount },
                                        activeSosAlerts = uiState.incomingSosBeacons.size,
                                        onSelectTab = { viewModel.selectTab(it) },
                                        onOpenScanModal = { viewModel.openScanAndIdBottomSheet() },
                                        modifier = Modifier.navigationBarsPadding()
                                    )
                                }
                            },
                            containerColor = CyberSlateBg
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                if (activeThread != null) {
                                    ChatDetailScreen(
                                        thread = activeThread,
                                        messages = uiState.messagesByThread[activeThread.id] ?: emptyList(),
                                        onSendMessage = { text, replyTo -> viewModel.sendMessage(activeThread.id, text, replyToText = replyTo) },
                                        onBack = { viewModel.closeThread() },
                                        threadSummary = uiState.activeThreadSummary,
                                        isSummarizing = uiState.isSummarizingThread,
                                        onTriggerSummary = { viewModel.generateThreadSummary(activeThread.id) },
                                        onDismissSummary = { viewModel.dismissThreadSummary() },
                                        compressedDraft = uiState.draftCompressedPacket,
                                        onCompressDraft = { viewModel.compressCurrentDraft(it) },
                                        crisisQos = uiState.draftCrisisQos,
                                        piiAudit = uiState.draftPiiAudit,
                                        onDraftChanged = { viewModel.runDraftAiAudit(it) },
                                        onRedactDraft = { viewModel.clearDraftAiAudit() },
                                        onOpenScanModal = { viewModel.openScanAndIdBottomSheet() },
                                        onAddReaction = { msgId, emoji -> viewModel.addReactionToMessage(activeThread.id, msgId, emoji) },
                                        isOffline = uiState.connectivityMode == com.example.model.ConnectivityMode.OFFLINE_MESH || uiState.isSimulatedOutage
                                    )
                                } else {
                                    Crossfade(
                                        targetState = uiState.currentTab,
                                        label = "TabCrossfade"
                                    ) { tab ->
                                        when (tab) {
                                            0 -> ChatListScreen(
                                                uiState = uiState,
                                                onOpenThread = { threadId -> viewModel.openThread(threadId) },
                                                onOpenContactSync = { viewModel.openContactSyncModal() },
                                                onOpenStory = { story -> viewModel.openStory(story) },
                                                onOpenAiAssistant = { viewModel.setDesignShowcaseMode(DesignShowcaseMode.AI_SUPPORT_ASSISTANT) },
                                                activeFilter = activeFilter,
                                                onSelectPredictiveMode = { mode -> viewModel.setPredictiveContextMode(mode) },
                                                onTriggerPredictiveAction = { mode ->
                                                    when (mode) {
                                                        com.example.model.PredictiveContextMode.DRIVING_MOTION -> {
                                                            val target = uiState.threads.firstOrNull()
                                                            if (target != null) viewModel.openThread(target.id)
                                                        }
                                                        com.example.model.PredictiveContextMode.NEAR_REGISTERED_PEER -> {
                                                            viewModel.openScanAndIdBottomSheet()
                                                        }
                                                        com.example.model.PredictiveContextMode.INTERNET_OUTAGE -> {
                                                            viewModel.selectTab(2)
                                                        }
                                                        com.example.model.PredictiveContextMode.STANDARD_CHAT -> {
                                                            viewModel.openContactSyncModal()
                                                        }
                                                    }
                                                }
                                            )
                                            1 -> ContactsScreen(
                                                uiState = uiState,
                                                onOpenChat = { threadId -> viewModel.openThread(threadId) },
                                                onScanQr = { viewModel.openScanAndIdBottomSheet() },
                                                onAddContact = { viewModel.openContactSyncModal() }
                                            )
                                            2 -> MeshNetworkScreen(
                                                uiState = uiState,
                                                onBack = { viewModel.selectTab(0) },
                                                onOpenSettings = { viewModel.selectTab(4) },
                                                onSelectPeer = { peer -> viewModel.selectPeerNode(peer) }
                                            )
                                            3 -> BangAiSupportScreen(
                                                engine = viewModel.aiAssistantEngine,
                                                onBack = { viewModel.selectTab(0) }
                                            )
                                            4 -> ProfileSettingsScreen(
                                                uiState = uiState,
                                                onBack = { viewModel.selectTab(0) },
                                                onOpenQr = { viewModel.openScanAndIdBottomSheet() },
                                                onSelectPresence = { viewModel.setUserPresenceMode(it) },
                                                onToggleStrangerRelay = { viewModel.toggleStrangerRelayAllowed() },
                                                onToggleApkServer = { viewModel.toggleApkServer() },
                                                onPanicWipe = { viewModel.purgeAllAccountData() }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Mesh Story Viewer Modal overlay (Component A)
                        val activeStory = uiState.activeViewingStory
                        if (activeStory != null) {
                            MeshStoryViewerModal(
                                story = activeStory,
                                allStories = uiState.meshStories,
                                onClose = { viewModel.closeStory() },
                                onNext = { viewModel.nextStory() },
                                onPrev = { viewModel.prevStory() },
                                onVotePoll = { storyId, optIdx -> viewModel.voteStoryPoll(storyId, optIdx) },
                                onSendReply = { _, _ -> }
                            )
                        }

                        // Scan & ID Dual-Mode Bottom Sheet Modal overlay (Component B)
                        if (uiState.showScanAndIdBottomSheet) {
                            ScanAndIdBottomSheetModal(
                                identity = uiState.identity,
                                peers = uiState.meshPeers,
                                onDismiss = { viewModel.closeScanAndIdBottomSheet() },
                                onStartChatWithPeer = { peer ->
                                    viewModel.closeScanAndIdBottomSheet()
                                    val matchedThread = uiState.threads.firstOrNull {
                                        it.id.contains("alex") || it.id.contains("charlie") || it.id.contains("sarah")
                                    }
                                    if (matchedThread != null) {
                                        viewModel.openThread(matchedThread.id)
                                    }
                                }
                            )
                        }

                        // Contact Sync Modal overlay
                        if (uiState.showContactSyncModal) {
                            BangContactSyncModal(
                                onSyncClick = { viewModel.closeContactSyncModal() },
                                onSkipClick = { viewModel.closeContactSyncModal() }
                            )
                        }

                        // Direct Peer Discovery Modal overlay
                        if (uiState.showPeerDiscoveryModal) {
                            val peer = uiState.discoveryModalPeer ?: uiState.meshPeers.find { it.displayName.contains("Alex Rivera") }
                            BangDirectPeerDiscoveryModal(
                                displayName = peer?.displayName ?: "Alex Rivera",
                                usernameHandle = peer?.handle ?: "@alex_rivera",
                                statusBadge = "● Direct BLE Node Verified",
                                keyInfo = peer?.keyInfo ?: "Node ID: 8f9a...32c1 (Curve25519 Verified)",
                                onConnectClick = { viewModel.closePeerDiscoveryModal() },
                                onDismissClick = { viewModel.closePeerDiscoveryModal() }
                            )
                        }

                        // NFC Bump Modal overlay
                        if (uiState.showNfcScanModal) {
                            androidx.compose.ui.window.Dialog(onDismissRequest = { viewModel.closeNfcModal() }) {
                                com.example.ui.components.BangNfcBumpModal(
                                    onConnectSuccess = { viewModel.simulateNfcHandshakeWithPeer() },
                                    onDismiss = { viewModel.closeNfcModal() }
                                )
                            }
                        }

                        // QR Code Modal overlay
                        if (uiState.showQrCodeModal) {
                            androidx.compose.ui.window.Dialog(onDismissRequest = { viewModel.closeQrModal() }) {
                                com.example.ui.components.BangQrIdentityModal(
                                    nodeId = uiState.identity.nodeId,
                                    handle = "@atlas_01",
                                    onDismiss = { viewModel.closeQrModal() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

