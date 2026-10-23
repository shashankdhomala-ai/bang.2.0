package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RelaySimulationRole
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
import com.example.ui.theme.BangTextSecondary

@Composable
fun ZeroTrustRelayScreen(
    uiState: BangUiState,
    onRunSimulation: () -> Unit,
    onCustomTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStepIndex by remember { mutableStateOf(2) } // Defaults to Bob (the stranger) for inspection!

    val infiniteTransition = rememberInfiniteTransition(label = "pulseRelay")
    val hopPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hopPulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BangBackground)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = BangPrimaryNeon,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "A ➔ B ➔ C ZERO-TRUST RELAY",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        ),
                        color = BangTextPrimary
                    )
                    Text(
                        text = "A uses Stranger B's phone without B knowing sender, recipient, or text",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = BangCyanAccent
                    )
                }
            }
        }

        // Live Topology Diagram: Node A -> Node B (Stranger) -> Node C
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangPrimaryNeon.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PHYSICAL HOP TOPOLOGY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            letterSpacing = 1.sp
                        ),
                        color = BangTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Three Interactive Node Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Node A
                        NodeTopologyPill(
                            title = "Node A",
                            subtitle = "Alice (You)",
                            badge = "SENDER",
                            badgeColor = BangCyanAccent,
                            isSelected = selectedStepIndex == 1,
                            isActive = uiState.relaySimulationStep >= 1,
                            onClick = { selectedStepIndex = 1 }
                        )

                        // Hop 1 Arrow (BLE/Wi-Fi)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = if (uiState.relaySimulationStep >= 2) BangPrimaryNeon else BangTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Wi-Fi P2P",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = BangPrimaryNeon
                            )
                        }

                        // Node B: The Stranger
                        NodeTopologyPill(
                            title = "Node B",
                            subtitle = "Bob (Stranger)",
                            badge = "STRANGER RELAY",
                            badgeColor = BangAmberWarning,
                            isSelected = selectedStepIndex == 2,
                            isActive = uiState.relaySimulationStep >= 2,
                            pulseScale = if (uiState.relaySimulationStep == 2 && uiState.relaySimulationActive) hopPulse else 1f,
                            onClick = { selectedStepIndex = 2 }
                        )

                        // Hop 2 Arrow
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = if (uiState.relaySimulationStep >= 3) BangPrimaryNeon else BangTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Wi-Fi Direct",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = BangPrimaryNeon
                            )
                        }

                        // Node C
                        NodeTopologyPill(
                            title = "Node C",
                            subtitle = "Charlie (Friend)",
                            badge = "RECIPIENT",
                            badgeColor = BangPrimaryNeon,
                            isSelected = selectedStepIndex == 3,
                            isActive = uiState.relaySimulationStep >= 3,
                            onClick = { selectedStepIndex = 3 }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Automatic Switch to Wi-Fi Notice Pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BangSurface)
                            .border(1.dp, BangPrimaryNeon.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = BangPrimaryNeon,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUTO-SWITCH ACTIVE: Switched from BLE to Wi-Fi Direct (250 Mbps) for instant 14ms hop.",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = BangPrimaryNeon,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Inspection Details for Selected Node
        val currentStep = uiState.relaySimSteps.find { it.stepIndex == selectedStepIndex }
        currentStep?.let { step ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (step.role == RelaySimulationRole.NODE_B_STRANGER_RELAY) BangSurfaceVariant else BangSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (step.role == RelaySimulationRole.NODE_B_STRANGER_RELAY) BangAmberWarning else BangCyanAccent
                    ),
                    shape = RoundedCornerShape(16.dp)
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
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (step.role == RelaySimulationRole.NODE_B_STRANGER_RELAY) BangAmberWarning.copy(alpha = 0.2f) else BangCyanAccent.copy(alpha = 0.2f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (step.role == RelaySimulationRole.NODE_B_STRANGER_RELAY) Icons.Default.PersonOff else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (step.role == RelaySimulationRole.NODE_B_STRANGER_RELAY) BangAmberWarning else BangCyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "DEVICE PERSPECTIVE: ${step.nodeName}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = BangTextPrimary
                                    )
                                    Text(
                                        text = if (step.isFriend) "Trusted End-Node" else "UNTRUSTED BYSTANDER PHONE (NOT A FRIEND)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (step.isFriend) BangPrimaryNeon else BangAmberWarning
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // What this device can see
                        Text(
                            text = "WHAT THIS PHONE SEES IN MEMORY & STORAGE:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = BangTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BangSurface)
                                .border(1.dp, BangSurfaceBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = step.visiblePayload,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (step.canReadContent) BangPrimaryNeon else BangSosRed
                                    )
                                )
                                if (!step.canReadContent) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🔒 CIPHERTEXT ONLY: Bob's hardware has no decrypt key. Reading is mathematically impossible.",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = BangTextMuted)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Security Privacy Checklist
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            PrivacyCheckRow(
                                label = "Can read message text?",
                                isYes = step.canReadContent,
                                negativeNotice = "Blocked by E2EE (ChaCha20-Poly1305)"
                            )
                            PrivacyCheckRow(
                                label = "Knows who sent it (Alice)?",
                                isYes = step.knowsOriginalSender,
                                negativeNotice = "Hidden (Random rotating nonce)"
                            )
                            PrivacyCheckRow(
                                label = "Knows who receives it (Charlie)?",
                                isYes = step.knowsFinalRecipient,
                                negativeNotice = "Hidden (Opaque envelope token #${step.tokenSeen})"
                            )
                            PrivacyCheckRow(
                                label = "Anti-DoS Micro-PoW verified?",
                                isYes = step.powVerified,
                                negativeNotice = "PoW Check Failed"
                            )
                            if (step.role == RelaySimulationRole.NODE_C_RECIPIENT) {
                                PrivacyCheckRow(
                                    label = "Cryptographic Delivery Receipt (ACK)?",
                                    isYes = true,
                                    negativeNotice = "Pending"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Autonomous Action
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BangSurface)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "BACKGROUND PROTOCOL ACTION:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = BangCyanAccent
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = step.actionTaken,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = BangTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Simulation Controller
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangSurfaceBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "TEST SIMULATION PAYLOAD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        ),
                        color = BangTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = uiState.simCustomText,
                        onValueChange = onCustomTextChanged,
                        placeholder = { Text("Type custom message from Alice…") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sim_custom_text_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BangSurface,
                            unfocusedContainerColor = BangSurface,
                            focusedBorderColor = BangPrimaryNeon,
                            unfocusedBorderColor = BangSurfaceBorder,
                            focusedTextColor = BangTextPrimary,
                            unfocusedTextColor = BangTextPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onRunSimulation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("run_simulation_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BangPrimaryNeon,
                            contentColor = BangBackground
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !uiState.relaySimulationActive
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.relaySimulationActive) "RELAYING: STEP ${uiState.relaySimulationStep}/3…" else "Run A ➔ B ➔ C Zero-Trust Hop",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
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
}

@Composable
private fun NodeTopologyPill(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    isSelected: Boolean,
    isActive: Boolean,
    pulseScale: Float = 1f,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .scale(pulseScale)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) BangSurface else BangSurfaceVariant)
            .border(
                1.5.dp,
                if (isSelected) badgeColor else BangSurfaceBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor.copy(alpha = 0.2f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = badgeColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                ),
                color = if (isActive) BangTextPrimary else BangTextMuted
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = BangTextSecondary
            )
        }
    }
}

@Composable
private fun PrivacyCheckRow(label: String, isYes: Boolean, negativeNotice: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = BangTextSecondary
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isYes) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = BangPrimaryNeon,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "YES (Owner)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = BangPrimaryNeon,
                        fontWeight = FontWeight.Bold
                    )
                )
            } else {
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = BangSosRed,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = negativeNotice,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = BangSosRed,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
