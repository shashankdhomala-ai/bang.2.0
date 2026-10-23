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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MeshPeerNode
import com.example.model.MyIdentity
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.CyberElectricCyan
import com.example.ui.theme.CyberKineticEmerald
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberSlateBg
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

/**
 * Interactive Modal Spec: "Scan & ID" Bottom Sheet Modal
 *
 * Triggered via the Center Navigation Bar action:
 * - Top Half: Camera viewport with live QR code scanning reticle & animated laser sweep.
 * - Center Bar: User's Unique ID & Curve25519 key info with quick-copy.
 * - Bottom Half: Animated pulsing NFC wave graphic with instructions:
 *   "Tap phones together to swap Node Keys & @username profile card".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanAndIdBottomSheetModal(
    identity: MyIdentity,
    peers: List<MeshPeerNode>,
    onDismiss: () -> Unit,
    onStartChatWithPeer: (MeshPeerNode) -> Unit,
    onShowToast: (String) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isFlashOn by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var isGridOn by remember { mutableStateOf(true) }
    var isCopied by remember { mutableStateOf(false) }
    var nfcBumpSuccess by remember { mutableStateOf(false) }

    val targetPeer = remember(peers) {
        peers.firstOrNull() ?: MeshPeerNode(
            id = "peer_alex_direct",
            displayName = "Alex Rivera",
            rssi = -46,
            distanceMeters = 1.4f,
            angleDegrees = 45f,
            batteryPct = 88,
            hopsAway = 1,
            transport = com.example.model.TransportType.BLE_L2CAP,
            packetsRelayed = 142,
            isVerified = true,
            handle = "@alex_rivera"
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scannerLaserAnim")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    val nfcPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "nfcPulse"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CyberSlateBg,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(CyberCardBorder)
            )
        },
        modifier = Modifier.testTag("scan_and_id_bottom_sheet_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyberElectricCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SCAN & ID • DUAL-MODE LINK",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = CyberElectricCyan,
                            letterSpacing = 0.8.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = CyberTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =================================================================
            // TOP HALF: LIVE CAMERA VIEWPORT FOR QR SCANNING
            // =================================================================
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF04070D)),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("modal_camera_qr_viewport")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Viewport Control Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(CyberKineticEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFrontCamera) "FRONT CAM • SCANNER" else "REAR CAM • ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberKineticEmerald
                                )
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Grid Toggle
                            IconButton(
                                onClick = { isGridOn = !isGridOn },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Grid",
                                    tint = if (isGridOn) CyberElectricCyan else CyberTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            // Flip Camera
                            IconButton(
                                onClick = { isFrontCamera = !isFrontCamera },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Flip",
                                    tint = CyberElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            // Torch
                            IconButton(
                                onClick = {
                                    isFlashOn = !isFlashOn
                                    onShowToast(if (isFlashOn) "Torch ON" else "Torch OFF")
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Torch",
                                    tint = if (isFlashOn) CyberNeonAmber else CyberTextMuted,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Viewfinder Screen (1:1 Ratio)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .aspectRatio(1.2f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF030509))
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Custom Canvas: Corner Reticles & Laser
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val cornerLen = 28.dp.toPx()
                            val strokeW = 3.dp.toPx()
                            val bracketColor = CyberElectricCyan

                            // Optional 3x3 Grid
                            if (isGridOn) {
                                val tx = w / 3
                                val ty = h / 3
                                val gridColor = Color(0x1800E5FF)
                                drawLine(gridColor, Offset(tx, 0f), Offset(tx, h), 1.dp.toPx())
                                drawLine(gridColor, Offset(tx * 2, 0f), Offset(tx * 2, h), 1.dp.toPx())
                                drawLine(gridColor, Offset(0f, ty), Offset(w, ty), 1.dp.toPx())
                                drawLine(gridColor, Offset(0f, ty * 2), Offset(w, ty * 2), 1.dp.toPx())
                            }

                            // 4 Corner Brackets
                            // Top-Left
                            drawLine(bracketColor, Offset(12.dp.toPx(), 12.dp.toPx()), Offset(12.dp.toPx() + cornerLen, 12.dp.toPx()), strokeW)
                            drawLine(bracketColor, Offset(12.dp.toPx(), 12.dp.toPx()), Offset(12.dp.toPx(), 12.dp.toPx() + cornerLen), strokeW)
                            // Top-Right
                            drawLine(bracketColor, Offset(w - 12.dp.toPx(), 12.dp.toPx()), Offset(w - 12.dp.toPx() - cornerLen, 12.dp.toPx()), strokeW)
                            drawLine(bracketColor, Offset(w - 12.dp.toPx(), 12.dp.toPx()), Offset(w - 12.dp.toPx(), 12.dp.toPx() + cornerLen), strokeW)
                            // Bottom-Left
                            drawLine(bracketColor, Offset(12.dp.toPx(), h - 12.dp.toPx()), Offset(12.dp.toPx() + cornerLen, h - 12.dp.toPx()), strokeW)
                            drawLine(bracketColor, Offset(12.dp.toPx(), h - 12.dp.toPx()), Offset(12.dp.toPx(), h - 12.dp.toPx() - cornerLen), strokeW)
                            // Bottom-Right
                            drawLine(bracketColor, Offset(w - 12.dp.toPx(), h - 12.dp.toPx()), Offset(w - 12.dp.toPx() - cornerLen, h - 12.dp.toPx()), strokeW)
                            drawLine(bracketColor, Offset(w - 12.dp.toPx(), h - 12.dp.toPx()), Offset(w - 12.dp.toPx(), h - 12.dp.toPx() - cornerLen), strokeW)

                            // Animated Laser Beam
                            val curY = h * laserY
                            drawLine(CyberKineticEmerald, Offset(20.dp.toPx(), curY), Offset(w - 20.dp.toPx(), curY), 2.5.dp.toPx())
                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(CyberKineticEmerald.copy(alpha = 0.3f), Color.Transparent),
                                    startY = curY - 14.dp.toPx(),
                                    endY = curY
                                ),
                                topLeft = Offset(20.dp.toPx(), curY - 14.dp.toPx()),
                                size = Size(w - 40.dp.toPx(), 14.dp.toPx())
                            )
                        }

                        // Target Overlay Info
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x7707140B))
                                    .border(1.dp, CyberKineticEmerald.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = CyberKineticEmerald,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "READY • ALIGN PEER QR CODE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp,
                                            color = CyberKineticEmerald
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Curve25519 • Noise-XK Zero-Trust Handshake",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = CyberTextMuted
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =================================================================
            // USER UNIQUE ID COMPACT BANNER
            // =================================================================
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyberElectricCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ID",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = Color.Black
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "YOUR NODE ID",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.5.sp,
                                        color = CyberTextMuted
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "● VERIFIED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberKineticEmerald
                                    )
                                )
                            }
                            Text(
                                text = identity.nodeId.ifEmpty { "BANG-NODE-7A3F-92C1" },
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CyberElectricCyan
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            isCopied = true
                            onShowToast("Node ID Copied")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCopied) CyberKineticEmerald.copy(alpha = 0.2f) else Color(0xFF162338),
                            contentColor = if (isCopied) CyberKineticEmerald else CyberElectricCyan
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isCopied) "COPIED" else "COPY", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =================================================================
            // BOTTOM HALF: ANIMATED PULSING NFC WAVE GRAPHIC
            // =================================================================
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberKineticEmerald.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("modal_nfc_pulse_section")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Concentric Electromagnetic Wave Graphic
                    Box(
                        modifier = Modifier.size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxR = size.width / 2
                            drawCircle(
                                color = if (nfcBumpSuccess) CyberKineticEmerald.copy(alpha = 0.5f) else CyberKineticEmerald.copy(alpha = (1f - nfcPulse).coerceIn(0.1f, 0.7f)),
                                radius = maxR * nfcPulse,
                                style = Stroke(width = 2.dp.toPx())
                            )
                            drawCircle(
                                color = CyberElectricCyan.copy(alpha = 0.3f),
                                radius = maxR * 0.5f,
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        if (nfcBumpSuccess) listOf(CyberKineticEmerald, CyberElectricCyan)
                                        else listOf(CyberKineticEmerald, CyberNeonAmber)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contactless,
                                contentDescription = "NFC Wave",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (nfcBumpSuccess) "✓ NFC PEER DISCOVERED & VERIFIED" else "TAP PHONES TOGETHER TO CONNECT",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp,
                            color = if (nfcBumpSuccess) CyberKineticEmerald else CyberTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (nfcBumpSuccess)
                            "Curve25519 identity key verified with ${targetPeer.displayName} (${targetPeer.handle})."
                        else
                            "Tap phones together to swap Node Keys & @username profile card",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = CyberTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!nfcBumpSuccess) {
                        Button(
                            onClick = {
                                nfcBumpSuccess = true
                                onShowToast("✓ NFC Bump verified with ${targetPeer.displayName}")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberKineticEmerald,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("btn_modal_simulate_nfc_bump")
                        ) {
                            Icon(Icons.Default.Contactless, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SIMULATE NFC BUMP (TAP PHONES)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                onDismiss()
                                onStartChatWithPeer(targetPeer)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberElectricCyan,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("btn_modal_start_chat")
                        ) {
                            Text(
                                text = "⚡ START DIRECT MESH CHAT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
