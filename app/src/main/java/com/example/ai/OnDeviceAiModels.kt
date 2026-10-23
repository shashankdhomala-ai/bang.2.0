package com.example.ai

/**
 * Supported on-device Quantized Small Language Models (SLMs)
 */
enum class SlmArchitecture(
    val modelName: String,
    val parameterCount: String,
    val quantization: String,
    val memoryFootprintMb: Int,
    val contextWindowTokens: Int,
    val recommendedBackend: String
) {
    SMOL_LM_360M(
        modelName = "SmolLM-360M-Instruct",
        parameterCount = "360M",
        quantization = "Q4_K_M",
        memoryFootprintMb = 240,
        contextWindowTokens = 2048,
        recommendedBackend = "llama.cpp / CPU (NEON)"
    ),
    GEMMA_2_2B(
        modelName = "Gemma-2-2B-IT",
        parameterCount = "2.6B",
        quantization = "Q4_K_M",
        memoryFootprintMb = 1450,
        contextWindowTokens = 4096,
        recommendedBackend = "ExecuTorch / NPU (Qualcomm HTP / MediaTek APU)"
    ),
    LLAMA_3_2_1B(
        modelName = "Llama-3.2-1B-Instruct",
        parameterCount = "1.23B",
        quantization = "Q4_K_M",
        memoryFootprintMb = 780,
        contextWindowTokens = 4096,
        recommendedBackend = "llama.cpp / Vulkan or OpenCL"
    )
}

/**
 * Hardware accelerator delegate selection
 */
enum class AccelerationDelegate {
    AUTO_NPU_FIRST,
    QUALCOMM_QNN_NPU,
    MEDIATEK_NEURON_NPU,
    CPU_XNNPACK_NEON,
    GPU_VULKAN
}

/**
 * Feature A: Summarization & Smart Reply
 */
data class ThreadSummaryResult(
    val threadId: String,
    val bulletPoints: List<String>,
    val smartReplySuggestions: List<String>,
    val unreadMessagesAnalyzed: Int,
    val inferenceLatencyMs: Long,
    val ramUsedMb: Int
)

/**
 * Feature B: AI Bandwidth Compression & Generative Expansion
 */
data class CompressedMeshPacket(
    val packetId: String,
    val originalCharacterCount: Int,
    val compressedPayload: String, // Semantic shorthand / Intent Tokens
    val compressedByteSize: Int,
    val compressionRatioPct: Float, // e.g. 88.5% smaller
    val targetMtuFit: Boolean = true // Fits in single BLE L2CAP packet (<512 bytes)
)

data class ReconstructedMeshMessage(
    val packetId: String,
    val reconstructedText: String,
    val semanticConfidence: Float,
    val reconstructionLatencyMs: Long
)

/**
 * Feature C: Emergency & Crisis QoS Classification
 */
enum class CrisisQosLevel(val priorityTier: Int, val headerTag: String) {
    CRITICAL_EMERGENCY(0, "QOS_P0_CRITICAL_LIFE_SAFETY"),
    HIGH_URGENCY(1, "QOS_P1_OPERATIONAL_URGENT"),
    STANDARD_CHAT(2, "QOS_P2_NORMAL_MESH_TRAFFIC"),
    BACKGROUND_BULK(3, "QOS_P3_EPIDEMIC_STORE_FORWARD")
}

data class CrisisClassification(
    val qosLevel: CrisisQosLevel,
    val isEmergency: Boolean,
    val detectedEmergencyCategory: String?, // e.g. "MEDICAL_TRAUMA", "EVACUATION", "STRUCTURAL_COLLAPSE"
    val urgencyScore: Float, // 0.0 .. 1.0
    val analysisLatencyMs: Long
)

/**
 * Feature D: PII Protection & DoS Spam Guard
 */
data class PiiAuditVerdict(
    val containsSensitivePii: Boolean,
    val detectedPiiTypes: List<String>, // e.g. "CREDIT_CARD", "PASSWORD", "SEED_PHRASE", "SSN"
    val redactedDraftPreview: String,
    val shouldBlockOutboundBroadcast: Boolean,
    val isSpamOrDosPacket: Boolean = false,
    val reason: String? = null
)
