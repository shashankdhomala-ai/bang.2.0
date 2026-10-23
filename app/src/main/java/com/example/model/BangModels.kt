package com.example.model

enum class ConnectivityMode(val label: String, val description: String) {
    HYBRID_AUTO("Hybrid Mode", "Auto-handover between Cloud, Wi-Fi Direct, and BLE Mesh"),
    OFFLINE_MESH("Mesh Only", "Cellular offline. BLE L2CAP + Wi-Fi Direct P2P active"),
    WIFI_DIRECT_P2P("Wi-Fi Direct P2P", "High-speed ad-hoc peer-to-peer (up to 250 Mbps)"),
    CLOUD_ONLINE("Cloud Online", "Standard low-latency cloud relay routing")
}

/**
 * Zero-Click Contextual UI Modes.
 * Predicts the user's primary action based on hardware motion, proximity, and network telemetry.
 */
enum class PredictiveContextMode(
    val title: String,
    val iconEmoji: String,
    val actionLabel: String,
    val badgeText: String,
    val reasonDescription: String
) {
    STANDARD_CHAT(
        title = "Standard Encrypted",
        iconEmoji = "💬",
        actionLabel = "New Encrypted Chat",
        badgeText = "Online / Ready",
        reasonDescription = "Cellular & Wi-Fi active • Direct peer messaging ready"
    ),
    DRIVING_MOTION(
        title = "Moving Fast / Driving",
        iconEmoji = "🎙️",
        actionLabel = "Hold for Voice Pulse",
        badgeText = "45 km/h Motion",
        reasonDescription = "Zero-click hands-free voice pulse mode active"
    ),
    NEAR_REGISTERED_PEER(
        title = "Near Registered Peer",
        iconEmoji = "🔲",
        actionLabel = "NFC Key Swap",
        badgeText = "Charlie Vance (1.2m)",
        reasonDescription = "Ultra-close proximity detected • Tap to verify Ed25519 key"
    ),
    INTERNET_OUTAGE(
        title = "Cellular Outage",
        iconEmoji = "⚡",
        actionLabel = "Scan Local Mesh Peers",
        badgeText = "Blackout Mode",
        reasonDescription = "Internet down • Direct store-and-forward BLE radar active"
    )
}

enum class DeliveryStatus {
    QUEUED_OFFLINE,
    AUTO_SWITCHING_WIFI,
    STORE_AND_FORWARD,
    RELAYED_HOP,
    DELIVERED,
    READ,
    KEY_EXCHANGE_REQUIRED
}

enum class TransportType(val displayName: String, val maxBandwidth: String, val typicalRange: String) {
    BLE_L2CAP("BLE 5.3 Mesh", "128 kbps", "30-50m"),
    WIFI_DIRECT("Wi-Fi Direct P2P", "250 Mbps", "80-100m"),
    CLOUD_GRPC("Cloud Internet", "Gigabit", "Global")
}

data class MeshHop(
    val nodeId: String,
    val alias: String,
    val rssi: Int,
    val isFriend: Boolean = false, // If false, intermediate node is a completely untrusted stranger
    val transportUsed: TransportType = TransportType.BLE_L2CAP,
    val timeTakenMs: Int = 18
)

data class AutoSwitchEvent(
    val id: String,
    val timestamp: Long,
    val fromTransport: TransportType,
    val toTransport: TransportType,
    val reason: String,
    val throughputGauge: String
)

data class ProofOfWorkHeader(
    val nonce: Long,
    val difficultyBits: Int = 12,
    val hashOutput: String = "0000a4f8921e",
    val computeDurationMs: Long = 28,
    val isVerified: Boolean = true
)

data class CryptographicDeliveryReceipt(
    val messageId: String,
    val recipientAlias: String,
    val recipientPublicKey: String,
    val ed25519Signature: String,
    val timestamp: Long,
    val reverseHops: List<MeshHop> = emptyList(),
    val isSignatureVerified: Boolean = true,
    val bufferPurgedOnRelays: Boolean = true
)

data class ChatMessage(
    val id: String,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val isFromMe: Boolean,
    val deliveryStatus: DeliveryStatus,
    val transport: TransportType,
    val hopRoute: List<MeshHop> = emptyList(),
    val isE2eeEncrypted: Boolean = true,
    val isSos: Boolean = false,
    val attachmentType: String? = null, // "VOICE_6KBPS", "IMAGE_WEBP"
    val autoSwitchNotice: String? = null,
    val opaqueRelayToken: String = "TOK-7A2F-9C14", // What intermediate stranger nodes see instead of real ID
    val powHeader: ProofOfWorkHeader? = ProofOfWorkHeader(nonce = 4912, difficultyBits = 12),
    val deliveryReceipt: CryptographicDeliveryReceipt? = null,
    val replyToText: String? = null,
    val pulseReactions: List<String> = emptyList()
)

data class ChatThread(
    val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int,
    val isGroup: Boolean,
    val preferredTransport: TransportType,
    val activeHops: Int,
    val peerPublicKeyFingerprint: String,
    val involvesStrangerRelay: Boolean = true,
    val routingTag: String = "⚡ 2 Hops via Stranger Bob"
)

data class MeshPulseStory(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val avatarInitials: String,
    val isLocalMesh: Boolean, // true = glowing amber ring (BLE/Wi-Fi Direct offline pulse), false = gradient magenta ring (recent story)
    val hopsBadge: String,    // "⚡ 2 Hops via Stranger Bob", "⚡ 1 Hop (BLE)", "🌐 Cloud Direct"
    val timestampText: String, // "6m ago"
    val caption: String,
    val bgGradientStartHex: Long = 0xFF1C0A30,
    val bgGradientEndHex: Long = 0xFF080C14,
    val pollQuestion: String? = null,
    val pollOptionA: String? = null,
    val pollVotesA: Int = 0,
    val pollOptionB: String? = null,
    val pollVotesB: Int = 0,
    val userVotedOption: Int? = null,
    val hasVoiceNote: Boolean = false,
    val voiceDurationSec: Int = 14,
    val addYoursPrompt: String? = null,
    val addYoursCount: Int = 0,
    val voicePacket: VoicePulsePacket? = null
)

/**
 * 30-Second BLE Voice Pulse Payload.
 * Broadcast as structured packet arrays across the local BLE mesh (expires in 24 hours).
 */
data class VoicePulsePacket(
    val pulseId: String,
    val senderHandle: String,
    val audioOpusData: ByteArray,
    val timestamp: Long = System.currentTimeMillis(),
    val timeToLiveMs: Long = 86_400_000L // Expires automatically in 24 hours
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VoicePulsePacket
        if (pulseId != other.pulseId) return false
        if (senderHandle != other.senderHandle) return false
        if (!audioOpusData.contentEquals(other.audioOpusData)) return false
        if (timestamp != other.timestamp) return false
        return timeToLiveMs == other.timeToLiveMs
    }

    override fun hashCode(): Int {
        var result = pulseId.hashCode()
        result = 31 * result + senderHandle.hashCode()
        result = 31 * result + audioOpusData.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + timeToLiveMs.hashCode()
        return result
    }
}

data class MeshPeerNode(
    val id: String,
    val displayName: String,
    val rssi: Int,              // e.g. -54 dBm
    val distanceMeters: Float,  // e.g. 4.2m
    val angleDegrees: Float,    // for radar display (0..360)
    val batteryPct: Int,        // e.g. 82%
    val hopsAway: Int,          // 1 = direct neighbor, 2 = 1 intermediate relay
    val transport: TransportType,
    val packetsRelayed: Int,
    val isFriend: Boolean = false, // Stranger nodes can still relay!
    val isVerified: Boolean = true,
    val handle: String = "@alex_rivera",
    val keyInfo: String = "Node ID: 8f9a...32c1 (Curve25519 Verified)"
)

data class SosBeacon(
    val id: String,
    val senderName: String,
    val senderId: String,
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double,
    val batteryPct: Int,
    val statusText: String,
    val timestamp: Long,
    val hopsReplicated: Int,
    val isMyBeacon: Boolean = false
)

data class AntiDosMetrics(
    val spamPacketsRejected: Int = 48,
    val duplicateBroadcastsSuppressed: Int = 19,
    val bufferBytesUsed: Long = 1840000L,
    val bufferCapacityBytes: Long = 52428800L, // 50 MB circular buffer
    val spectralBackoffMs: Long = 180L
)

data class PlatformComplianceStatus(
    val foregroundServiceActive: Boolean = true,
    val serviceType: String = "connectedDevice|dataSync",
    val batteryDrain24h: Float = 3.6f, // percent
    val isDozeModeSimulated: Boolean = false,
    val dutyCycleWindow: String = "12s scan / 90s sleep in Doze"
)

data class MyIdentity(
    val displayName: String = "Atlas-Node-01",
    val nodeId: String = "8f9a...32c1",
    val fingerprint: String = "BANG-ED25519-8F92-C04B-991A",
    val epochId: String = "EPOCH-4819",
    val hardwareSecurity: String = "Android StrongBox KeyStore Active",
    val activeRatchetSessions: Int = 18,
    val dutyCycleMode: String = "Tactical Active (100% Scan)",
    val strangerRelayAllowed: Boolean = true,
    val strangerPacketsForwarded: Int = 1429,
    val bandwidthDonatedBytes: Long = 3840000L,
    val antiDosMetrics: AntiDosMetrics = AntiDosMetrics(),
    val complianceStatus: PlatformComplianceStatus = PlatformComplianceStatus()
)

enum class RelaySimulationRole {
    NODE_A_SENDER,
    NODE_B_STRANGER_RELAY,
    NODE_C_RECIPIENT
}

data class ZeroTrustRelayStep(
    val stepIndex: Int,
    val nodeName: String,
    val role: RelaySimulationRole,
    val isFriend: Boolean,
    val visiblePayload: String,
    val canReadContent: Boolean,
    val knowsOriginalSender: Boolean,
    val knowsFinalRecipient: Boolean,
    val actionTaken: String,
    val tokenSeen: String,
    val transport: TransportType,
    val powVerified: Boolean = true,
    val ackEmitted: Boolean = false
)

/**
 * User Presence & Availability Status Modes
 * Allows automated active/idle switching and manual user configuration.
 */
enum class UserPresenceMode(
    val emoji: String,
    val title: String,
    val description: String,
    val isAutoManaged: Boolean = false
) {
    USING_APP(
        emoji = "🌐",
        title = "Using App",
        description = "Active foreground interaction. Fast message ACKs and live mesh peer discovery.",
        isAutoManaged = true
    ),
    NOT_USING_APP(
        emoji = "⭕",
        title = "Not Using App",
        description = "Idle or running in background. Battery-saving mode with store-and-forward packet relay.",
        isAutoManaged = true
    ),
    DO_NOT_DISTURB(
        emoji = "🔴",
        title = "Do Not Disturb",
        description = "Silence all alerts, broadcast tones, and vibration pulses. High-priority SOS bypasses.",
        isAutoManaged = false
    ),
    SLEEP_MODE(
        emoji = "🌙",
        title = "Sleep Mode",
        description = "Scheduled deep radio sleep. BLE scan intervals throttled to conserve device battery.",
        isAutoManaged = false
    ),
    INVISIBLE_MODE(
        emoji = "🔲",
        title = "Invisible Mode",
        description = "Stealth cloaking. Node ID is hidden from nearby peer radar scans and unverified broadcasts.",
        isAutoManaged = false
    );

    val badge: String get() = "$emoji $title"
}
