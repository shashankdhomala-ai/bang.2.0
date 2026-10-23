package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BangAmberWarning
import com.example.ui.theme.BangCyanAccent
import com.example.ui.theme.BangElectricBlue
import com.example.ui.theme.BangEmeraldMesh
import com.example.ui.theme.BangNeonPurple
import com.example.ui.theme.BangSosRed
import com.example.ui.theme.BangSurface
import com.example.ui.theme.BangSurfaceBorder
import com.example.ui.theme.BangSurfaceVariant
import com.example.ui.theme.BangTextMuted
import com.example.ui.theme.BangTextPrimary
import com.example.ui.theme.BangTextSecondary

/**
 * Types of legal & compliance documents required by Apple App Store and Google Play Console
 */
enum class LegalDocumentType(
    val title: String,
    val subtitle: String,
    val badge: String,
    val webUrl: String,
    val icon: ImageVector,
    val accentColor: Color
) {
    TERMS_OF_SERVICE(
        title = "Terms of Service & EULA",
        subtitle = "End User License Agreement for BANG Hybrid Mesh & P2P Messenger",
        badge = "LEGAL & EULA • OCT 2026",
        webUrl = "https://bang-mesh.network/legal/terms",
        icon = Icons.Default.Gavel,
        accentColor = BangElectricBlue
    ),
    PRIVACY_POLICY(
        title = "Privacy Policy",
        subtitle = "Zero-Knowledge Architecture, Local Caching & Mesh Relays",
        badge = "PRIVACY • ZERO-KNOWLEDGE",
        webUrl = "https://bang-mesh.network/legal/privacy",
        icon = Icons.Default.Security,
        accentColor = BangEmeraldMesh
    ),
    OPEN_SOURCE_LICENSES(
        title = "Open Source Licenses",
        subtitle = "Third-Party Cryptography & Android Framework Notices",
        badge = "COMPLIANCE • ATTRIBUTIONS",
        webUrl = "https://bang-mesh.network/legal/licenses",
        icon = Icons.Default.Description,
        accentColor = BangCyanAccent
    ),
    CUSTOMER_SUPPORT(
        title = "Customer Support & Security",
        subtitle = "Official Developer & App Store Compliance Support Portal",
        badge = "SUPPORT • 24/7 SLA",
        webUrl = "support@bang-messenger.io",
        icon = Icons.Default.SupportAgent,
        accentColor = BangEmeraldMesh
    )
}

/**
 * Full-screen modal viewer for Terms of Service, Privacy Policy, and Licenses
 */
@Composable
fun BangLegalModal(
    documentType: LegalDocumentType,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var copiedUrl by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, documentType.accentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("bang_legal_modal"),
            color = BangSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header Row with Icon, Badge, and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(documentType.accentColor.copy(alpha = 0.15f))
                                .border(1.dp, documentType.accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = documentType.icon,
                                contentDescription = null,
                                tint = documentType.accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(documentType.accentColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = documentType.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = documentType.accentColor
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = documentType.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 15.sp
                                ),
                                color = BangTextPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BangSurfaceVariant)
                            .testTag("legal_modal_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Legal Modal",
                            tint = BangTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hosted Web Page URL Capsule with Copy Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BangSurfaceVariant)
                        .border(1.dp, BangSurfaceBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = documentType.accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = documentType.webUrl,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = BangTextSecondary
                            ),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("BANG Legal URL", documentType.webUrl)
                            clipboard?.setPrimaryClip(clip)
                            copiedUrl = true
                        },
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("copy_legal_url_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (copiedUrl) BangEmeraldMesh else documentType.accentColor
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (copiedUrl) BangEmeraldMesh else documentType.accentColor.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = if (copiedUrl) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (copiedUrl) "Copied" else "Copy URL",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Document Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BangSurfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, BangSurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .testTag("legal_content_scroll_area"),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (documentType) {
                            LegalDocumentType.TERMS_OF_SERVICE -> {
                                TermsOfServiceContent()
                            }
                            LegalDocumentType.PRIVACY_POLICY -> {
                                PrivacyPolicyContent()
                            }
                            LegalDocumentType.OPEN_SOURCE_LICENSES -> {
                                OpenSourceLicensesContent()
                            }
                            LegalDocumentType.CUSTOMER_SUPPORT -> {
                                CustomerSupportContent()
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Footer Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("legal_accept_dismiss_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = documentType.accentColor
                    )
                ) {
                    Text(
                        text = "I Understand & Accept",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BangSurface
                        )
                    )
                }
            }
        }
    }
}

/**
 * Standard Professional T&C Text Template for BANG as requested
 */
@Composable
private fun TermsOfServiceContent() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Document Meta Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BangSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, BangElectricBlue.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "BANG Messenger — Terms of Service & End User License Agreement (EULA)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BangElectricBlue,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last Updated: October 2026 • Compliant with Apple & Google Play Store Policies",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = BangTextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        LegalSectionCard(
            sectionNumber = "1",
            title = "Acceptance of Terms",
            body = "By downloading, installing, or using BANG (\"the Application\"), you agree to be bound by these Terms of Service. If you do not agree, do not use the Application.",
            accentColor = BangElectricBlue
        )

        LegalSectionCard(
            sectionNumber = "2",
            title = "Hybrid Mesh & P2P Network Disclaimer",
            body = "BANG provides both internet-connected cloud messaging and infrastructure-free local peer-to-peer (P2P) routing via Bluetooth Low Energy (BLE) and Wi-Fi Direct. You acknowledge that:\n\n" +
                    "• Messages sent via local mesh networks rely on intermediate devices (\"Nodes\") owned by other users to relay packets.\n\n" +
                    "• While payloads are end-to-end encrypted (E2EE), temporary store-and-forward routing data may pass through third-party peer devices within physical proximity.",
            accentColor = BangAmberWarning,
            highlight = true
        )

        LegalSectionCard(
            sectionNumber = "3",
            title = "User Conduct & Emergency (SOS) Channels",
            body = "• The SOS broadcast channel is designed for critical assistance requests. Misuse of emergency QoS priority broadcasts may result in network node blacklisting.\n\n" +
                    "• You agree not to use BANG for any unlawful activities, harassment, or transmission of malicious code.",
            accentColor = BangSosRed
        )

        LegalSectionCard(
            sectionNumber = "4",
            title = "Limitation of Liability",
            body = "BANG is provided on an \"as-is\" and \"as-available\" basis. The developers disclaim all warranties regarding mesh network connectivity, range limitations, or physical interference in offline environments.",
            accentColor = BangElectricBlue
        )

        LegalSectionCard(
            sectionNumber = "5",
            title = "Account & Data Deletion",
            body = "You retain full control over your cryptographic identity keys and local cache. You may request complete account data purging at any time via Settings -> Legal & Compliance -> Account & Data Deletion.",
            accentColor = BangEmeraldMesh
        )
    }
}

/**
 * Privacy Policy Content tailored for Zero-Knowledge & Mesh networking
 */
@Composable
private fun PrivacyPolicyContent() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BangSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, BangEmeraldMesh.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "BANG Messenger — Zero-Knowledge Privacy Architecture",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BangEmeraldMesh,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last Updated: October 2026 • Decentralized & Zero-Tracking Guarantee",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = BangTextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        LegalSectionCard(
            sectionNumber = "1",
            title = "Cryptographic Key Derivation",
            body = "Your private identity keys (Ed25519 signing keys and Curve25519 DH keys) are generated strictly on your physical Android device within the Hardware Security Module (HSM) / StrongBox. BANG servers never have access to your private cryptographic material.",
            accentColor = BangEmeraldMesh
        )

        LegalSectionCard(
            sectionNumber = "2",
            title = "Zero Central Telemetry & Trackers",
            body = "BANG contains no third-party telemetry, ad trackers, or behavioural analytics. No IP logs or hardware device fingerprints are associated with your public key node address.",
            accentColor = BangCyanAccent
        )

        LegalSectionCard(
            sectionNumber = "3",
            title = "Ephemeral Mesh Relay & Store-and-Forward",
            body = "When relaying packets through stranger nodes, intermediate devices cannot decrypt message content or inspect contact lists. Temporary relay packets are held in a RAM circular buffer and automatically purged upon cryptographic Delivery ACK or TTL expiration.",
            accentColor = BangAmberWarning
        )

        LegalSectionCard(
            sectionNumber = "4",
            title = "Permissions Required by Android OS",
            body = "• BLUETOOTH_CONNECT & SCAN: Required for discovery of neighboring mesh nodes.\n" +
                    "• ACCESS_FINE_LOCATION: Mandated by Android system framework for low-level BLE radio scans (never recorded or transmitted).\n" +
                    "• POST_NOTIFICATIONS: Delivers alerts when new encrypted mesh packets arrive in background.\n" +
                    "• FOREGROUND_SERVICE_CONNECTED_DEVICE: Keeps the P2P mesh relay active while preserving battery efficiency.",
            accentColor = BangElectricBlue
        )

        LegalSectionCard(
            sectionNumber = "5",
            title = "Right to Complete Data Erasure",
            body = "In accordance with GDPR, CCPA, and App Store guidelines, you can trigger an irrevocable local zero-out of all cryptographic identity keys, cached messages, and peer records with a single click in Settings.",
            accentColor = BangSosRed
        )
    }
}

/**
 * Open Source Software Notices and Attributions
 */
@Composable
private fun OpenSourceLicensesContent() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BangSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, BangCyanAccent.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Open Source Software Attributions",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BangCyanAccent,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "BANG acknowledges and thanks the developers of open source cryptographic libraries and Android foundations.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = BangTextSecondary)
                )
            }
        }

        OpenSourceLibraryItem(
            name = "TweetNaCl / Libsodium (Ed25519 & X25519)",
            license = "ISC License / Public Domain",
            description = "High-security cryptographic library for digital signatures, authenticated encryption, and Elliptic-Curve Diffie-Hellman key exchanges."
        )

        OpenSourceLibraryItem(
            name = "Android Jetpack & Jetpack Compose",
            license = "Apache License 2.0",
            description = "Modern declarative UI toolkit, Architecture Components, and WindowInsets edge-to-edge support."
        )

        OpenSourceLibraryItem(
            name = "Kotlin Coroutines & Flow",
            license = "Apache License 2.0",
            description = "Lightweight asynchronous reactive pipelines for packet multiplexing and state observation."
        )

        OpenSourceLibraryItem(
            name = "Vico Charting Engine",
            license = "Apache License 2.0",
            description = "Declarative graphing and telemetry visualization for BLE SNR and packet propagation curves."
        )

        OpenSourceLibraryItem(
            name = "Google Material Symbols & JetBrains Fonts",
            license = "SIL Open Font License 1.1 & Apache 2.0",
            description = "High-legibility monospace and clean vector icons utilized for technical tactical UI."
        )
    }
}

/**
 * Customer Support & App Store Compliance Info
 * Fulfills Customer Support Contact requirement (support@bang-messenger.io)
 */
@Composable
private fun CustomerSupportContent() {
    val context = LocalContext.current
    var emailCopied by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BangSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, BangEmeraldMesh.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = BangEmeraldMesh,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "OFFICIAL CUSTOMER SUPPORT",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BangEmeraldMesh,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "support@bang-messenger.io",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = BangTextPrimary,
                        fontSize = 15.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Apple App Store & Google Play Console Verified Developer Support Channel",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.5.sp,
                        color = BangTextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("BANG Support Email", "support@bang-messenger.io")
                            clipboard?.setPrimaryClip(clip)
                            emailCopied = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (emailCopied) BangEmeraldMesh else BangCyanAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp).testTag("btn_copy_support_email")
                    ) {
                        Icon(
                            imageVector = if (emailCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (emailCopied) "Copied Email" else "Copy Email",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        LegalSectionCard(
            sectionNumber = "1",
            title = "Response Time & Critical SOS Inquiries",
            body = "Support tickets regarding Emergency SOS network broadcasts, node blacklisting, or cryptographic pairing are prioritized with a target SLA of under 2 hours. General inquiries responded to within 24 hours.",
            accentColor = BangEmeraldMesh
        )

        LegalSectionCard(
            sectionNumber = "2",
            title = "Security Enclave & Bug Bounty",
            body = "If you discover any vulnerability in our Ed25519 or Curve25519 implementation, please contact security@bang-messenger.io directly with encrypted PGP messages. We offer bounties for verified zero-trust bypass disclosures.",
            accentColor = BangCyanAccent
        )

        LegalSectionCard(
            sectionNumber = "3",
            title = "Data Deletion Requests (GDPR / CCPA)",
            body = "Because BANG operates on a decentralized, zero-knowledge architecture, no server-side user database exists. To erase all on-device identity keys and message records, navigate to Settings -> Account -> Delete Account.",
            accentColor = BangSosRed
        )
    }
}

@Composable
private fun LegalSectionCard(
    sectionNumber: String,
    title: String,
    body: String,
    accentColor: Color,
    highlight: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BangSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (highlight) accentColor.copy(alpha = 0.6f) else BangSurfaceBorder
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f))
                        .border(1.dp, accentColor.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sectionNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = accentColor
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BangTextPrimary,
                        fontSize = 13.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    color = BangTextSecondary,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Composable
private fun OpenSourceLibraryItem(
    name: String,
    license: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BangSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BangSurfaceBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BangTextPrimary,
                        fontSize = 12.5.sp
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BangCyanAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = license,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = BangCyanAccent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    color = BangTextSecondary
                )
            )
        }
    }
}

/**
 * Item row in Settings screen for Legal & Compliance
 */
@Composable
fun LegalSettingsListItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    accentColor: Color = BangCyanAccent,
    tag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BangSurfaceVariant)
            .border(1.dp, BangSurfaceBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BangTextPrimary
                    )
                )
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    color = BangTextSecondary
                )
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = BangTextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Account & Data Deletion Confirmation Dialog
 * Implements EULA Clause 5: "You retain full control over your cryptographic identity keys and local cache."
 */
@Composable
fun AccountDataDeletionDialog(
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BangSosRed.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .testTag("delete_account_dialog"),
            color = BangSurface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BangSosRed.copy(alpha = 0.2f))
                        .border(1.dp, BangSosRed.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = BangSosRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "PURGE CRYPTOGRAPHIC IDENTITY?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = BangSosRed,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "As stipulated in Section 5 of the BANG Terms of Service & EULA, this action immediately:\n\n" +
                            "• Destroys on-device Ed25519 & X25519 private keys\n" +
                            "• Purges all local encrypted chat threads & pulse stories\n" +
                            "• Drops peer routing cache and blind relay tokens\n\n" +
                            "This action is irreversible and ensures zero residual data on device.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = BangTextSecondary,
                        lineHeight = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cancel_purge_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BangTextSecondary)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onConfirmDelete()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("confirm_purge_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BangSosRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Purge All Data", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
