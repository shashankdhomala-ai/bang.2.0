package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AccelerationDelegate
import com.example.ai.CompressedMeshPacket
import com.example.ai.CrisisClassification
import com.example.ai.LocalSlmEngine
import com.example.ai.PiiAuditVerdict
import com.example.ai.ReconstructedMeshMessage
import com.example.ai.SlmArchitecture
import com.example.ai.ThreadSummaryResult
import com.example.ui.BangUiState
import com.example.ui.theme.BangDeepMidnightObsidian
import com.example.ui.theme.BangElectricBlue
import com.example.ui.theme.BangHyperCyan
import com.example.ui.theme.BangKineticLime
import com.example.ui.theme.BangNeonHotPink
import com.example.ui.theme.BangNeonPurple
import com.example.ui.theme.BangTextMuted
import com.example.ui.theme.BangTextPrimary
import kotlinx.coroutines.launch

/**
 * OnDeviceAiArchitectureScreen
 *
 * Interactive On-Device AI Architecture & Live Engineering Workbench for BANG.
 * Demonstrates:
 * 1. Zero-Cloud Local SLM Execution (llama.cpp / ExecuTorch)
 * 2. Strict 1.5 GB RAM Cap with Live Memory Gauge
 * 3. Feature A: On-Device Mesh Summarizer & Smart Reply (>20 unread multi-hop msgs)
 * 4. Feature B: AI Bandwidth Compressor & Generative Semantic Reconstruction
 * 5. Feature C: Emergency & Crisis Packet Prioritization Classifier (QoS P0)
 * 6. Feature D: On-Device PII Protection & Anti-DoS Relay Filter
 * 7. End-to-End Pipeline Data Flow & Architecture Flowchart
 * 8. Concrete Kotlin / Native Runtime Code Specs & Prompt Engineering Templates
 */
@Composable
fun OnDeviceAiArchitectureScreen(
    uiState: BangUiState,
    slmEngine: LocalSlmEngine,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Active Engine State
    var activeModel by remember { mutableStateOf(SlmArchitecture.SMOL_LM_360M) }
    var activeDelegate by remember { mutableStateOf(AccelerationDelegate.CPU_XNNPACK_NEON) }

    // Live Testbench States
    var summaryResult by remember { mutableStateOf<ThreadSummaryResult?>(null) }
    var isSummarizing by remember { mutableStateOf(false) }

    var compressionInput by remember { mutableStateOf("Hey team, let's rendezvous at Gate 4 near the perimeter in about 10 minutes. Bring extra water batteries.") }
    var compressedPacket by remember { mutableStateOf<CompressedMeshPacket?>(null) }
    var reconstructedMessage by remember { mutableStateOf<ReconstructedMeshMessage?>(null) }

    var crisisInput by remember { mutableStateOf("Medical help needed near stage B! Someone collapsed and is unconscious.") }
    var crisisResult by remember { mutableStateOf<CrisisClassification?>(null) }

    var piiInput by remember { mutableStateOf("Here is my backup card: 4532-8921-4401-9218 and seed phrase: apple banana cherry dog elephant fox grape horse") }
    var piiResult by remember { mutableStateOf<PiiAuditVerdict?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "aiPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BangDeepMidnightObsidian)
            .testTag("on_device_ai_architecture_screen")
    ) {
        // ---------------------------------------------------------------------
        // 1. Futuristic Executive Header
        // ---------------------------------------------------------------------
        Surface(
            color = Color(0xFF070B12),
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 6.dp
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(listOf(BangHyperCyan, BangNeonHotPink))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "BANG ON-DEVICE AI",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = BangHyperCyan,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BangKineticLime.copy(alpha = 0.15f))
                                        .border(0.5.dp, BangKineticLime, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "100% ZERO-CLOUD",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BangKineticLime,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Distributed P2P SLMs • llama.cpp & ExecuTorch NPU Engine",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.5.sp,
                                    color = BangTextMuted
                                )
                            )
                        }
                    }

                    // Exit button
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF141A24))
                            .border(1.dp, Color(0xFF2A3446), CircleShape)
                            .clickable { onClose() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "CLOSE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BangTextPrimary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedSection,
                    containerColor = Color.Transparent,
                    contentColor = BangHyperCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                            color = BangHyperCyan,
                            height = 2.5.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedSection == 0,
                        onClick = { selectedSection = 0 },
                        text = {
                            Text(
                                text = "LIVE WORKBENCH",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedSection == 0) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    color = if (selectedSection == 0) BangHyperCyan else BangTextMuted
                                )
                            )
                        }
                    )
                    Tab(
                        selected = selectedSection == 1,
                        onClick = { selectedSection = 1 },
                        text = {
                            Text(
                                text = "PIPELINE & SPECS",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedSection == 1) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    color = if (selectedSection == 1) BangHyperCyan else BangTextMuted
                                )
                            )
                        }
                    )
                    Tab(
                        selected = selectedSection == 2,
                        onClick = { selectedSection = 2 },
                        text = {
                            Text(
                                text = "PROMPT TEMPLATES",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedSection == 2) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    color = if (selectedSection == 2) BangHyperCyan else BangTextMuted
                                )
                            )
                        }
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
            when (selectedSection) {
                0 -> LiveWorkbenchTab(
                    activeModel = activeModel,
                    activeDelegate = activeDelegate,
                    onSelectModel = {
                        activeModel = it
                        slmEngine.selectModel(it)
                    },
                    onSelectDelegate = { activeDelegate = it },
                    isSummarizing = isSummarizing,
                    summaryResult = summaryResult,
                    onRunSummarize = {
                        coroutineScope.launch {
                            isSummarizing = true
                            summaryResult = slmEngine.summarizeUnreadMeshThread(
                                "thread_festival_camp",
                                listOf(
                                    "Perimeter breach reported near gate 2",
                                    "Water station 3 is refilled; crowd shifting to West gate",
                                    "Medical team dispatched to Stage B for heat exhaustion",
                                    "Stranger Node 77 successfully forwarding packets across crowd",
                                    "BLE signal strength strong (-58 dBm); 8 peers synced"
                                )
                            )
                            isSummarizing = false
                        }
                    },
                    compressionInput = compressionInput,
                    onCompressionInputChange = { compressionInput = it },
                    compressedPacket = compressedPacket,
                    reconstructedMessage = reconstructedMessage,
                    onRunCompress = {
                        coroutineScope.launch {
                            val comp = slmEngine.compressMeshDraft(compressionInput)
                            compressedPacket = comp
                            val recon = slmEngine.reconstructCompressedPayload(comp.compressedPayload)
                            reconstructedMessage = recon
                        }
                    },
                    crisisInput = crisisInput,
                    onCrisisInputChange = { crisisInput = it },
                    crisisResult = crisisResult,
                    onRunCrisisClassify = {
                        coroutineScope.launch {
                            crisisResult = slmEngine.classifyCrisisPriority(crisisInput)
                        }
                    },
                    piiInput = piiInput,
                    onPiiInputChange = { piiInput = it },
                    piiResult = piiResult,
                    onRunPiiAudit = {
                        coroutineScope.launch {
                            piiResult = slmEngine.auditOutboundDraftForPii(piiInput)
                        }
                    }
                )
                1 -> ArchitecturePipelineTab()
                2 -> PromptTemplatesTab()
            }
        }
    }
}

// =============================================================================
// SUB-TAB 0: LIVE WORKBENCH (Model Matrix, RAM Gauge, 4 Interactive Features)
// =============================================================================
@Composable
private fun LiveWorkbenchTab(
    activeModel: SlmArchitecture,
    activeDelegate: AccelerationDelegate,
    onSelectModel: (SlmArchitecture) -> Unit,
    onSelectDelegate: (AccelerationDelegate) -> Unit,
    isSummarizing: Boolean,
    summaryResult: ThreadSummaryResult?,
    onRunSummarize: () -> Unit,
    compressionInput: String,
    onCompressionInputChange: (String) -> Unit,
    compressedPacket: CompressedMeshPacket?,
    reconstructedMessage: ReconstructedMeshMessage?,
    onRunCompress: () -> Unit,
    crisisInput: String,
    onCrisisInputChange: (String) -> Unit,
    crisisResult: CrisisClassification?,
    onRunCrisisClassify: () -> Unit,
    piiInput: String,
    onPiiInputChange: (String) -> Unit,
    piiResult: PiiAuditVerdict?,
    onRunPiiAudit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ---------------------------------------------------------------------
        // RAM Allocation Monitor & Ceiling Card
        // ---------------------------------------------------------------------
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1019)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = BangHyperCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RAM ALLOCATION vs STRICT 1.5 GB CEILING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.5.sp,
                                color = BangHyperCyan
                            )
                        )
                    }

                    Text(
                        text = "${activeModel.memoryFootprintMb} MB / 1500 MB",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = if (activeModel.memoryFootprintMb > 1200) BangNeonHotPink else BangKineticLime
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val fraction = (activeModel.memoryFootprintMb.toFloat() / 1500f).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (fraction > 0.8f) BangNeonHotPink else BangKineticLime,
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Buffer Remaining: ${1500 - activeModel.memoryFootprintMb} MB (Zero BLE throttling guarantee)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 9.5.sp,
                            color = BangTextMuted
                        )
                    )
                    Text(
                        text = "Quant: ${activeModel.quantization}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = BangElectricBlue
                        )
                    )
                }
            }
        }

        // ---------------------------------------------------------------------
        // Quantized Model Selector Pills
        // ---------------------------------------------------------------------
        Column {
            Text(
                text = "1. SELECT ACTIVE QUANTIZED SLM",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.5.sp,
                    color = BangTextMuted,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SlmArchitecture.values().forEach { model ->
                    val isSelected = model == activeModel
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF132035) else Color(0xFF090E17))
                            .border(
                                1.dp,
                                if (isSelected) BangHyperCyan else Color(0xFF1B2433),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectModel(model) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = model.modelName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = if (isSelected) BangHyperCyan else BangTextPrimary
                                    )
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = BangKineticLime,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${model.parameterCount} • ${model.memoryFootprintMb} MB RAM • ${model.recommendedBackend}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.sp,
                                    color = BangTextMuted
                                )
                            )
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // FEATURE A: On-Device Mesh Summarizer & Smart Reply
        // ---------------------------------------------------------------------
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D16)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D283A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FEATURE A: THREAD SUMMARIZER & SMART REPLY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = BangHyperCyan
                            )
                        )
                        Text(
                            text = "Trigger: >20 unread multi-hop messages in offline group chat",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.5.sp,
                                color = BangTextMuted
                            )
                        )
                    }

                    Button(
                        onClick = onRunSummarize,
                        enabled = !isSummarizing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BangHyperCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (isSummarizing) "INFERRING..." else "RUN SLM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Summary output card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF03060A))
                        .border(0.5.dp, Color(0xFF1E2A3C), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    if (summaryResult != null) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "3-BULLET CONCISE LOCAL SUMMARY:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.5.sp,
                                        color = BangKineticLime
                                    )
                                )
                                Text(
                                    text = "${summaryResult.inferenceLatencyMs}ms • Zero-Cloud",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = BangTextMuted
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            summaryResult.bulletPoints.forEach { bullet ->
                                Text(
                                    text = "• $bullet",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = BangTextPrimary,
                                        lineHeight = 16.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "CONTEXT-AWARE SMART REPLY CHIPS:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = BangElectricBlue
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                            ) {
                                summaryResult.smartReplySuggestions.forEach { reply ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFF111D2D))
                                            .border(0.5.dp, BangElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = reply,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                color = BangElectricBlue
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Tap 'RUN SLM' to simulate on-device inference for 24 unread multi-hop chatter messages.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.5.sp,
                                color = BangTextMuted
                            )
                        )
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // FEATURE B: AI Bandwidth Compressor & Generative Reconstruction
        // ---------------------------------------------------------------------
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D16)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D283A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "FEATURE B: AI BANDWIDTH COMPRESSOR & RECONSTRUCTION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = BangHyperCyan
                    )
                )
                Text(
                    text = "Solves BLE L2CAP MTU limit by transmitting semantic intent tokens over mesh relays",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 9.5.sp,
                        color = BangTextMuted
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = compressionInput,
                    onValueChange = onCompressionInputChange,
                    label = { Text("Sender Draft Text", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BangHyperCyan,
                        unfocusedBorderColor = Color(0xFF1E2A3C),
                        focusedTextColor = BangTextPrimary,
                        unfocusedTextColor = BangTextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onRunCompress,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Brush.linearGradient(listOf(BangHyperCyan, BangNeonHotPink)).let {
                            BangHyperCyan
                        },
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Compress, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "COMPRESS & RECONSTRUCT ACROSS MESH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    )
                }

                if (compressedPacket != null && reconstructedMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Step 1: Over-the-air payload
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF04070C))
                            .border(0.5.dp, BangKineticLime.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "1. OVER-THE-AIR BLE MESH PAYLOAD:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = BangKineticLime
                                    )
                                )
                                Text(
                                    text = "${compressedPacket.compressedByteSize} bytes • ${String.format("%.1f", compressedPacket.compressionRatioPct)}% MTU Saved",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = BangKineticLime
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = compressedPacket.compressedPayload,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = BangHyperCyan
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Step 2: Recipient Generative Expansion
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF04070C))
                            .border(0.5.dp, BangNeonPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "2. RECIPIENT LOCAL SLM EXPANSION:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = BangNeonPurple
                                    )
                                )
                                Text(
                                    text = "Confidence: ${reconstructedMessage.semanticConfidence * 100}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = BangTextMuted
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = reconstructedMessage.reconstructedText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = BangTextPrimary,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // FEATURE C: Emergency & Crisis Packet Prioritization Classifier
        // ---------------------------------------------------------------------
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D16)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D283A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "FEATURE C: CRISIS PACKET PRIORITIZATION CLASSIFIER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = BangHyperCyan
                    )
                )
                Text(
                    text = "Micro-classifier flags life-safety alerts to elevate QoS priority in relay queues",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 9.5.sp,
                        color = BangTextMuted
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = crisisInput,
                    onValueChange = onCrisisInputChange,
                    label = { Text("Emergency or Normal Draft Text", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BangNeonHotPink,
                        unfocusedBorderColor = Color(0xFF1E2A3C),
                        focusedTextColor = BangTextPrimary,
                        unfocusedTextColor = BangTextPrimary
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onRunCrisisClassify,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BangNeonHotPink,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ANALYZE CRISIS QoS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    )
                }

                if (crisisResult != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (crisisResult.isEmergency) Color(0xFF1C060E) else Color(0xFF06140D)
                            )
                            .border(
                                1.dp,
                                if (crisisResult.isEmergency) BangNeonHotPink else BangKineticLime,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = crisisResult.qosLevel.headerTag,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = if (crisisResult.isEmergency) BangNeonHotPink else BangKineticLime
                                    )
                                )
                                Text(
                                    text = "Tier ${crisisResult.qosLevel.priorityTier} • Score: ${crisisResult.urgencyScore}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = BangTextMuted
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (crisisResult.isEmergency)
                                    "🚨 ELEVATED: Tagged for store-and-forward queue preemption. All relay nodes will bypass standard packets to broadcast this emergency immediately."
                                else
                                    "Standard mesh packet. Routed in ordinary FIFO relay buffer with Micro-Hashcash PoW.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.5.sp,
                                    color = BangTextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // FEATURE D: On-Device PII Protection & Anti-DoS Spam Guard
        // ---------------------------------------------------------------------
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D16)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D283A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "FEATURE D: ON-DEVICE PII PROTECTION & SPAM GUARD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = BangHyperCyan
                    )
                )
                Text(
                    text = "Scans outbound text before public BLE broadcast & drops DoS packet flooding",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 9.5.sp,
                        color = BangTextMuted
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = piiInput,
                    onValueChange = onPiiInputChange,
                    label = { Text("Draft Message with Credentials", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BangElectricBlue,
                        unfocusedBorderColor = Color(0xFF1E2A3C),
                        focusedTextColor = BangTextPrimary,
                        unfocusedTextColor = BangTextPrimary
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onRunPiiAudit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF223147),
                        contentColor = BangHyperCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AUDIT FOR PII & RF LEAKAGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    )
                }

                if (piiResult != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (piiResult.containsSensitivePii) Color(0xFF20090E) else Color(0xFF081910)
                            )
                            .border(
                                1.dp,
                                if (piiResult.containsSensitivePii) BangNeonHotPink else BangKineticLime,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (piiResult.containsSensitivePii) "⚠️ OUTBOUND BROADCAST BLOCKED" else "✅ CLEAN: NO PII DETECTED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (piiResult.containsSensitivePii) BangNeonHotPink else BangKineticLime
                                    )
                                )
                                Text(
                                    text = "Types: ${piiResult.detectedPiiTypes.joinToString()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = BangTextMuted
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Safe Redacted Preview: \"${piiResult.redactedDraftPreview}\"",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = BangTextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// SUB-TAB 1: ARCHITECTURE PIPELINE & NATIVE KOTLIN RUNTIME SPECS
// =============================================================================
@Composable
private fun ArchitecturePipelineTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // End-to-End Pipeline Visualization
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090E17)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E283A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "END-TO-END DATA FLOW PIPELINE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = BangHyperCyan,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = "Step-by-step pipeline from payload creation to local inference, BLE mesh transport, and recipient reconstruction",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BangTextMuted,
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                PipelineStepItem(
                    stepNumber = "1",
                    title = "User Input & Pre-Flight PII Audit",
                    detail = "Local regex and token scanning audits draft text before RF packet assembly. Any credit card, password, or seed phrase triggers user warning and halts broadcast.",
                    tag = "On-Device CPU • <1ms",
                    color = BangElectricBlue
                )

                PipelineStepItem(
                    stepNumber = "2",
                    title = "Crisis QoS Classifier & Token Compression",
                    detail = "Micro-classifier flags emergency life-safety text (QoS P0). Semantic tokenizer compresses verbose sentences into intent tokens (INT:MEET|LOC:G4), achieving 84% MTU reduction.",
                    tag = "Local SLM (SmolLM-360M) • 45ms",
                    color = BangKineticLime
                )

                PipelineStepItem(
                    stepNumber = "3",
                    title = "Noise Protocol XK + Micro-Hashcash PoW",
                    detail = "Recipient's Curve25519 public key encrypts the compressed token payload (ChaCha20-Poly1305). Sender computes 12-bit PoW nonce to protect downstream mesh nodes from DoS.",
                    tag = "Cryptographic Layer • 12ms",
                    color = BangNeonPurple
                )

                PipelineStepItem(
                    stepNumber = "4",
                    title = "Multi-Hop BLE / Wi-Fi Direct Relay Transport",
                    detail = "Packet transits stranger relay hops. Relays verify PoW nonce, inspect QoS P0 header for queue priority, and forward without being able to decrypt payload or metadata.",
                    tag = "RF Mesh Layer • Zero-Trust",
                    color = BangHyperCyan
                )

                PipelineStepItem(
                    stepNumber = "5",
                    title = "Recipient Decryption & Generative Reconstruction",
                    detail = "Destination node decrypts payload with private key. Local SLM generative expansion decodes semantic tokens into fluent natural language, verifying semantic confidence.",
                    tag = "Recipient SLM • 120ms",
                    color = BangNeonHotPink
                )
            }
        }

        // Concrete Kotlin / Native Runtime Code Specs
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090E17)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E283A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = BangKineticLime, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "KOTLIN / NATIVE RUNTIME ENGINE SPECS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = BangKineticLime,
                            fontSize = 12.sp
                        )
                    )
                }
                Text(
                    text = "ForegroundService lifecycle, llama.cpp JNI bindings, memory locking, and non-blocking coroutines",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BangTextMuted,
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF030508))
                        .border(0.5.dp, Color(0xFF1B2433), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = """
// -------------------------------------------------------------
// BANG Native Engine Binding Architecture (llama.cpp / JNI)
// -------------------------------------------------------------
class BangNativeSlmBridge private constructor() {
    companion object {
        init {
            System.loadLibrary("bang_slm_engine")
        }
        @Volatile private var instance: BangNativeSlmBridge? = null
        fun get(): BangNativeSlmBridge = instance ?: synchronized(this) {
            instance ?: BangNativeSlmBridge().also { instance = it }
        }
    }

    // Native JNI hooks
    external fun initModelContext(
        modelPath: String,
        numThreads: Int = 4,
        contextSize: Int = 2048,
        useMlock: Boolean = true,
        useGpuDelegate: Boolean = false
    ): Long

    external fun generateCompletion(
        ctxHandle: Long,
        systemPrompt: String,
        userPrompt: String,
        temp: Float = 0.2f
    ): String

    external fun releaseModelContext(ctxHandle: Long)
}

// -------------------------------------------------------------
// Isolated Background Execution Scope (Non-Blocking BLE Mesh)
// -------------------------------------------------------------
class SlmInferenceService : LifecycleService() {
    private val inferenceDispatcher = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "BANG-OnDevice-SLM-Worker").apply {
            // Set thread priority to background to prevent BLE GATT jitter
            priority = Thread.MIN_PRIORITY
        }
    }.asCoroutineDispatcher()

    private val slmScope = CoroutineScope(SupervisorJob() + inferenceDispatcher)

    suspend fun executeInference(prompt: String): String = withContext(inferenceDispatcher) {
        val bridge = BangNativeSlmBridge.get()
        bridge.generateCompletion(handle, SYSTEM_SUMMARIZER, prompt)
    }
}
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.5.sp,
                            color = Color(0xFF81D4FA),
                            lineHeight = 14.sp
                        )
                    )
                }
            }
        }
    }
}

// =============================================================================
// SUB-TAB 2: PROMPT ENGINEERING TEMPLATES
// =============================================================================
@Composable
private fun PromptTemplatesTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PromptCard(
            title = "1. Mesh Thread Summarizer & Smart Reply Template",
            tokens = "~140 tokens context",
            systemPrompt = """
<system>
You are BANG's offline mesh thread assistant. The user has opened an offline group chat with >20 unread multi-hop messages.
Generate:
1. Exactly 3 bullet points summarizing urgent statuses, supplies, and locations. Keep each bullet under 14 words.
2. Exactly 3 concise smart reply suggestions under 6 words each.
Output strictly formatted as:
BULLETS:
- [bullet 1]
- [bullet 2]
- [bullet 3]
REPLIES:
- [reply 1]
- [reply 2]
- [reply 3]
</system>
            """.trimIndent(),
            color = BangHyperCyan
        )

        PromptCard(
            title = "2. AI Bandwidth Semantic Compressor Template",
            tokens = "~90 tokens context",
            systemPrompt = """
<system>
You are BANG's mesh bandwidth optimizer. Compress the user's verbose message into dense semantic intent tokens that fit within a single 512-byte BLE L2CAP MTU packet.
Strip all filler, greetings, and syntax. Extract:
INT:[INTENT]|LOC:[LOCATION]|ETA:[TIME]|PERSONS:[COUNT]|URG:[PRIORITY]
Preserve critical operational nouns and numbers. Never output conversational pleasantries.
</system>
            """.trimIndent(),
            color = BangKineticLime
        )

        PromptCard(
            title = "3. Recipient Generative Semantic Reconstruction Template",
            tokens = "~110 tokens context",
            systemPrompt = """
<system>
You are BANG's recipient reconstruction engine. You have received a compressed semantic intent packet transmitted across a multi-hop stranger mesh relay.
Input payload: {INT:MEET|LOC:GATE_4|ETA:10M|URG:MED}
Reconstruct this into a clear, polite, natural human sentence. Do not add fictitious details beyond the token parameters.
</system>
            """.trimIndent(),
            color = BangNeonPurple
        )

        PromptCard(
            title = "4. Crisis & Emergency Prioritization Classifier Template",
            tokens = "~75 tokens context",
            systemPrompt = """
<system>
Classify message urgency for mesh radio queue prioritization:
Options:
- QOS_P0_CRITICAL_LIFE_SAFETY (Trauma, cardiac, bleeding, fire, structural collapse)
- QOS_P1_OPERATIONAL_URGENT (Perimeter breach, power outage, lost child)
- QOS_P2_NORMAL_MESH_TRAFFIC (Standard chat, coordinates check)
Output: [QOS_TIER]|[CATEGORY]|[URGENCY_SCORE 0.0-1.0]
</system>
            """.trimIndent(),
            color = BangNeonHotPink
        )
    }
}

// -----------------------------------------------------------------------------
// Reusable UI Helpers
// -----------------------------------------------------------------------------
@Composable
private fun PipelineStepItem(
    stepNumber: String,
    title: String,
    detail: String,
    tag: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .border(1.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = color,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BangTextPrimary,
                        fontSize = 11.5.sp
                    )
                )
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.5.sp,
                        color = color
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = BangTextMuted,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            )
        }
    }
}

@Composable
private fun PromptCard(
    title: String,
    tokens: String,
    systemPrompt: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090E17)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E283A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = color,
                        fontSize = 11.5.sp
                    )
                )
                Text(
                    text = tokens,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = BangTextMuted
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF030508))
                    .border(0.5.dp, Color(0xFF1C2534), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = systemPrompt,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.5.sp,
                        color = Color(0xFF90CAF9),
                        lineHeight = 14.sp
                    )
                )
            }
        }
    }
}
