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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BangUiState
import com.example.ui.theme.BangCharcoalBorder
import com.example.ui.theme.BangCharcoalSurface
import com.example.ui.theme.BangElectricKineticLime
import com.example.ui.theme.BangObsidianBg
import com.example.ui.theme.BangStatusOnline

/**
 * Screen 7: Identity Exchange & NFC Bump Screen
 * Matches Phone 7 of the production design layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrAndNfcScreen(
    uiState: BangUiState,
    onBack: (() -> Unit)? = null,
    onShareQr: () -> Unit = {},
    onSaveImage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedSegment by remember { mutableStateOf("My QR") } // My QR, Scan, NFC Tap
    var showNfcBumpSuccessModal by remember { mutableStateOf(false) }
    var shareFeedback by remember { mutableStateOf<String?>(null) }

    // Pulsing NFC wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "nfcPulse")
    val nfcPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nfcAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidianBg)
            .testTag("qr_and_nfc_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    text = "Your QR Code",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(start = if (onBack != null) 0.dp else 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Segmented Control: [ My QR | Scan | ⚡ NFC Tap ]
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF131B28),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    val segments = listOf("My QR", "Scan", "⚡ NFC Tap")
                    segments.forEach { seg ->
                        val isSelected = selectedSegment == seg
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFF3B286D) else Color.Transparent)
                                .clickable { selectedSegment = seg },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = seg,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            when (selectedSegment) {
                "My QR" -> {
                    // 3. Stylized QR Card with Neon Green Corner Brackets
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BangCharcoalSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // QR Matrix with glowing corner brackets
                            Box(
                                modifier = Modifier
                                    .size(220.dp)
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Real QR Matrix Draw
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val matrixSize = 25
                                    val cellSize = size.width / matrixSize
                                    val seed = 42

                                    // Draw Finder patterns in 3 corners
                                    fun drawFinder(startX: Int, startY: Int) {
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = Offset(startX * cellSize, startY * cellSize),
                                            size = Size(7 * cellSize, 7 * cellSize)
                                        )
                                        drawRect(
                                            color = Color.White,
                                            topLeft = Offset((startX + 1) * cellSize, (startY + 1) * cellSize),
                                            size = Size(5 * cellSize, 5 * cellSize)
                                        )
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = Offset((startX + 2) * cellSize, (startY + 2) * cellSize),
                                            size = Size(3 * cellSize, 3 * cellSize)
                                        )
                                    }

                                    drawFinder(0, 0)
                                    drawFinder(matrixSize - 7, 0)
                                    drawFinder(0, matrixSize - 7)

                                    // Fill pseudo-random data cells
                                    for (x in 0 until matrixSize) {
                                        for (y in 0 until matrixSize) {
                                            val inFinder = (x < 7 && y < 7) || (x >= matrixSize - 7 && y < 7) || (x < 7 && y >= matrixSize - 7)
                                            val inCenter = x in 10..14 && y in 10..14
                                            if (!inFinder && !inCenter) {
                                                val bit = ((x * 37 + y * 19 + seed) % 7) > 3
                                                if (bit) {
                                                    drawRect(
                                                        color = Color.Black,
                                                        topLeft = Offset(x * cellSize, y * cellSize),
                                                        size = Size(cellSize, cellSize)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Center BANG lightning bolt badge
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF070B11))
                                        .border(2.dp, BangElectricKineticLime, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "BANG",
                                        tint = BangElectricKineticLime,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "@uma_bang",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Scan to connect",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                            Text(
                                text = "ed25519:7a4f..9b12",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = BangElectricKineticLime
                                ),
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 4. Action Buttons: [ Share QR ] and [ Save Image ]
                    Row(
                        modifier = Modifier.fillMaxWidth(0.88f),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                shareFeedback = "QR Code public key copied to clipboard"
                                onShareQr()
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = BangCharcoalSurface,
                                contentColor = Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Share QR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                shareFeedback = "Saved QR code to device gallery"
                                onSaveImage()
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = BangCharcoalSurface,
                                contentColor = Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save Image",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }
                    }

                    if (shareFeedback != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = shareFeedback ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BangElectricKineticLime,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                "Scan" -> {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BangCharcoalSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(340.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Camera Scanner",
                                tint = BangElectricKineticLime,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Align QR Code within Frame",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Directly extracts and verifies peer Ed25519 identity key without cellular connection.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF94A3B8),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { showNfcBumpSuccessModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BangElectricKineticLime)
                            ) {
                                Text("Simulate Scanned Peer", color = Color(0xFF070B11), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                "⚡ NFC Tap" -> {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BangCharcoalSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(340.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(BangElectricKineticLime.copy(alpha = nfcPulseAlpha * 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Contactless,
                                    contentDescription = "NFC Tap",
                                    tint = BangElectricKineticLime,
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "Touch Phones Back-to-Back",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Hardware NFC antenna active. Swaps Ed25519 public keys and verifies identity in under 200ms.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF94A3B8),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { showNfcBumpSuccessModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BangElectricKineticLime)
                            ) {
                                Text("Simulate NFC Bump Handshake", color = Color(0xFF070B11), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Instant bottom-sheet profile pop-up on NFC bump / QR scan
        if (showNfcBumpSuccessModal) {
            ModalBottomSheet(
                onDismissRequest = { showNfcBumpSuccessModal = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = BangCharcoalSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(BangElectricKineticLime.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BangElectricKineticLime,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Instant Peer Key Exchanged!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Swapped Ed25519 keypair with @alex over local radio hardware.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF94A3B8),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF070B11),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "VERIFIED PEER FINGERPRINT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = Color(0xFF64748B)
                                )
                            )
                            Text(
                                text = "ed25519:4a8c:99fa:10de:72bb:3e91",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = BangElectricKineticLime
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showNfcBumpSuccessModal = false },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BangElectricKineticLime),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Start Secure Mesh Chat", color = Color(0xFF070B11), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
