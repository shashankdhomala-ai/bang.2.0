package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.MeshPeerNode
import com.example.ui.BangUiState
import com.example.ui.theme.BangAmberWarning
import com.example.ui.theme.BangDeepMidnightObsidian
import com.example.ui.theme.BangElectricBlue
import com.example.ui.theme.BangEmeraldMesh
import com.example.ui.theme.BangHyperCyan
import com.example.ui.theme.BangKineticLime
import com.example.ui.theme.BangNeonHotPink
import com.example.ui.theme.BangNeonPurple
import com.example.ui.theme.BangTextMuted
import com.example.ui.theme.BangTextPrimary
import com.example.ui.theme.WhatsAppDarkBg
import com.example.ui.theme.WhatsAppFabGreen
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppSurface
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary
import kotlinx.coroutines.delay

/**
 * ScanAndNfcScreen
 *
 * Dedicated unified screen replacing the old radar with:
 * 1. 📷 SCANNER: Interactive Camera / QR Code & Local Mesh Beacon Scanner with animated laser sweep
 * 2. 🪪 USER ID: High-fidelity Cryptographic Node ID card with QR Matrix, Curve25519 key, and export
 * 3. ⚡ NFC BUMP: Contactless Near-Field Key Exchange with tactile phone-to-phone bump simulation
 */
@Composable
fun ScanAndNfcScreen(
    uiState: BangUiState,
    onSelectPeer: (MeshPeerNode?) -> Unit = {},
    onStartChatWithPeer: (MeshPeerNode) -> Unit = {},
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(2400)
            toastMessage = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BangDeepMidnightObsidian)
            .testTag("scan_and_nfc_screen")
    ) {
        // ---------------------------------------------------------------------
        // 1. Futuristic Header
        // ---------------------------------------------------------------------
        Surface(
            color = Color(0xFF060910),
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SCANNER & NFC IDENTITY",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = BangHyperCyan,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                        Text(
                            text = "Zero-Trust Discovery • Camera QR • Cryptographic NFC Handshake",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.5.sp,
                                color = BangTextMuted
                            )
                        )
                    }

                    // Active Peers Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(BangKineticLime.copy(alpha = 0.15f))
                            .border(1.dp, BangKineticLime, RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(BangKineticLime)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "📶 ${uiState.meshPeers.size} PEERS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = BangKineticLime,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs: [ SCANNER | USER ID | NFC BUMP ]
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = BangHyperCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = BangHyperCyan,
                            height = 2.5.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(15.dp), tint = if (selectedTab == 0) BangHyperCyan else BangTextMuted)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "SCANNER",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp,
                                        color = if (selectedTab == 0) BangHyperCyan else BangTextMuted
                                    )
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_scanner")
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(15.dp), tint = if (selectedTab == 1) BangHyperCyan else BangTextMuted)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "MY USER ID",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp,
                                        color = if (selectedTab == 1) BangHyperCyan else BangTextMuted
                                    )
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_user_id")
                    )

                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Contactless, contentDescription = null, modifier = Modifier.size(15.dp), tint = if (selectedTab == 2) BangHyperCyan else BangTextMuted)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "NFC BUMP",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp,
                                        color = if (selectedTab == 2) BangHyperCyan else BangTextMuted
                                    )
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_nfc")
                    )
                }
            }
        }

        // Ephemeral Toast Alert
        AnimatedVisibility(visible = toastMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0E2218))
                    .border(1.dp, BangKineticLime, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BangKineticLime, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = toastMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = BangKineticLime
                        )
                    )
                }
            }
        }

        // ---------------------------------------------------------------------
        // Main Tab Content
        // ---------------------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedTab) {
                0 -> ScannerView(
                    identity = uiState.identity,
                    peers = uiState.meshPeers,
                    onSelectPeer = onSelectPeer,
                    onStartChatWithPeer = onStartChatWithPeer,
                    onSwitchToTab = { selectedTab = it },
                    onShowToast = { toastMessage = it }
                )
                1 -> UserIdCardView(
                    identity = uiState.identity,
                    onShowToast = { toastMessage = it }
                )
                2 -> NfcBumpView(
                    peers = uiState.meshPeers,
                    onStartChatWithPeer = onStartChatWithPeer,
                    onShowToast = { toastMessage = it }
                )
            }
        }
    }
}

// =============================================================================
// SUB-TAB 0: SCANNER VIEW (Camera Viewfinder with Laser & Local Beacons)
// =============================================================================
@Composable
private fun ScannerView(
    identity: com.example.model.MyIdentity,
    peers: List<MeshPeerNode>,
    onSelectPeer: (MeshPeerNode?) -> Unit,
    onStartChatWithPeer: (MeshPeerNode) -> Unit,
    onSwitchToTab: (Int) -> Unit,
    onShowToast: (String) -> Unit
) {
    var isFlashOn by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var isGridOn by remember { mutableStateOf(true) }
    var isTargetLocked by remember { mutableStateOf(true) }
    var peerIndex by remember { mutableIntStateOf(0) }
    var isCopied by remember { mutableStateOf(false) }
    var showMyQrDialog by remember { mutableStateOf(false) }
    var showNfcBumpDialog by remember { mutableStateOf(false) }

    val activePeersList = remember(peers) {
        if (peers.isNotEmpty()) peers else listOf(
            MeshPeerNode(
                id = "peer_alex_direct",
                displayName = "Alex Rivera",
                handle = "@alex_rivera",
                distanceMeters = 1.4f,
                rssi = -46,
                angleDegrees = 45f,
                batteryPct = 88,
                hopsAway = 1,
                transport = com.example.model.TransportType.BLE_L2CAP,
                packetsRelayed = 142,
                isVerified = true
            )
        )
    }

    val currentTargetPeer = activePeersList[peerIndex % activePeersList.size]

    val infiniteTransition = rememberInfiniteTransition(label = "scannerLaser")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.94f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserSweep"
    )

    val nfcPulseRing by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "nfcPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // =====================================================================
        // 1. USER'S UNIQUE ID DISPLAY CARD (Prominently displayed)
        // =====================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070C16)),
            border = androidx.compose.foundation.BorderStroke(1.dp, BangHyperCyan.copy(alpha = 0.45f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("user_unique_id_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // User Avatar with Neon Cyan Glow
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(BangHyperCyan, BangNeonPurple)))
                                .border(1.5.dp, BangKineticLime, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AT",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "MY UNIQUE ID",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BangTextMuted,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BangKineticLime.copy(alpha = 0.18f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "● VERIFIED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BangKineticLime
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = identity.nodeId.ifEmpty { "BANG-NODE-7A3F-92C1" },
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.5.sp,
                                    color = BangKineticLime,
                                    letterSpacing = 0.5.sp
                                )
                            )

                            Text(
                                text = "@atlas_01 • Curve25519: 0x8F9A...66D3",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = BangHyperCyan
                                )
                            )
                        }
                    }

                    // Action buttons: Copy ID & Show My QR
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Copy ID Button
                        Button(
                            onClick = {
                                isCopied = true
                                onShowToast("Copied Unique Node ID: BANG-NODE-7A3F-92C1")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCopied) BangKineticLime.copy(alpha = 0.2f) else Color(0xFF101B2B),
                                contentColor = if (isCopied) BangKineticLime else BangHyperCyan
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 9.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp).testTag("btn_copy_unique_id")
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy ID",
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCopied) "COPIED" else "COPY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Show My QR Modal Button
                        Button(
                            onClick = { showMyQrDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BangHyperCyan,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 9.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp).testTag("btn_show_my_qr")
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "Show QR",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MY QR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
        // =====================================================================
        // 2. CENTRAL QR SCANNER WINDOW (Reticle, Camera Controls & Laser Sweep)
        // =====================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF04070D)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF162338)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("central_qr_scanner_window")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Viewfinder Top Control Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BangKineticLime)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFrontCamera) "FRONT CAM • SCANNER" else "REAR CAM • SCANNER ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = BangKineticLime,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    // Viewfinder Quick Controls: Grid, Flip Camera, Torch
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Grid Toggle Button
                        IconButton(
                            onClick = {
                                isGridOn = !isGridOn
                                onShowToast(if (isGridOn) "Framing Grid Enabled" else "Framing Grid Disabled")
                            },
                            modifier = Modifier.size(34.dp).testTag("btn_toggle_grid")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Toggle Grid",
                                tint = if (isGridOn) BangHyperCyan else BangTextMuted,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Flip Camera Button
                        IconButton(
                            onClick = {
                                isFrontCamera = !isFrontCamera
                                onShowToast(if (isFrontCamera) "Switched to Front Camera" else "Switched to Rear Camera")
                            },
                            modifier = Modifier.size(34.dp).testTag("btn_flip_camera")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Flip Camera",
                                tint = if (isFrontCamera) BangNeonHotPink else BangTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Flashlight / Torch Toggle
                        IconButton(
                            onClick = {
                                isFlashOn = !isFlashOn
                                onShowToast(if (isFlashOn) "Torch turned ON" else "Torch turned OFF")
                            },
                            modifier = Modifier.size(34.dp).testTag("btn_toggle_torch")
                        ) {
                            Icon(
                                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Torch",
                                tint = if (isFlashOn) BangAmberWarning else BangTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // The Central Viewfinder Window (Reticle)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isFrontCamera) Color(0xFF090B12) else Color(0xFF050912)
                        )
                        .border(1.5.dp, Color(0xFF1E2F4A), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Canvas: Corner Brackets, Optional Grid, Crosshairs & Laser Sweep
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val cornerLen = 34.dp.toPx()
                        val strokeW = 3.5.dp.toPx()
                        val bracketColor = if (isTargetLocked) BangKineticLime else BangHyperCyan

                        // 1. Optional 3x3 Rule-of-Thirds Grid
                        if (isGridOn) {
                            val thirdX = w / 3
                            val thirdY = h / 3
                            val gridColor = Color(0x1800E5FF)
                            drawLine(gridColor, Offset(thirdX, 0f), Offset(thirdX, h), 1.dp.toPx())
                            drawLine(gridColor, Offset(thirdX * 2, 0f), Offset(thirdX * 2, h), 1.dp.toPx())
                            drawLine(gridColor, Offset(0f, thirdY), Offset(w, thirdY), 1.dp.toPx())
                            drawLine(gridColor, Offset(0f, thirdY * 2), Offset(w, thirdY * 2), 1.dp.toPx())
                        }

                        // 2. Corner Brackets (Top-Left, Top-Right, Bottom-Left, Bottom-Right)
                        // Top-Left
                        drawLine(bracketColor, Offset(16.dp.toPx(), 16.dp.toPx()), Offset(16.dp.toPx() + cornerLen, 16.dp.toPx()), strokeW)
                        drawLine(bracketColor, Offset(16.dp.toPx(), 16.dp.toPx()), Offset(16.dp.toPx(), 16.dp.toPx() + cornerLen), strokeW)

                        // Top-Right
                        drawLine(bracketColor, Offset(w - 16.dp.toPx(), 16.dp.toPx()), Offset(w - 16.dp.toPx() - cornerLen, 16.dp.toPx()), strokeW)
                        drawLine(bracketColor, Offset(w - 16.dp.toPx(), 16.dp.toPx()), Offset(w - 16.dp.toPx(), 16.dp.toPx() + cornerLen), strokeW)

                        // Bottom-Left
                        drawLine(bracketColor, Offset(16.dp.toPx(), h - 16.dp.toPx()), Offset(16.dp.toPx() + cornerLen, h - 16.dp.toPx()), strokeW)
                        drawLine(bracketColor, Offset(16.dp.toPx(), h - 16.dp.toPx()), Offset(16.dp.toPx(), h - 16.dp.toPx() - cornerLen), strokeW)

                        // Bottom-Right
                        drawLine(bracketColor, Offset(w - 16.dp.toPx(), h - 16.dp.toPx()), Offset(w - 16.dp.toPx() - cornerLen, h - 16.dp.toPx()), strokeW)
                        drawLine(bracketColor, Offset(w - 16.dp.toPx(), h - 16.dp.toPx()), Offset(w - 16.dp.toPx(), h - 16.dp.toPx() - cornerLen), strokeW)

                        // 3. Center Crosshair Reticle
                        val cx = w / 2
                        val cy = h / 2
                        val crosshair = 10.dp.toPx()
                        drawLine(Color(0x4400E5FF), Offset(cx - crosshair, cy), Offset(cx + crosshair, cy), 1.5.dp.toPx())
                        drawLine(Color(0x4400E5FF), Offset(cx, cy - crosshair), Offset(cx, cy + crosshair), 1.5.dp.toPx())

                        // 4. Animated Scanning Laser Bar
                        val currentY = h * laserY
                        drawLine(
                            color = BangKineticLime,
                            start = Offset(24.dp.toPx(), currentY),
                            end = Offset(w - 24.dp.toPx(), currentY),
                            strokeWidth = 2.8.dp.toPx()
                        )
                        // Laser glow shadow
                        drawRect(
                            brush = Brush.verticalGradient(
                                listOf(BangKineticLime.copy(alpha = 0.35f), Color.Transparent),
                                startY = currentY - 16.dp.toPx(),
                                endY = currentY
                            ),
                            topLeft = Offset(24.dp.toPx(), currentY - 16.dp.toPx()),
                            size = Size(w - 48.dp.toPx(), 16.dp.toPx())
                        )
                    }

                    // Viewfinder Center Overlay: QR Frame & Target Lock Indicator
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (isTargetLocked) {
                            // Target Locked Reticle Box
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x6608140E))
                                    .border(1.dp, BangKineticLime.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = BangKineticLime,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "TARGET ACQUIRED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                color = BangKineticLime,
                                                letterSpacing = 1.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${currentTargetPeer.displayName} (${currentTargetPeer.handle})",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = "Curve25519 • Distance: ${currentTargetPeer.distanceMeters}m",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.5.sp,
                                            color = BangHyperCyan
                                        )
                                    )
                                }
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color(0x3300E5FF),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ALIGN PEER'S QR CODE INSIDE WINDOW",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BangTextMuted,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Telemetry Strip
                Row(
                    modifier = Modifier.fillMaxWidth(0.92f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FPS: 60 • 1080P",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = BangTextMuted
                        )
                    )
                    Text(
                        text = "NOISE-XK (CURVE25519)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = BangHyperCyan
                        )
                    )
                    Text(
                        text = "BLE 5.2 L2CAP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = BangKineticLime
                        )
                    )
                }
            }
        }

        // =====================================================================
        // 3. NFC TRIGGER BUTTON FOR PEER DISCOVERY (Prominent tactile trigger)
        // =====================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF09141E)),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, BangKineticLime.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showNfcBumpDialog = true
                }
                .testTag("btn_nfc_trigger_discovery")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Pulsing Contactless NFC Antenna Icon
                    Box(
                        modifier = Modifier
                            .size(46.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxR = size.width / 2
                            drawCircle(
                                color = BangKineticLime.copy(alpha = (1f - nfcPulseRing).coerceIn(0.1f, 0.7f)),
                                radius = maxR * nfcPulseRing,
                                style = Stroke(width = 1.8.dp.toPx())
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(BangKineticLime, WhatsAppGreen))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contactless,
                                contentDescription = "NFC Trigger",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⚡ TAP TO DISCOVER VIA NFC",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.5.sp,
                                    color = BangKineticLime,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                        Text(
                            text = "Instant contactless peer pairing • 4cm proximity bump",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = BangTextMuted
                            )
                        )
                    }
                }

                // Tactile Trigger Action Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BangKineticLime)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "TRIGGER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                    )
                }
            }
        }

        // =====================================================================
        // 4. DETECTED PEER ACTION CARD & NEARBY DISCOVERY FEED
        // =====================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070F1A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, BangHyperCyan.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().testTag("discovered_peer_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(BangHyperCyan, BangNeonPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentTargetPeer.displayName.take(2).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentTargetPeer.displayName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = BangTextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BangKineticLime, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = "${currentTargetPeer.handle} • Signal: Strong (${currentTargetPeer.rssi} dBm)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.5.sp,
                                    color = BangHyperCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }

                    // Distance Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF132235))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${currentTargetPeer.distanceMeters}m away",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = BangKineticLime
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Curve25519 Key: 8f9a...32c1 • Zero-Trust Noise-XK Handshake Ready",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = BangTextMuted
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onStartChatWithPeer(currentTargetPeer) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BangHyperCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("btn_start_direct_chat")
                    ) {
                        Text(
                            text = "⚡ START DIRECT CHAT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Button(
                        onClick = {
                            peerIndex++
                            isTargetLocked = true
                            onShowToast("Target acquired: ${activePeersList[(peerIndex) % activePeersList.size].displayName}")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF132235),
                            contentColor = BangHyperCyan
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(40.dp).testTag("btn_cycle_peer")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("NEXT PEER", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // =========================================================================
    // MODAL DIALOG 1: SHOW MY QR CODE MODAL
    // =========================================================================
    if (showMyQrDialog) {
        Dialog(onDismissRequest = { showMyQrDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF05080E)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, BangHyperCyan),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MY NODE QR CODE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = BangHyperCyan,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = "Allow nearby peers to scan your identity",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = BangTextMuted
                                )
                            )
                        }
                        IconButton(
                            onClick = { showMyQrDialog = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BangTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Matrix Canvas
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF030509))
                            .border(1.dp, BangKineticLime, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val gridSize = 19
                            val cellSize = size.width / gridSize
                            for (r in 0 until gridSize) {
                                for (c in 0 until gridSize) {
                                    val isTopLeftFinder = (r in 0..5 && c in 0..5)
                                    val isTopRightFinder = (r in 0..5 && c >= gridSize - 6)
                                    val isBottomLeftFinder = (r >= gridSize - 6 && c in 0..5)

                                    val shouldDraw = when {
                                        isTopLeftFinder -> (r == 0 || r == 5 || c == 0 || c == 5 || (r in 2..3 && c in 2..3))
                                        isTopRightFinder -> {
                                            val tc = c - (gridSize - 6)
                                            (r == 0 || r == 5 || tc == 0 || tc == 5 || (r in 2..3 && tc in 2..3))
                                        }
                                        isBottomLeftFinder -> {
                                            val tr = r - (gridSize - 6)
                                            (tr == 0 || tr == 5 || c == 0 || c == 5 || (tr in 2..3 && c in 2..3))
                                        }
                                        else -> ((r * 31 + c * 17) % 3 == 0) || ((r + c) % 4 == 0)
                                    }

                                    if (shouldDraw) {
                                        val color = if (isTopLeftFinder || isTopRightFinder || isBottomLeftFinder) {
                                            BangHyperCyan
                                        } else {
                                            if ((r + c) % 5 == 0) BangKineticLime else Color(0xFFD6F5FF)
                                        }
                                        drawRect(
                                            color = color,
                                            topLeft = Offset(c * cellSize, r * cellSize),
                                            size = Size(cellSize - 1.2f, cellSize - 1.2f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = identity.nodeId.ifEmpty { "BANG-NODE-7A3F-92C1" },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = BangKineticLime,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "Curve25519: 0x8F9A-77C1-44B2-99EA",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = BangTextMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            showMyQrDialog = false
                            onShowToast("Identity QR link copied")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BangHyperCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text("DONE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }

    // =========================================================================
    // MODAL DIALOG 2: INTERACTIVE NFC BUMP PEER DISCOVERY MODAL
    // =========================================================================
    if (showNfcBumpDialog) {
        Dialog(onDismissRequest = { showNfcBumpDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF05080E)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, BangKineticLime),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                var bumpSuccess by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NFC PEER DISCOVERY",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = BangKineticLime,
                                fontSize = 14.sp
                            )
                        )
                        IconButton(
                            onClick = { showNfcBumpDialog = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BangTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated Contactless Graphic
                    Box(
                        modifier = Modifier.size(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxR = size.width / 2
                            drawCircle(
                                color = if (bumpSuccess) BangKineticLime.copy(alpha = 0.5f) else BangHyperCyan.copy(alpha = 0.4f),
                                radius = maxR * 0.85f,
                                style = Stroke(width = 2.dp.toPx())
                            )
                            drawCircle(
                                color = if (bumpSuccess) BangKineticLime.copy(alpha = 0.8f) else BangHyperCyan.copy(alpha = 0.7f),
                                radius = maxR * 0.55f,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    if (bumpSuccess) BangKineticLime else BangHyperCyan
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contactless,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (bumpSuccess) "✓ PEER DISCOVERED & VERIFIED" else "HOLD PHONES BACK-TO-BACK",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = if (bumpSuccess) BangKineticLime else Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (bumpSuccess)
                            "Curve25519 Keys exchanged with ${currentTargetPeer.displayName} via NFC hardware."
                        else
                            "Touch phone backs within 4cm to exchange zero-trust keys out-of-band.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.5.sp,
                            color = BangTextMuted,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    if (!bumpSuccess) {
                        Button(
                            onClick = {
                                bumpSuccess = true
                                onShowToast("✓ NFC Handshake Verified with ${currentTargetPeer.displayName}")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BangKineticLime,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Icon(Icons.Default.Contactless, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SIMULATE BUMP (TAP PHONES)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                showNfcBumpDialog = false
                                onStartChatWithPeer(currentTargetPeer)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BangKineticLime,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text("START ENCRYPTED CHAT", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// SUB-TAB 1: USER ID CARD VIEW (Holographic Obsidian Identity Card & QR Code)
// =============================================================================
@Composable
private fun UserIdCardView(
    identity: com.example.model.MyIdentity,
    onShowToast: (String) -> Unit
) {
    var isCopied by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Holographic Cryptographic Identity Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF05080E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, BangHyperCyan.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header of Card
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
                                .background(Brush.linearGradient(listOf(BangNeonHotPink, BangNeonPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ATLAS-01",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "@atlas_01 • Node ID Master",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = BangHyperCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BangKineticLime.copy(alpha = 0.15f))
                            .border(0.5.dp, BangKineticLime, RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "● VERIFIED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BangKineticLime,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High-Fidelity Canvas QR Matrix
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF030509))
                        .border(1.dp, BangKineticLime.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridSize = 19
                        val cellSize = size.width / gridSize

                        // Data Pattern Simulation with Finder Patterns
                        for (r in 0 until gridSize) {
                            for (c in 0 until gridSize) {
                                val isTopLeftFinder = (r in 0..5 && c in 0..5)
                                val isTopRightFinder = (r in 0..5 && c >= gridSize - 6)
                                val isBottomLeftFinder = (r >= gridSize - 6 && c in 0..5)

                                val shouldDraw = when {
                                    isTopLeftFinder -> (r == 0 || r == 5 || c == 0 || c == 5 || (r in 2..3 && c in 2..3))
                                    isTopRightFinder -> {
                                        val tc = c - (gridSize - 6)
                                        (r == 0 || r == 5 || tc == 0 || tc == 5 || (r in 2..3 && tc in 2..3))
                                    }
                                    isBottomLeftFinder -> {
                                        val tr = r - (gridSize - 6)
                                        (tr == 0 || tr == 5 || c == 0 || c == 5 || (tr in 2..3 && c in 2..3))
                                    }
                                    else -> {
                                        ((r * 31 + c * 17) % 3 == 0) || ((r + c) % 4 == 0)
                                    }
                                }

                                if (shouldDraw) {
                                    val color = if (isTopLeftFinder || isTopRightFinder || isBottomLeftFinder) {
                                        BangHyperCyan
                                    } else {
                                        if ((r + c) % 5 == 0) BangKineticLime else Color(0xFFD6F5FF)
                                    }

                                    drawRect(
                                        color = color,
                                        topLeft = Offset(c * cellSize, r * cellSize),
                                        size = Size(cellSize - 1.2f, cellSize - 1.2f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cryptographic Key Fingerprint Panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0B1019))
                        .border(0.5.dp, Color(0xFF1E2D44), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "NODE ID FINGERPRINT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BangTextMuted
                                )
                            )
                            Text(
                                text = "Curve25519",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = BangHyperCyan
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "0x8F9A-77C1-44B2-99EA-66D3",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BangKineticLime
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Public Key: ed25519:e87d...11c2 (Noise Protocol XK)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.5.sp,
                                color = BangTextMuted
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Copy Key, Share Identity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            isCopied = true
                            onShowToast("Public Key copied to clipboard")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF122238),
                            contentColor = BangHyperCyan
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCopied) "COPIED" else "COPY KEY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Button(
                        onClick = {
                            onShowToast("Identity Card exported for zero-trust pairing")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BangHyperCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SHARE ID",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// SUB-TAB 2: NFC BUMP VIEW (Contactless Key Exchange & Phone Tap Simulation)
// =============================================================================
@Composable
private fun NfcBumpView(
    peers: List<MeshPeerNode>,
    onStartChatWithPeer: (MeshPeerNode) -> Unit,
    onShowToast: (String) -> Unit
) {
    var isBumping by remember { mutableStateOf(false) }
    var nfcSuccess by remember { mutableStateOf(false) }
    val targetPeer = peers.firstOrNull { it.displayName.contains("Alex Rivera") } ?: peers.firstOrNull()

    val infiniteTransition = rememberInfiniteTransition(label = "nfcPulse")
    val pulseRing by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "nfcRing"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // NFC Graphic & Antenna Simulation Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF05080E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF182436)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEAR-FIELD KEY EXCHANGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = BangHyperCyan
                        )
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BangKineticLime.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "NFC ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BangKineticLime,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Pulsing NFC Electromagnetic Antenna Graphic
                Box(
                    modifier = Modifier
                        .size(170.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2
                        val cy = size.height / 2
                        val maxR = size.width / 2

                        // Concentric expanding NFC waves
                        val radii = listOf(maxR * 0.4f, maxR * 0.7f, maxR * pulseRing)
                        radii.forEachIndexed { i, r ->
                            drawCircle(
                                color = if (nfcSuccess) BangKineticLime.copy(alpha = (1f - (r / maxR)).coerceIn(0.1f, 0.8f))
                                else BangHyperCyan.copy(alpha = (1f - (r / maxR)).coerceIn(0.1f, 0.8f)),
                                radius = r,
                                center = Offset(cx, cy),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (nfcSuccess) listOf(BangKineticLime, WhatsAppGreen)
                                    else listOf(BangHyperCyan, BangNeonPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Contactless,
                            contentDescription = "NFC",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (nfcSuccess) "✓ NFC PAIRING SUCCESSFUL" else "HOLD PHONES BACK-TO-BACK",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = if (nfcSuccess) BangKineticLime else Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (nfcSuccess)
                        "Curve25519 Ephemeral noise keys exchanged out-of-band via NFC hardware. Zero RF leakage."
                    else
                        "Tap or bring phones within 4cm to exchange Noise Protocol XK public keys instantly.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.5.sp,
                        color = BangTextMuted,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Primary NFC Action Button
                Button(
                    onClick = {
                        isBumping = true
                        nfcSuccess = true
                        onShowToast("✓ NFC Bump Verified: Noise Protocol Keys Exchanged!")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Brush.linearGradient(listOf(BangHyperCyan, BangNeonHotPink)).let {
                            if (nfcSuccess) BangKineticLime else BangHyperCyan
                        },
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_simulate_nfc_bump")
                ) {
                    Icon(imageVector = Icons.Default.Contactless, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (nfcSuccess) "✓ NFC HANDSHAKE VERIFIED" else "⚡ SIMULATE NFC BUMP (TAP PHONES)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Verified Peer Card (Revealed after NFC Handshake)
        if (nfcSuccess && targetPeer != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF09140E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangKineticLime),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(BangKineticLime),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = targetPeer.displayName.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = targetPeer.displayName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BangTextPrimary
                                    )
                                )
                                Text(
                                    text = "${targetPeer.handle} • Out-of-Band NFC Authenticated",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = BangKineticLime
                                    )
                                )
                            }
                        }

                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BangKineticLime, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Curve25519 Public Key: 8f9a...32c1 (Signed via Contactless NFC Chip)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = BangTextMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onStartChatWithPeer(targetPeer) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BangKineticLime,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text("OPEN SECURE CHAT", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
