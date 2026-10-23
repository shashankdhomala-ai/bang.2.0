package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ai.AiAssistantMessage
import com.example.ai.AiEngineType
import com.example.ai.BangAiAssistantEngine
import com.example.ai.EngineRoutingPreference
import com.example.ai.GeminiModelTier
import kotlinx.coroutines.launch

// Color tokens strictly matching design spec
private val ObsidianSlate = Color(0xFF080C14)
private val DeepCharcoalUserMsg = Color(0xFF17202F)
private val GlassmorphicAiCard = Color(0xDC0E1724) // Translucent glassmorphism
private val ElectricCyan = Color(0xFF00E5FF)
private val KineticEmerald = Color(0xFF00E676)
private val CyberBorderCyan = Color(0xFF00E5FF).copy(alpha = 0.55f)
private val AmberGold = Color(0xFFFFB300)
private val UltraViolet = Color(0xFF9D4EDD)
private val TextPrimary = Color(0xFFE2E8F0)
private val TextSecondary = Color(0xFF94A3B8)
private val TextMuted = Color(0xFF64748B)

/**
 * High-Fidelity Dark Mode "AI Support Assistant" Screen for BANG
 */
@Composable
fun BangAiSupportScreen(
    engine: BangAiAssistantEngine,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by engine.state.collectAsState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    var inputText by remember { mutableStateOf("") }
    var isMicrophoneActive by remember { mutableStateOf(false) }
    var copiedMessageId by remember { mutableStateOf<String?>(null) }

    // Set context on engine for connectivity manager
    LaunchedEffect(Unit) {
        engine.setContext(context)
    }

    // Auto-scroll to latest message
    LaunchedEffect(state.messages.size, state.isGenerating) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianSlate)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("bang_ai_support_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. Header Bar: "🤖 BANG AI Assistant" + Engine Capsule Badge
            AiAssistantHeader(
                activeEngine = state.activeEngine,
                routingPref = state.routingPreference,
                isInternetAvailable = state.isInternetSimulatedAvailable,
                onBack = onBack,
                onOpenArchitecture = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    engine.setArchitectureModalVisible(true)
                },
                onClearChat = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    engine.clearChat()
                }
            )

            // 2. Gemini Model Tier Selector (gemini-3.8-flash, gemini-3.1-flash-lite, gemini-4-argon, on-device SLM)
            GeminiModelTierTabsRow(
                selectedTier = state.selectedModelTier,
                onSelectTier = { tier ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    engine.setModelTier(tier)
                }
            )

            // 3. System Role Instruction Badge ("as BANG AI")
            BangAiSystemRoleBanner(
                roleInstruction = state.systemRoleInstruction,
                selectedTier = state.selectedModelTier
            )

            // 4. Quick Chips (Horizontal Scroll)
            QuickChipsRow(
                chips = state.quickChips,
                onChipClicked = { chipText ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    scope.launch {
                        engine.askQuestion(chipText)
                    }
                }
            )

            // 3. Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(state.messages, key = { it.id }) { msg ->
                    if (msg.isUser) {
                        UserMessageCard(message = msg)
                    } else {
                        AiMessageCard(
                            message = msg,
                            isGenerating = msg.isGenerating && state.isGenerating,
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("BANG AI Response", msg.text))
                                copiedMessageId = msg.id
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            isCopied = copiedMessageId == msg.id
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // 4. Simulated Microphone banner if active
            AnimatedVisibility(
                visible = isMicrophoneActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = Color(0xFF102A38),
                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Listening... Speak prompt or tap sample prompt",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ElectricCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Text(
                            text = "Tap to Insert",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElectricCyan.copy(alpha = 0.2f))
                                .clickable {
                                    inputText = "How do I trigger an emergency SOS alert during a cellular blackout?"
                                    isMicrophoneActive = false
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 5. Bottom Bar: Capsule text field + microphone + glowing Cyan send button
            AiAssistantBottomBar(
                inputText = inputText,
                onInputChanged = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank() && !state.isGenerating) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        val textToSend = inputText
                        inputText = ""
                        scope.launch {
                            engine.askQuestion(textToSend)
                        }
                    }
                },
                isGenerating = state.isGenerating,
                isMicrophoneActive = isMicrophoneActive,
                onToggleMicrophone = {
                    isMicrophoneActive = !isMicrophoneActive
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            )
        }

        // Hybrid Architecture Diagram & Test Modal
        if (state.showArchitectureModal) {
            HybridArchitectureModal(
                state = state,
                onDismiss = { engine.setArchitectureModalVisible(false) },
                onToggleInternet = { engine.toggleInternetAvailability(it) },
                onSelectRoutingPreference = { engine.setRoutingPreference(it) }
            )
        }
    }
}

/**
 * 1. Header Bar: "🤖 BANG AI Assistant" with active engine capsule badge
 */
@Composable
private fun AiAssistantHeader(
    activeEngine: AiEngineType,
    routingPref: EngineRoutingPreference,
    isInternetAvailable: Boolean,
    onBack: () -> Unit,
    onOpenArchitecture: () -> Unit,
    onClearChat: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Surface(
        color = ObsidianSlate,
        border = BorderStroke(1.dp, Color(0xFF162030)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_assistant_header")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Back button & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp).testTag("ai_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🤖 BANG AI Assistant",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 0.2.sp
                            ),
                            color = TextPrimary
                        )
                    }

                    // Engine Capsule Badge: "⚡ On-Device Engine Active (Offline)" or "☁️ Cloud LLM Active (Online)"
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (activeEngine == AiEngineType.OFFLINE_SLM) Color(0xFF0C1F2B) else Color(0xFF0D223B),
                        border = BorderStroke(
                            1.dp,
                            if (activeEngine == AiEngineType.OFFLINE_SLM) ElectricCyan.copy(alpha = glowAlpha)
                            else Color(0xFF38BDF8).copy(alpha = 0.8f)
                        ),
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenArchitecture() }
                            .testTag("ai_engine_capsule_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (activeEngine == AiEngineType.OFFLINE_SLM) KineticEmerald else Color(0xFF38BDF8))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (activeEngine == AiEngineType.OFFLINE_SLM) "⚡ On-Device Engine Active (Offline)" else "☁️ Cloud LLM Active (Online)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeEngine == AiEngineType.OFFLINE_SLM) ElectricCyan else Color(0xFF38BDF8)
                                )
                            )
                        }
                    }
                }
            }

            // Right: Actions (Architecture Diagram Info & Clear Chat)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenArchitecture,
                    modifier = Modifier.size(36.dp).testTag("ai_architecture_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Architecture diagram & telemetry",
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier.size(36.dp).testTag("ai_clear_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear chat history",
                        tint = TextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

/**
 * 2. Quick Chips (Horizontal Scroll)
 */
@Composable
private fun QuickChipsRow(
    chips: List<String>,
    onChipClicked: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("ai_quick_chips_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEachIndexed { index, chipText ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF101926),
                border = BorderStroke(1.dp, Color(0xFF243348)),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onChipClicked(chipText) }
                    .testTag("quick_chip_$index")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chipText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            color = TextPrimary
                        )
                    )
                }
            }
        }
    }
}

/**
 * 3. User Message: Right-aligned deep charcoal container
 */
@Composable
private fun UserMessageCard(message: AiAssistantMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                color = DeepCharcoalUserMsg,
                border = BorderStroke(1.dp, Color(0xFF2A394E)),
                shadowElevation = 2.dp,
                modifier = Modifier.testTag("user_msg_${message.id}")
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp)
                )
            }

            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = message.formattedTime,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                ),
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

/**
 * 4. AI Response: Left-aligned translucent panel with glowing Electric Cyan border & "🤖 Offline SLM" badge
 */
@Composable
private fun AiMessageCard(
    message: AiAssistantMessage,
    isGenerating: Boolean,
    onCopy: () -> Unit,
    isCopied: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseBorder")
    val borderPulse by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderPulse"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                color = GlassmorphicAiCard,
                border = BorderStroke(
                    1.2.dp,
                    if (message.engineType == AiEngineType.OFFLINE_SLM) ElectricCyan.copy(alpha = if (isGenerating) borderPulse else 0.65f)
                    else Color(0xFF38BDF8).copy(alpha = 0.7f)
                ),
                shadowElevation = 4.dp,
                modifier = Modifier.testTag("ai_response_${message.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {

                    // Header row: Badge + Telemetry chip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge: "🤖 Offline SLM" or "☁️ Cloud LLM"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (message.engineType == AiEngineType.OFFLINE_SLM) ElectricCyan.copy(alpha = 0.15f)
                                    else Color(0xFF38BDF8).copy(alpha = 0.15f)
                                )
                                .border(
                                    1.dp,
                                    if (message.engineType == AiEngineType.OFFLINE_SLM) ElectricCyan.copy(alpha = 0.5f)
                                    else Color(0xFF38BDF8).copy(alpha = 0.5f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 7.dp, vertical = 2.5.dp)
                        ) {
                            Text(
                                text = message.engineType.badgeText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp,
                                    color = if (message.engineType == AiEngineType.OFFLINE_SLM) ElectricCyan else Color(0xFF38BDF8)
                                )
                            )
                        }

                        // Telemetry chip: e.g. "42ms • NPU"
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${message.latencyMs}ms • ${if (message.engineType == AiEngineType.OFFLINE_SLM) "0KB Egress" else "Cloud Edge"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isGenerating) {
                        // Streaming typing shimmer dots
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ElectricCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    } else {
                        // Render formatted Markdown-like text
                        FormattedMarkdownText(text = message.text)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Footer with RAG source tag, copy action, and timestamp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        message.ragSource?.let { source ->
                            Text(
                                text = "Source: $source",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onCopy,
                                modifier = Modifier.size(24.dp).testTag("copy_button_${message.id}")
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy message",
                                    tint = if (isCopied) KineticEmerald else TextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = message.formattedTime,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Basic Markdown / Bullet formatted text rendering for clean cyber aesthetics
 */
@Composable
private fun FormattedMarkdownText(text: String) {
    val lines = text.lines()

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        lines.forEach { rawLine ->
            val line = rawLine.trim()
            when {
                line.startsWith("### ") -> {
                    Text(
                        text = line.removePrefix("### "),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 13.5.sp,
                            color = ElectricCyan
                        )
                    )
                }
                line.startsWith("- ") || line.startsWith("• ") -> {
                    Row(
                        modifier = Modifier.padding(start = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = line.removePrefix("- ").removePrefix("• "),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.5.sp,
                                lineHeight = 17.5.sp,
                                color = TextPrimary
                            )
                        )
                    }
                }
                line.matches(Regex("^\\d+\\.\\s.*")) -> {
                    Row(
                        modifier = Modifier.padding(start = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        val numberPrefix = line.substringBefore(". ") + "."
                        val content = line.substringAfter(". ")
                        Text(
                            text = numberPrefix,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AmberGold,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.5.sp,
                                lineHeight = 17.5.sp,
                                color = TextPrimary
                            )
                        )
                    }
                }
                line.isEmpty() -> {
                    Spacer(modifier = Modifier.height(2.dp))
                }
                else -> {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            color = TextPrimary
                        )
                    )
                }
            }
        }
    }
}

/**
 * 5. Bottom Bar: Capsule text field reading "Ask BANG AI about mesh, statuses, or setup..." with mic and glowing Cyan send button
 */
@Composable
private fun AiAssistantBottomBar(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
    isGenerating: Boolean,
    isMicrophoneActive: Boolean,
    onToggleMicrophone: () -> Unit
) {
    Surface(
        color = ObsidianSlate,
        border = BorderStroke(1.dp, Color(0xFF162030)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_bottom_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Capsule Text Field Container
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF111A27),
                border = BorderStroke(1.dp, Color(0xFF23354C)),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (inputText.isEmpty()) {
                            Text(
                                text = "Ask BANG AI about mesh, statuses, or setup...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = TextMuted
                                ),
                                maxLines = 1
                            )
                        }

                        BasicTextField(
                            value = inputText,
                            onValueChange = onInputChanged,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                fontSize = 13.sp
                            ),
                            cursorBrush = SolidColor(ElectricCyan),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { onSend() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_input_text_field")
                        )
                    }

                    // Microphone Icon in Capsule
                    IconButton(
                        onClick = onToggleMicrophone,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("ai_microphone_button")
                    ) {
                        Icon(
                            imageVector = if (isMicrophoneActive) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice dictation input",
                            tint = if (isMicrophoneActive) ElectricCyan else TextSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Glowing Cyan Send Button
            val canSend = inputText.isNotBlank() && !isGenerating
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (canSend) Brush.linearGradient(listOf(ElectricCyan, Color(0xFF00B0FF)))
                        else Brush.linearGradient(listOf(Color(0xFF1C2A3A), Color(0xFF162230)))
                    )
                    .border(
                        1.dp,
                        if (canSend) ElectricCyan else Color(0xFF26374D),
                        CircleShape
                    )
                    .clickable(enabled = canSend) { onSend() }
                    .testTag("ai_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send prompt",
                    tint = if (canSend) Color(0xFF080C14) else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Hybrid Architecture Visualizer & Engine Switcher Modal
 * Demonstrates:
 * User Question Input -> [ Internet Available? ] -> Cloud LLM vs On-Device SLM -> Formatted UI Response
 */
@Composable
private fun HybridArchitectureModal(
    state: com.example.ai.BangAiAssistantState,
    onDismiss: () -> Unit,
    onToggleInternet: (Boolean) -> Unit,
    onSelectRoutingPreference: (EngineRoutingPreference) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = ObsidianSlate,
            border = BorderStroke(1.2.dp, ElectricCyan.copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("hybrid_architecture_modal")
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⚡", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HYBRID AI ARCHITECTURE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            ),
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "CLOSE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        ),
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // The Architecture ASCII / Diagram Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1520)),
                    border = BorderStroke(1.dp, Color(0xFF1E2E44)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ROUTING TOPOLOGY (ONLINE / OFFLINE MESH)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual Flow Box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF070B12), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF172538), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "┌───────────────────────────────┐\n" +
                                        "│      User Question Input      │\n" +
                                        "└───────────────┬───────────────┘\n" +
                                        "                │\n" +
                                        "      [ Internet Available? ]\n" +
                                        "      /                   \\\n" +
                                        " YES /                     \\ NO (Mesh/Off)\n" +
                                        "    ▼                       ▼\n" +
                                        "┌───────────────┐   ┌───────────────────┐\n" +
                                        "│ Cloud LLM API │   │  On-Device SLM    │\n" +
                                        "│ (Fast & Deep) │   │  (ExecuTorch/NPU) │\n" +
                                        "└───────┬───────┘   └─────────┬─────────┘\n" +
                                        "        │                     │\n" +
                                        "        └───────────┬─────────┘\n" +
                                        "                    ▼\n" +
                                        "     ┌─────────────────────────────┐\n" +
                                        "     │    Formatted UI Response    │\n" +
                                        "     └─────────────────────────────┘",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.5.sp,
                                    lineHeight = 11.5.sp,
                                    color = ElectricCyan
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Test Controls: Internet Simulation
                Text(
                    text = "LIVE ENVIRONMENT SIMULATOR:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle 1: Internet Connection Simulator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF111A28))
                        .border(1.dp, Color(0xFF223246), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (state.isInternetSimulatedAvailable) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = if (state.isInternetSimulatedAvailable) Color(0xFF38BDF8) else KineticEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (state.isInternetSimulatedAvailable) "Internet Connected (Cellular/Wi-Fi)" else "Offline Grid (BLE / Wi-Fi Mesh)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = TextPrimary
                                )
                            )
                        }
                        Text(
                            text = if (state.isInternetSimulatedAvailable) "High-performance Cloud LLM API active" else "Running 100% on phone hardware via ExecuTorch SLM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        )
                    }

                    Switch(
                        checked = state.isInternetSimulatedAvailable,
                        onCheckedChange = { onToggleInternet(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF38BDF8),
                            checkedTrackColor = Color(0xFF1E3A5F),
                            uncheckedThumbColor = KineticEmerald,
                            uncheckedTrackColor = Color(0xFF132B20)
                        ),
                        modifier = Modifier.testTag("toggle_internet_simulation")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Routing Preferences
                Text(
                    text = "ENGINE ROUTING PREFERENCE:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                EngineRoutingPreference.entries.forEach { pref ->
                    val isSelected = state.routingPreference == pref
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF15263A) else Color(0xFF0F1722))
                            .border(
                                1.dp,
                                if (isSelected) ElectricCyan else Color(0xFF1C2A3C),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSelectRoutingPreference(pref) }
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) ElectricCyan else TextMuted)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = pref.displayName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isSelected) ElectricCyan else TextPrimary
                                )
                            )
                            Text(
                                text = pref.description,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Gemini Model Tier Selector:
 * gemini-3.8-flash for general tasks,
 * gemini-3.1-flash-lite for tasks that should happen fast,
 * gemini-4-argon for particularly complex tasks,
 * and On-Device SLM for offline mesh.
 */
@Composable
private fun GeminiModelTierTabsRow(
    selectedTier: GeminiModelTier,
    onSelectTier: (GeminiModelTier) -> Unit
) {
    Surface(
        color = Color(0xFF0C1420),
        border = BorderStroke(0.7.dp, Color(0xFF1E2D40)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GeminiModelTier.values().forEach { tier ->
                val isSelected = tier == selectedTier
                val badgeColor = when (tier) {
                    GeminiModelTier.GENERAL_FLASH -> ElectricCyan
                    GeminiModelTier.FAST_LITE -> KineticEmerald
                    GeminiModelTier.COMPLEX_ARGON -> UltraViolet
                    GeminiModelTier.OFFLINE_SLM -> AmberGold
                }

                Surface(
                    color = if (isSelected) Color(0xFF132236) else Color(0x800D1624),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) badgeColor else Color(0xFF1C2B3E)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectTier(tier) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) badgeColor else TextMuted)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = tier.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp,
                                    color = if (isSelected) badgeColor else TextSecondary
                                )
                            )
                            Text(
                                text = tier.taskTypeLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    color = TextMuted
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * System Role Instruction Banner ("as BANG AI")
 */
@Composable
private fun BangAiSystemRoleBanner(
    roleInstruction: String,
    selectedTier: GeminiModelTier
) {
    Surface(
        color = Color(0x9008101C),
        border = BorderStroke(0.5.dp, Color(0xFF162538)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ROLE: BANG AI Companion & Mesh Intelligence",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = ElectricCyan
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F1E2E))
                    .border(0.5.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Multi-Turn Memory ON",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        color = KineticEmerald
                    )
                )
            }
        }
    }
}

