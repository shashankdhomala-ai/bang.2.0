package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.CompressedMeshPacket
import com.example.ai.CrisisClassification
import com.example.ai.PiiAuditVerdict
import com.example.ai.ThreadSummaryResult
import com.example.model.ChatMessage
import com.example.model.ChatThread
import com.example.model.DeliveryStatus
import com.example.model.TransportType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

// -----------------------------------------------------------------------------
// Obsidian Cyber Palette Tokens
// -----------------------------------------------------------------------------
private val ObsidianSlate = Color(0xFF070B11)
private val DeepCharcoalSent = Color(0xFF121C2A) // Deep Charcoal (#121C2A)
private val DarkSlateReceived = Color(0xFF0D1522) // Dark Slate (#0D1522)
private val NeonLimeAccent = Color(0xFF99FF00)   // Electric Kinetic Lime (#99FF00)
private val ElectricCyan = Color(0xFF00E5FF)
private val UltraViolet = Color(0xFF9D4EDD)
private val KineticEmerald = Color(0xFF00E676)
private val AmberGold = Color(0xFFFFB800)
private val NeonHotPink = Color(0xFFFF3366)
private val GlassSurfaceBg = Color(0xDC0C1420)
private val TextPrimary = Color(0xFFE2E8F0)
private val TextSecondary = Color(0xFF94A3B8)
private val TextMuted = Color(0xFF64748B)

/**
 * Obsidian Cyber Active Chat Screen for BANG Messenger
 *
 * Implements:
 * 1. Glassmorphic chat containers with 12dp rounded corners & glowing accent borders
 * 2. Sent messages: Deep Charcoal with Electric Cyan (#00E5FF) right border
 * 3. Received messages: Dark Slate with Ultra Violet (#9D4EDD) left border
 * 4. Ambient Mesh Particle Vector Canvas with faint pulsing packet wave
 * 5. Header Mesh Topology Pill ("⚡ BLE Mesh • 2 Hops" or "🌐 Cloud Mode")
 * 6. Contact Avatar with glowing Cyan ring ("Using App") + tap to refresh NFC/QR zero-trust key
 * 7. Delivery Packet Badges: ⚪ Single Circle (queued), 🔲 Double Square (relayed), 🩵 Cyan Ring (delivered)
 * 8. Inline /ai assistant queries & answers within thread
 * 9. One-Tap Contextual Smart Chips floating above input bar
 * 10. Haptic Wave Swipe right on messages to quote reply
 * 11. Ephemeral Pulse Reactions on long press
 * 12. Glassmorphic input capsule with glowing Cyan send button
 */
@Composable
fun ChatDetailScreen(
    thread: ChatThread,
    messages: List<ChatMessage>,
    onSendMessage: (String, String?) -> Unit,
    onBack: () -> Unit,
    threadSummary: ThreadSummaryResult? = null,
    isSummarizing: Boolean = false,
    onTriggerSummary: () -> Unit = {},
    onDismissSummary: () -> Unit = {},
    compressedDraft: CompressedMeshPacket? = null,
    onCompressDraft: (String) -> Unit = {},
    crisisQos: CrisisClassification? = null,
    piiAudit: PiiAuditVerdict? = null,
    onDraftChanged: (String) -> Unit = {},
    onRedactDraft: (String) -> Unit = {},
    onOpenScanModal: () -> Unit = {},
    onAddReaction: (String, String) -> Unit = { _, _ -> },
    isOffline: Boolean = true,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var inspectedHopMsg by remember { mutableStateOf<ChatMessage?>(null) }
    var reactionTargetMsg by remember { mutableStateOf<ChatMessage?>(null) }
    var showChatMenu by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }

    // Auto-scroll on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianSlate)
            .statusBarsPadding()
            .imePadding()
            .testTag("chat_detail_screen")
    ) {
        // ---------------------------------------------------------------------
        // 1. Ambient Mesh Vector Canvas Background (Animated Particles & Grid)
        // ---------------------------------------------------------------------
        AmbientMeshParticleBackground(isOffline = isOffline)

        Column(modifier = Modifier.fillMaxSize()) {

            // -----------------------------------------------------------------
            // 2. Cyber Header Bar (Avatar with Glowing Halo + Mesh Topology Pill)
            // -----------------------------------------------------------------
            CyberChatHeader(
                thread = thread,
                isOffline = isOffline,
                onBack = onBack,
                onAvatarClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onOpenScanModal()
                },
                onTriggerSummary = onTriggerSummary,
                onMenuClick = { showChatMenu = !showChatMenu },
                showChatMenu = showChatMenu,
                onDismissMenu = { showChatMenu = false },
                onSimulateVoice = {
                    showChatMenu = false
                    onSendMessage("🎤 [Encrypted Voice Memo: 6kbps Opus • 0:04]", null)
                },
                onSimulateOutage = {
                    showChatMenu = false
                    onSendMessage("/ai route", null)
                }
            )

            // -----------------------------------------------------------------
            // 3. Messages LazyColumn Feed
            // -----------------------------------------------------------------
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Encryption Zero-Trust Security Pill
                    item {
                        CyberEncryptionPill()
                    }

                    // On-Device SLM Summary Banner
                    if (isSummarizing) {
                        item {
                            AiSummarizingCard()
                        }
                    } else if (threadSummary != null) {
                        item {
                            AiSummaryResultCard(
                                summary = threadSummary,
                                onDismiss = onDismissSummary
                            )
                        }
                    }

                    // Chat Messages List
                    items(messages, key = { it.id }) { msg ->
                        CyberGlassmorphicMessageBubble(
                            message = msg,
                            onInspect = { inspectedHopMsg = msg },
                            onSwipeToReply = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                replyingToMessage = msg
                            },
                            onLongPress = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                reactionTargetMsg = msg
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                // Ephemeral Pulse Reaction Picker Overlay (when long-pressed)
                if (reactionTargetMsg != null) {
                    EphemeralReactionPicker(
                        onSelectEmoji = { emoji ->
                            reactionTargetMsg?.let { onAddReaction(it.id, emoji) }
                            reactionTargetMsg = null
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        onDismiss = { reactionTargetMsg = null }
                    )
                }
            }

            // -----------------------------------------------------------------
            // 4. Attachment Drawer (when paperclip is tapped)
            // -----------------------------------------------------------------
            AnimatedVisibility(visible = showAttachmentSheet, enter = fadeIn(), exit = fadeOut()) {
                AttachmentOptionsGrid(
                    onSelectCamera = {
                        showAttachmentSheet = false
                        onSendMessage("📷 [Encrypted Mesh Photo: IMG_${Random.nextInt(1000, 9999)}.webp • 420 KB]", replyingToMessage?.content)
                        replyingToMessage = null
                    },
                    onSelectDoc = {
                        showAttachmentSheet = false
                        onSendMessage("📄 [Signed Document: mesh_manifest_${Random.nextInt(10, 99)}.pdf • 240 KB]", replyingToMessage?.content)
                        replyingToMessage = null
                    },
                    onSelectAudio = {
                        showAttachmentSheet = false
                        onSendMessage("🎵 [Voice Note: 6kbps Opus Encrypted • 3.8s]", replyingToMessage?.content)
                        replyingToMessage = null
                    },
                    onSelectKey = {
                        showAttachmentSheet = false
                        onSendMessage("🔑 [Out-of-Band Noise XK Key Handshake Emitted]", replyingToMessage?.content)
                        replyingToMessage = null
                    }
                )
            }

            // -----------------------------------------------------------------
            // 5. On-Device AI Audits (Crisis QoS & PII Protection Alerts)
            // -----------------------------------------------------------------
            if (crisisQos != null && crisisQos.isEmergency) {
                CrisisQosWarningBar(crisisQos = crisisQos)
            }

            if (piiAudit != null && piiAudit.containsSensitivePii) {
                PiiProtectionAlertBar(
                    piiAudit = piiAudit,
                    onRedact = {
                        inputText = piiAudit.redactedDraftPreview
                        onDraftChanged(inputText)
                        onRedactDraft(inputText)
                    }
                )
            }

            if (compressedDraft != null) {
                AiCompressionPreviewBar(compressedDraft = compressedDraft)
            }

            // -----------------------------------------------------------------
            // 6. Active "Replying To" Banner
            // -----------------------------------------------------------------
            AnimatedVisibility(
                visible = replyingToMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                replyingToMessage?.let { target ->
                    ReplyingToBanner(
                        replyingMsg = target,
                        onDismiss = { replyingToMessage = null }
                    )
                }
            }

            // -----------------------------------------------------------------
            // 7. Contextual Smart Chips (Floating right above input box)
            // -----------------------------------------------------------------
            ContextualSmartChipsRow(
                onChipClicked = { chipText ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    when (chipText) {
                        "⚡ Query Local Route" -> {
                            onSendMessage("/ai route", replyingToMessage?.content)
                            replyingToMessage = null
                        }
                        "📝 Summarize Thread" -> {
                            onTriggerSummary()
                        }
                        "🆘 Send SOS Pin" -> {
                            onSendMessage("🆘 [EMERGENCY PIN: Lat 37.7749° N, Lon 122.4194° W • Priority Tier 0 Beacon]", replyingToMessage?.content)
                            replyingToMessage = null
                        }
                        "🌐 Translate to Mesh Format" -> {
                            if (inputText.isNotBlank()) {
                                onSendMessage("/ai translate $inputText", replyingToMessage?.content)
                                inputText = ""
                                onDraftChanged("")
                                replyingToMessage = null
                            } else {
                                onSendMessage("/ai translate", replyingToMessage?.content)
                                replyingToMessage = null
                            }
                        }
                        "🤖 Ask BANG AI" -> {
                            inputText = "/ai "
                            onDraftChanged(inputText)
                        }
                    }
                }
            )

            // -----------------------------------------------------------------
            // 8. Cyber Glassmorphic Input Bar
            // -----------------------------------------------------------------
            CyberInputBar(
                inputText = inputText,
                onInputChanged = {
                    inputText = it
                    onDraftChanged(it)
                },
                onSend = {
                    if (inputText.isNotBlank()) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        val finalMsg = if (compressedDraft != null) {
                            "${compressedDraft.compressedPayload} [AI Compressed • -${String.format(Locale.US, "%.0f", compressedDraft.compressionRatioPct)}% BLE MTU]"
                        } else {
                            inputText.trim()
                        }
                        onSendMessage(finalMsg, replyingToMessage?.content)
                        inputText = ""
                        onDraftChanged("")
                        replyingToMessage = null
                    } else {
                        // Voice note memo simulation
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSendMessage("🎤 [Voice Memo: 6kbps Opus Encrypted • 4.2s]", replyingToMessage?.content)
                        replyingToMessage = null
                    }
                },
                onToggleAttachment = {
                    showAttachmentSheet = !showAttachmentSheet
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onCompress = {
                    if (inputText.isNotBlank()) {
                        onCompressDraft(inputText)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                }
            )

            // -----------------------------------------------------------------
            // 9. Interactive Honest Delivery Status Tracker (Phone 4)
            // -----------------------------------------------------------------
            val latestSentMsg = messages.lastOrNull { it.isFromMe }
            if (latestSentMsg != null) {
                CyberDeliveryStatusTrackerCard(status = latestSentMsg.deliveryStatus)
            }
        }
    }

    // Zero-Trust Packet Receipt Modal (when tapping a bubble)
    inspectedHopMsg?.let { msg ->
        ZeroTrustPacketModal(
            msg = msg,
            onDismiss = { inspectedHopMsg = null }
        )
    }
}

// -----------------------------------------------------------------------------
// Component: Ambient Mesh Particle Canvas Background
// -----------------------------------------------------------------------------
@Composable
private fun AmbientMeshParticleBackground(isOffline: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambientMesh")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val driftPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "driftPhase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Subtle radial bloom in center-top
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ElectricCyan.copy(alpha = 0.08f * pulseGlow),
                    UltraViolet.copy(alpha = 0.04f * pulseGlow),
                    Color.Transparent
                ),
                center = Offset(w * 0.5f, h * 0.35f),
                radius = w * 0.8f
            ),
            radius = w * 0.8f,
            center = Offset(w * 0.5f, h * 0.35f)
        )

        // Procedural particle nodes (16 mesh vertices)
        val nodeCount = 16
        val nodes = (0 until nodeCount).map { i ->
            val angle = (i.toFloat() / nodeCount) * 6.283f + driftPhase * 0.2f
            val baseRadius = (w * 0.42f) * (0.35f + (i % 4) * 0.2f)
            val cx = w * 0.5f + kotlin.math.cos(angle.toDouble()).toFloat() * baseRadius
            val cy = h * (0.15f + (i * 0.05f)) + kotlin.math.sin((angle + i).toDouble()).toFloat() * 18.dp.toPx()
            Offset(cx.coerceIn(20f, w - 20f), cy.coerceIn(20f, h - 20f))
        }

        // Draw mesh edges between adjacent nodes
        for (i in nodes.indices) {
            for (j in i + 1 until nodes.size) {
                val dist = (nodes[i] - nodes[j]).getDistance()
                if (dist < 180.dp.toPx()) {
                    val alpha = ((1f - dist / 180.dp.toPx()) * 0.14f * pulseGlow).coerceIn(0f, 0.22f)
                    val lineColor = if (i % 2 == 0) ElectricCyan else UltraViolet
                    drawLine(
                        color = lineColor.copy(alpha = alpha),
                        start = nodes[i],
                        end = nodes[j],
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        }

        // Draw particle points
        nodes.forEachIndexed { idx, pt ->
            val ptColor = if (idx % 3 == 0) ElectricCyan else if (idx % 3 == 1) UltraViolet else KineticEmerald
            drawCircle(
                color = ptColor.copy(alpha = 0.35f * pulseGlow),
                radius = 2.dp.toPx(),
                center = pt
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Cyber Header Bar (Avatar + Mesh Topology Pill)
// -----------------------------------------------------------------------------
@Composable
private fun CyberChatHeader(
    thread: ChatThread,
    isOffline: Boolean,
    onBack: () -> Unit,
    onAvatarClick: () -> Unit,
    onTriggerSummary: () -> Unit,
    onMenuClick: () -> Unit,
    showChatMenu: Boolean,
    onDismissMenu: () -> Unit,
    onSimulateVoice: () -> Unit,
    onSimulateOutage: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatarRing")
    val ringGlow by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringGlow"
    )

    Surface(
        color = GlassSurfaceBg,
        border = BorderStroke(1.dp, Color(0xFF162030)),
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Contact Avatar with Glowing Electric Cyan Ring ("Using App")
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onAvatarClick() }
                    .padding(2.dp)
                    .border(
                        BorderStroke(
                            1.8.dp,
                            Brush.sweepGradient(
                                listOf(
                                    ElectricCyan.copy(alpha = ringGlow),
                                    Color(0xFF007799),
                                    ElectricCyan.copy(alpha = ringGlow)
                                )
                            )
                        ),
                        CircleShape
                    )
                    .background(Color(0xFF0C1929), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Contact Avatar",
                    tint = ElectricCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Contact Title & Header Mesh Topology Pill
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = thread.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Header Mesh Topology Pill: "⚡ BLE Mesh • 2 Hops" or "🌐 Cloud Mode"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isOffline) Color(0xFF1C1304) else Color(0xFF041E15)
                        )
                        .border(
                            0.7.dp,
                            if (isOffline) AmberGold.copy(alpha = 0.5f) else KineticEmerald.copy(alpha = 0.5f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    val topologyText = if (isOffline) {
                        "⚡ BLE Mesh • ${thread.activeHops.coerceAtLeast(2)} Hops"
                    } else {
                        "🌐 Cloud Mode (Online)"
                    }

                    Text(
                        text = topologyText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            color = if (isOffline) AmberGold else KineticEmerald
                        ),
                        maxLines = 1
                    )
                }
            }

            // AI Summarizer Pill Action
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0B192A))
                    .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable { onTriggerSummary() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Summarizer",
                        tint = ElectricCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI Summary",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = ElectricCyan
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Overflow Menu
            Box {
                IconButton(onClick = onMenuClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showChatMenu,
                    onDismissRequest = onDismissMenu,
                    modifier = Modifier.background(Color(0xFF0D1524))
                ) {
                    DropdownMenuItem(
                        text = { Text("⚡ Query Routing Topology", color = TextPrimary, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Hub, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp)) },
                        onClick = onSimulateOutage
                    )
                    DropdownMenuItem(
                        text = { Text("🎤 Simulate Voice Note", color = TextPrimary, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Mic, contentDescription = null, tint = UltraViolet, modifier = Modifier.size(16.dp)) },
                        onClick = onSimulateVoice
                    )
                    DropdownMenuItem(
                        text = { Text("🛡️ Refresh Zero-Trust Key", color = TextPrimary, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = KineticEmerald, modifier = Modifier.size(16.dp)) },
                        onClick = {
                            onDismissMenu()
                            onAvatarClick()
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Cyber Encryption Notice Pill
// -----------------------------------------------------------------------------
@Composable
private fun CyberEncryptionPill() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0x900D1524),
            border = BorderStroke(0.7.dp, ElectricCyan.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "E2EE Lock",
                    tint = ElectricCyan,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Double Ratchet E2EE • Sealed with Curve25519 & ChaCha20-Poly1305",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.5.sp,
                        color = ElectricCyan.copy(alpha = 0.9f)
                    )
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Glassmorphic Message Bubble with Dynamic Accent Highlights
// -----------------------------------------------------------------------------
@Composable
private fun CyberGlassmorphicMessageBubble(
    message: ChatMessage,
    onInspect: () -> Unit,
    onSwipeToReply: () -> Unit,
    onLongPress: () -> Unit
) {
    val isMe = message.isFromMe
    val isAiAssistant = message.senderId == "bang-ai-inline" || message.senderName.contains("BANG AI")
    val formattedTime = formatBubbleTime(message.timestamp)

    // Swipe-to-reply horizontal offset
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var hasTriggeredSwipeReply by remember { mutableStateOf(false) }

    // Sent: Deep Charcoal with Electric Cyan right border
    // Received: Dark Slate with Ultra Violet left border
    // AI message: Futuristic glowing cyan card
    val containerColor = when {
        isAiAssistant -> Color(0xF00B1728)
        isMe -> DeepCharcoalSent
        else -> DarkSlateReceived
    }

    val bubbleAlignment = when {
        isAiAssistant -> Alignment.Center
        isMe -> Alignment.CenterEnd
        else -> Alignment.CenterStart
    }

    val borderStroke = when {
        isAiAssistant -> BorderStroke(1.2.dp, ElectricCyan.copy(alpha = 0.7f))
        isMe -> BorderStroke(1.4.dp, NeonLimeAccent) // Glowing Neon Lime right border (#99FF00)
        else -> BorderStroke(1.4.dp, UltraViolet) // Glowing Ultra Violet left border (#9D4EDD)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = bubbleAlignment
    ) {
        // Swipe gesture container
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(message.id) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX.value > 65f && !hasTriggeredSwipeReply) {
                                onSwipeToReply()
                            }
                            hasTriggeredSwipeReply = false
                            scope.launch { offsetX.animateTo(0f, tween(200, easing = FastOutSlowInEasing)) }
                        },
                        onDragCancel = {
                            scope.launch { offsetX.animateTo(0f, tween(150)) }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            if (dragAmount > 0) {
                                val newOffset = (offsetX.value + dragAmount * 0.6f).coerceIn(0f, 90f)
                                scope.launch { offsetX.snapTo(newOffset) }
                                if (newOffset > 60f && !hasTriggeredSwipeReply) {
                                    hasTriggeredSwipeReply = true
                                    onSwipeToReply()
                                }
                            }
                        }
                    )
                }
                .widthIn(min = 90.dp, max = if (isAiAssistant) 350.dp else 300.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(containerColor)
                .border(borderStroke, RoundedCornerShape(12.dp))
                .clickable { onInspect() }
                .padding(horizontal = 12.dp, vertical = 9.dp)
        ) {
            Column {

                // AI Assistant Header Capsule Badge
                if (isAiAssistant) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "BANG AI ASSISTANT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = ElectricCyan
                                )
                            )
                        }

                        Text(
                            text = "⚡ Local SLM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = KineticEmerald
                            )
                        )
                    }
                }

                // Reply Quote Preview (if replying to another message)
                if (!message.replyToText.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x55000000))
                            .border(BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.35f)), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(24.dp)
                                    .background(ElectricCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message.replyToText.take(45) + if (message.replyToText.length > 45) "…" else "",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Message Text Content
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.8.sp,
                        color = TextPrimary,
                        lineHeight = 19.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom Row: Relayed tag, Timestamp, Delivery Packet Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mesh routing tag
                    if (message.hopRoute.any { !it.isFriend }) {
                        Text(
                            text = "⚡ Relayed (${message.hopRoute.size} hops)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.5.sp,
                                color = AmberGold
                            )
                        )
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                color = TextMuted
                            )
                        )

                        if (isMe) {
                            Spacer(modifier = Modifier.width(5.dp))
                            // Mesh Network Delivery Packet Badges:
                            // ⚪ Single Outline Circle: Queued in local buffer
                            // 🔲 Double Outline Square: Relayed across intermediate nodes
                            // 🩵 Electric Cyan Ring: Delivered & zero-knowledge decrypted
                            DeliveryPacketBadge(status = message.deliveryStatus)
                        }
                    }
                }

                // Ephemeral Pulse Reactions Display (if user long-pressed and dropped reactions)
                if (message.pulseReactions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        message.pulseReactions.distinct().forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF132233))
                                    .border(0.7.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = emoji, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Delivery Packet Badges
// -----------------------------------------------------------------------------
@Composable
private fun DeliveryPacketBadge(status: DeliveryStatus) {
    when (status) {
        // ⚪ Single Outline Circle: Encrypted payload queued in local buffer
        DeliveryStatus.QUEUED_OFFLINE -> {
            Canvas(modifier = Modifier.size(11.dp)) {
                drawCircle(
                    color = TextMuted,
                    radius = size.minDimension / 2.3f,
                    style = Stroke(width = 1.3.dp.toPx())
                )
            }
        }

        // 🔲 Double Outline Square: Relayed across intermediate mesh nodes
        DeliveryStatus.STORE_AND_FORWARD, DeliveryStatus.RELAYED_HOP -> {
            Canvas(modifier = Modifier.size(11.dp)) {
                drawRect(
                    color = AmberGold,
                    style = Stroke(width = 1.2.dp.toPx())
                )
                val pad = 2.dp.toPx()
                drawRect(
                    color = AmberGold,
                    topLeft = Offset(pad, pad),
                    size = androidx.compose.ui.geometry.Size(size.width - pad * 2, size.height - pad * 2),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // 🩵 Electric Cyan Ring: Delivered & zero-knowledge decrypted
        DeliveryStatus.DELIVERED, DeliveryStatus.READ -> {
            Canvas(modifier = Modifier.size(11.dp)) {
                drawCircle(
                    color = ElectricCyan,
                    radius = size.minDimension / 2.3f,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = ElectricCyan,
                    radius = size.minDimension / 5f
                )
            }
        }

        else -> {
            Canvas(modifier = Modifier.size(11.dp)) {
                drawCircle(
                    color = TextSecondary,
                    radius = size.minDimension / 2.3f,
                    style = Stroke(width = 1.3.dp.toPx())
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Honest Delivery Status Tracker (Phone 4)
// -----------------------------------------------------------------------------
@Composable
private fun CyberDeliveryStatusTrackerCard(status: DeliveryStatus) {
    Surface(
        color = Color(0xFF0F1722),
        border = BorderStroke(1.dp, Color(0xFF1E2A3A)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("delivery_status_tracker_card")
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(NeonLimeAccent)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Message status",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.5.sp
                    )
                )
            }

            // Progress states: Queued ────── Relaying ────── Delivered
            val isQueued = true
            val isRelaying = status == DeliveryStatus.STORE_AND_FORWARD ||
                    status == DeliveryStatus.RELAYED_HOP ||
                    status == DeliveryStatus.DELIVERED
            val isDelivered = status == DeliveryStatus.DELIVERED

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Node 1: Queued
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isQueued) Color(0xFF00E5FF) else Color(0xFF334155))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Queued",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (status == DeliveryStatus.QUEUED_OFFLINE) FontWeight.Bold else FontWeight.Medium,
                            color = if (status == DeliveryStatus.QUEUED_OFFLINE) Color(0xFF00E5FF) else Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                }

                // Line 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 8.dp)
                        .background(if (isRelaying) AmberGold else Color(0xFF1E293B))
                )

                // Node 2: Relaying
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isRelaying) AmberGold else Color(0xFF334155))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Relaying",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isRelaying && !isDelivered) FontWeight.Bold else FontWeight.Medium,
                            color = if (isRelaying && !isDelivered) AmberGold else Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                }

                // Line 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 8.dp)
                        .background(if (isDelivered) NeonLimeAccent else Color(0xFF1E293B))
                )

                // Node 3: Delivered
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isDelivered) NeonLimeAccent else Color(0xFF334155))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Delivered",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isDelivered) FontWeight.Bold else FontWeight.Medium,
                            color = if (isDelivered) NeonLimeAccent else Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Contextual Smart Chips (Floating right above input box)
// -----------------------------------------------------------------------------
@Composable
private fun ContextualSmartChipsRow(
    onChipClicked: (String) -> Unit
) {
    val chips = listOf(
        "⚡ Query Local Route",
        "📝 Summarize Thread",
        "🆘 Send SOS Pin",
        "🌐 Translate to Mesh Format",
        "🤖 Ask BANG AI"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        chips.forEach { chipText ->
            Surface(
                color = Color(0xDD0D1624),
                border = BorderStroke(0.8.dp, ElectricCyan.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onChipClicked(chipText) }
            ) {
                Text(
                    text = chipText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Cyber Glassmorphic Input Bar
// -----------------------------------------------------------------------------
@Composable
private fun CyberInputBar(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
    onToggleAttachment: () -> Unit,
    onCompress: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sendGlow")
    val sendPulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sendPulseGlow"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Glassmorphic Input Capsule
        Surface(
            color = GlassSurfaceBg,
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2D42)),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Paperclip attachment icon
                IconButton(onClick = onToggleAttachment, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Text field with placeholder: "Type an encrypted packet or /ai..."
                BasicTextField(
                    value = inputText,
                    onValueChange = onInputChanged,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontSize = 13.5.sp
                    ),
                    cursorBrush = SolidColor(ElectricCyan),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                        .testTag("chat_message_input"),
                    decorationBox = { innerTextField ->
                        if (inputText.isEmpty()) {
                            Text(
                                text = "Type an encrypted packet or /ai...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.8.sp,
                                    color = TextMuted
                                )
                            )
                        }
                        innerTextField()
                    }
                )

                // AI Compress button when drafting large packet
                if (inputText.length > 30) {
                    IconButton(onClick = onCompress, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "AI Compress",
                            tint = KineticEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Microphone icon
                IconButton(
                    onClick = {
                        // Voice Memo trigger
                        onSend()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Microphone",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Glowing Cyan Send Button
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ElectricCyan)
                .clickable { onSend() }
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.radialGradient(
                            listOf(
                                ElectricCyan.copy(alpha = sendPulseGlow),
                                Color.Transparent
                            )
                        )
                    ),
                    CircleShape
                )
                .testTag("chat_send_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = ObsidianSlate,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Replying To Banner
// -----------------------------------------------------------------------------
@Composable
private fun ReplyingToBanner(
    replyingMsg: ChatMessage,
    onDismiss: () -> Unit
) {
    Surface(
        color = Color(0xEE0E1826),
        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Replying to ${replyingMsg.senderName}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = ElectricCyan
                        )
                    )
                    Text(
                        text = replyingMsg.content.take(60),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = TextSecondary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = TextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Ephemeral Pulse Reactions Picker
// -----------------------------------------------------------------------------
@Composable
private fun EphemeralReactionPicker(
    onSelectEmoji: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val reactions = listOf("⚡", "🔥", "🛡️", "💫", "👁️", "❤️")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x77000000))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFF0F1B2A),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.2.dp, ElectricCyan.copy(alpha = 0.6f)),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                reactions.forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16253A))
                            .clickable { onSelectEmoji(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: AI Summary Result Card
// -----------------------------------------------------------------------------
@Composable
private fun AiSummaryResultCard(
    summary: ThreadSummaryResult,
    onDismiss: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xEE0B1522)),
        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = KineticEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🤖 ON-DEVICE SLM SUMMARY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = KineticEmerald
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${summary.inferenceLatencyMs}ms",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onDismiss() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (summary.bulletPoints.isNotEmpty()) {
                    summary.bulletPoints.joinToString("\n• ", prefix = "• ")
                } else {
                    "Thread synchronized over local mesh. No critical distress flags."
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            )

            if (summary.smartReplySuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    summary.smartReplySuggestions.forEach { suggestion ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF132338))
                                .border(0.6.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    color = ElectricCyan
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiSummarizingCard() {
    Surface(
        color = Color(0xCC091320),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Executing local quantized SLM inference (Gemma 2B)...",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = ElectricCyan
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Drawer and Alerts
// -----------------------------------------------------------------------------
@Composable
private fun AttachmentOptionsGrid(
    onSelectCamera: () -> Unit,
    onSelectDoc: () -> Unit,
    onSelectAudio: () -> Unit,
    onSelectKey: () -> Unit
) {
    Surface(
        color = GlassSurfaceBg,
        border = BorderStroke(1.dp, Color(0xFF1E2D42)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            AttachmentPillItem(icon = Icons.Default.CameraAlt, label = "Camera", color = Color(0xFFE91E63), onClick = onSelectCamera)
            AttachmentPillItem(icon = Icons.Default.AttachFile, label = "Document", color = UltraViolet, onClick = onSelectDoc)
            AttachmentPillItem(icon = Icons.Default.Mic, label = "Audio", color = AmberGold, onClick = onSelectAudio)
            AttachmentPillItem(icon = Icons.Default.Hub, label = "Mesh Key", color = ElectricCyan, onClick = onSelectKey)
        }
    }
}

@Composable
private fun AttachmentPillItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(1.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = TextPrimary))
    }
}

@Composable
private fun CrisisQosWarningBar(crisisQos: CrisisClassification) {
    Surface(
        color = Color(0xFF260611),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, NeonHotPink),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = NeonHotPink, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "🚨 Elevated Queue Priority (Tier ${crisisQos.qosLevel.priorityTier}): Emergency SOS Routing Active",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp,
                    color = NeonHotPink
                )
            )
        }
    }
}

@Composable
private fun PiiProtectionAlertBar(
    piiAudit: PiiAuditVerdict,
    onRedact: () -> Unit
) {
    Surface(
        color = Color(0xFF28070E),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, NeonHotPink),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "⚠️ SENSITIVE PII DETECTED (${piiAudit.detectedPiiTypes.joinToString()})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = NeonHotPink
                    )
                )
                Text(
                    text = "Broadcast halted to prevent credentials leaking over open BLE waves.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.5.sp, color = Color(0xFFFFCDD2))
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonHotPink)
                    .clickable { onRedact() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "REDACT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 9.sp))
            }
        }
    }
}

@Composable
private fun AiCompressionPreviewBar(compressedDraft: CompressedMeshPacket) {
    Surface(
        color = Color(0xFF04180A),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, KineticEmerald),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = KineticEmerald, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "⚡ Compressed to ${compressedDraft.compressedByteSize} bytes (-${String.format(Locale.US, "%.0f", compressedDraft.compressionRatioPct)}% BLE MTU reduction)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    color = KineticEmerald
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Zero-Trust Packet Modal
// -----------------------------------------------------------------------------
@Composable
private fun ZeroTrustPacketModal(
    msg: ChatMessage,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Zero-Trust Packet Receipt",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Packet ID: ${msg.id.take(16)}…",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextSecondary)
                )
                Text(
                    text = "Cipher: ChaCha20-Poly1305 (Double Ratchet Session)",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = ElectricCyan)
                )
                Text(
                    text = "PoW Nonce: #${msg.powHeader?.nonce ?: 4291} • ${msg.powHeader?.hashOutput ?: "000a4f9e"}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextSecondary)
                )
                Text(
                    text = "Hop Route: ${if (msg.hopRoute.isNotEmpty()) msg.hopRoute.joinToString(" ➔ ") { it.alias } else "Direct Peer Radio"}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = AmberGold)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ElectricCyan)
            }
        },
        containerColor = Color(0xFF0F1B2A),
        shape = RoundedCornerShape(16.dp)
    )
}

private fun formatBubbleTime(millis: Long): String {
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
}
