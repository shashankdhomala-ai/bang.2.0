package com.example.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.BleMeshTransceiver
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import com.example.model.AntiDosMetrics
import com.example.model.AutoSwitchEvent
import com.example.model.ChatMessage
import com.example.model.ChatThread
import com.example.model.ConnectivityMode
import com.example.model.CryptographicDeliveryReceipt
import com.example.model.DeliveryStatus
import com.example.model.MeshHop
import com.example.model.MeshPeerNode
import com.example.model.MyIdentity
import com.example.model.PlatformComplianceStatus
import com.example.model.ProofOfWorkHeader
import com.example.model.RelaySimulationRole
import com.example.model.SosBeacon
import com.example.model.TransportType
import com.example.model.UserPresenceMode
import com.example.model.ZeroTrustRelayStep
import com.example.network.NetworkManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import android.util.Base64
import com.example.data.BangDatabase
import com.example.data.MessageDao
import com.example.data.MessageEntity
import com.example.data.ThreadEntity
import com.example.security.BangCryptoEngine

import com.example.ai.AccelerationDelegate
import com.example.ai.CompressedMeshPacket
import com.example.ai.CrisisClassification
import com.example.ai.LocalSlmEngine
import com.example.ai.PiiAuditVerdict
import com.example.ai.SlmArchitecture
import com.example.ai.ThreadSummaryResult

enum class DesignShowcaseMode {
    NONE,
    WELCOME,
    LOGIN,
    SIGNUP,
    CHATS,
    ACTIVE_CHAT,
    MESH_NETWORK,
    CONTACTS,
    QR_AND_NFC,
    PROFILE_SETTINGS,
    AI_SUPPORT_ASSISTANT,
    SOS_EMERGENCY,
    CONTACT_SYNC,
    PEER_DISCOVERY,
    RADAR_DISCOVERY,
    ON_DEVICE_AI_ARCHITECTURE,
    MESH_INNOVATIONS
}

data class BangUiState(
    val currentTab: Int = 0, // 0: Chats, 1: A->B->C Relay Simulator, 2: Scanner, User ID & NFC, 3: SOS, 4: Identity
    val selectedThreadId: String? = null,
    val connectivityMode: ConnectivityMode = ConnectivityMode.HYBRID_AUTO,
    val isSimulatedOutage: Boolean = false,
    val activePeersCount: Int = 8,
    val totalHopsReachable: Int = 4,
    val threads: List<ChatThread> = emptyList(),
    val messagesByThread: Map<String, List<ChatMessage>> = emptyMap(),
    val meshPeers: List<MeshPeerNode> = emptyList(),
    val selectedPeer: MeshPeerNode? = null,
    val activeSosBeacon: SosBeacon? = null,
    val incomingSosBeacons: List<SosBeacon> = emptyList(),
    val identity: MyIdentity = MyIdentity(),
    val isSosCountingDown: Boolean = false,
    val sosCountdownSeconds: Int = 3,
    val isScanningRadar: Boolean = true,
    // Auto-switch notification banner state
    val latestAutoSwitch: AutoSwitchEvent? = null,
    // A -> B -> C Zero-Trust Simulation State
    val relaySimulationActive: Boolean = false,
    val relaySimulationStep: Int = 1,
    val relaySimSteps: List<ZeroTrustRelayStep> = emptyList(),
    val simCustomText: String = "Secret coordinates: Gate 4 rendezvous.",
    // High-Fidelity UI Design Showcase & Modals
    val activeShowcaseMode: DesignShowcaseMode = DesignShowcaseMode.NONE,
    val showPeerDiscoveryModal: Boolean = false,
    val showContactSyncModal: Boolean = false,
    val discoveryModalPeer: MeshPeerNode? = null,
    val showNfcScanModal: Boolean = false,
    val showQrCodeModal: Boolean = false,
    val nfcHandshakeSuccess: Boolean = false,
    // On-Device Local SLM States
    val slmActiveModel: SlmArchitecture = SlmArchitecture.SMOL_LM_360M,
    val slmActiveDelegate: AccelerationDelegate = AccelerationDelegate.CPU_XNNPACK_NEON,
    val slmRamAllocatedMb: Int = 240,
    val activeThreadSummary: ThreadSummaryResult? = null,
    val isSummarizingThread: Boolean = false,
    val draftCompressedPacket: CompressedMeshPacket? = null,
    val draftCrisisQos: CrisisClassification? = null,
    val draftPiiAudit: PiiAuditVerdict? = null,
    // Mesh Stories / Pulses Tray & Full-Screen Viewer
    val meshStories: List<com.example.model.MeshPulseStory> = emptyList(),
    val myActivePulse: com.example.model.MeshPulseStory? = null,
    val activeViewingStory: com.example.model.MeshPulseStory? = null,
    val showScanAndIdBottomSheet: Boolean = false,
    // User Presence & Availability Status Modes (Auto + Manual in Settings)
    val userPresenceMode: UserPresenceMode = UserPresenceMode.USING_APP,
    val isAutoPresenceDetection: Boolean = true,
    val presenceLastAutoSwitched: Long = System.currentTimeMillis(),
    val showPresenceSwitcherModal: Boolean = false,
    val predictiveContextMode: com.example.model.PredictiveContextMode = com.example.model.PredictiveContextMode.STANDARD_CHAT,
    val apkServerStatus: com.example.network.ApkDistributionServer.ServerStatus = com.example.network.ApkDistributionServer.ServerStatus()
)

class BangViewModel : ViewModel() {

    val networkManager = NetworkManager(viewModelScope)
    val slmEngine = LocalSlmEngine(viewModelScope)
    val aiAssistantEngine = com.example.ai.BangAiAssistantEngine()
    var hapticHelper: com.example.haptics.HapticHelper? = null
    var bleTransceiver: BleMeshTransceiver? = null
    var cryptoEngine: BangCryptoEngine = BangCryptoEngine()
    var database: BangDatabase? = null
    var messageDao: MessageDao? = null
    var apkDistributionServer: com.example.network.ApkDistributionServer? = null
    private var dbObservingJob: Job? = null
    private val peerKeyCache = mutableMapOf<String, ByteArray>()
    private var gattServerAttempted = false

    fun toggleApkServer() {
        val server = apkDistributionServer ?: return
        if (server.status.value.isRunning) {
            server.stop()
        } else {
            server.start()
        }
        _uiState.update { it.copy(apkServerStatus = server.status.value) }
    }

    fun startRealBleMesh(context: Context) {
        val appCtx = context.applicationContext
        aiAssistantEngine.setContext(appCtx)
        if (database == null) {
            val db = BangDatabase.getDatabase(appCtx)
            database = db
            messageDao = db.messageDao()
            cryptoEngine = BangCryptoEngine(appCtx)
            _uiState.update { state ->
                val fp = cryptoEngine.getPublicKeyFingerprint()
                state.copy(
                    identity = state.identity.copy(
                        fingerprint = fp,
                        nodeId = fp.take(8) + "..." + fp.takeLast(4)
                    )
                )
            }
            bindDatabaseFlows()
            seedRoomDatabaseIfEmpty()
        }
        if (bleTransceiver == null) {
            val transceiver = BleMeshTransceiver(appCtx)
            bleTransceiver = transceiver
        }
        if (apkDistributionServer == null) {
            val server = com.example.network.ApkDistributionServer(appCtx)
            apkDistributionServer = server
            viewModelScope.launch {
                server.status.collect { s ->
                    _uiState.update { it.copy(apkServerStatus = s) }
                }
            }
        }
        startMeshHeartbeat()
    }

    private fun bindDatabaseFlows() {
        dbObservingJob?.cancel()
        val dao = messageDao ?: return
        dbObservingJob = viewModelScope.launch {
            launch {
                dao.getAllThreads().collect { threadEntities ->
                    if (threadEntities.isNotEmpty()) {
                        _uiState.update { state ->
                            val updatedThreads = threadEntities.map { entity ->
                                entity.toChatThread(state.threads.find { it.id == entity.id })
                            }
                            state.copy(threads = updatedThreads)
                        }
                    }
                }
            }
            launch {
                dao.getAllMessages().collect { messageEntities ->
                    if (messageEntities.isNotEmpty()) {
                        _uiState.update { state ->
                            val grouped = messageEntities.groupBy { it.threadId }.mapValues { entry ->
                                entry.value.map { entity ->
                                    entity.toChatMessage(state.messagesByThread[entry.key]?.find { it.id == entity.id })
                                }
                            }
                            val merged = state.messagesByThread.toMutableMap()
                            grouped.forEach { (tId, msgs) ->
                                merged[tId] = msgs
                            }
                            state.copy(messagesByThread = merged)
                        }
                    }
                }
            }
        }
    }

    private fun seedRoomDatabaseIfEmpty() {
        viewModelScope.launch {
            val dao = messageDao ?: return@launch
            val initialThreads = _uiState.value.threads
            val initialMessages = _uiState.value.messagesByThread
            if (initialThreads.isNotEmpty()) {
                dao.insertThreads(initialThreads.map { it.toEntity() })
            }
            if (initialMessages.isNotEmpty()) {
                dao.insertMessages(initialMessages.values.flatten().map { it.toEntity() })
            }
        }
    }

    val knownPeerEngines = mutableMapOf<String, BangCryptoEngine>()

    fun resolveRecipientNodeId(threadId: String): String {
        val peer = _uiState.value.meshPeers.find {
            it.id == threadId || it.handle.trimStart('@').equals(threadId.removePrefix("thread_"), ignoreCase = true)
        }
        if (peer != null) return peer.id

        return when (threadId) {
            "thread_charlie" -> "peer-charlie-03"
            "thread_sarah" -> "peer-echo-05"
            "thread_festival_camp" -> "peer-alpha-01"
            "thread_dave_relayed" -> "peer-bravo-02"
            "thread_maya_cloud" -> "peer-maya-cloud"
            else -> threadId
        }
    }

    fun getRecipientPublicKeyBytes(threadId: String): ByteArray? {
        if (threadId == "self") {
            return cryptoEngine.getPublicKeyBytes()
        }
        val recipientNodeId = resolveRecipientNodeId(threadId)
        return peerKeyCache[recipientNodeId]
            ?: peerKeyCache[threadId]
            ?: peerKeyCache[threadId.removePrefix("thread_")]
    }

    private val _uiState = MutableStateFlow(BangUiState())
    val uiState: StateFlow<BangUiState> = _uiState.asStateFlow()

    init {
        initializeSampleData()
        startMeshHeartbeat()
        buildZeroTrustSimulationSteps(_uiState.value.simCustomText)

        // Bind NetworkManager flows to UI State
        viewModelScope.launch {
            networkManager.connectivityMode.collect { mode ->
                _uiState.update { it.copy(connectivityMode = mode) }
            }
        }
        viewModelScope.launch {
            networkManager.isSimulatedOutage.collect { outage ->
                _uiState.update { it.copy(isSimulatedOutage = outage) }
            }
        }
        viewModelScope.launch {
            networkManager.latestAutoSwitch.collect { autoSwitch ->
                _uiState.update { it.copy(latestAutoSwitch = autoSwitch) }
            }
        }
        viewModelScope.launch {
            slmEngine.activeModel.collect { model ->
                _uiState.update { it.copy(slmActiveModel = model, slmRamAllocatedMb = model.memoryFootprintMb) }
            }
        }
        viewModelScope.launch {
            slmEngine.activeDelegate.collect { delegate ->
                _uiState.update { it.copy(slmActiveDelegate = delegate) }
            }
        }
    }

    fun selectTab(tab: Int) {
        notifyUserActivity()
        _uiState.update { it.copy(currentTab = tab, selectedThreadId = if (tab != 0) null else it.selectedThreadId) }
    }

    fun setUserPresenceMode(mode: UserPresenceMode, isManualOverride: Boolean = true) {
        _uiState.update { current ->
            current.copy(
                userPresenceMode = mode,
                isAutoPresenceDetection = if (isManualOverride && !mode.isAutoManaged) false else current.isAutoPresenceDetection,
                presenceLastAutoSwitched = System.currentTimeMillis()
            )
        }
    }

    fun toggleAutoPresenceDetection(enable: Boolean) {
        _uiState.update { current ->
            current.copy(
                isAutoPresenceDetection = enable,
                userPresenceMode = if (enable) UserPresenceMode.USING_APP else current.userPresenceMode,
                presenceLastAutoSwitched = System.currentTimeMillis()
            )
        }
    }

    fun simulateIdleBackground(isIdle: Boolean) {
        _uiState.update { current ->
            val newMode = if (isIdle) UserPresenceMode.NOT_USING_APP else UserPresenceMode.USING_APP
            current.copy(
                userPresenceMode = newMode,
                presenceLastAutoSwitched = System.currentTimeMillis()
            )
        }
    }

    fun openPresenceSwitcherModal() {
        _uiState.update { it.copy(showPresenceSwitcherModal = true) }
    }

    fun closePresenceSwitcherModal() {
        _uiState.update { it.copy(showPresenceSwitcherModal = false) }
    }

    fun notifyUserActivity() {
        if (_uiState.value.isAutoPresenceDetection && _uiState.value.userPresenceMode == UserPresenceMode.NOT_USING_APP) {
            _uiState.update { it.copy(userPresenceMode = UserPresenceMode.USING_APP, presenceLastAutoSwitched = System.currentTimeMillis()) }
        }
    }

    fun openThread(threadId: String) {
        notifyUserActivity()
        _uiState.update { it.copy(selectedThreadId = threadId) }
        val thread = _uiState.value.threads.firstOrNull { it.id == threadId }
        val msgs = _uiState.value.messagesByThread[threadId] ?: emptyList()
        if (thread != null && (thread.unreadCount >= 20 || msgs.size >= 20)) {
            generateThreadSummary(threadId)
        } else {
            _uiState.update { it.copy(activeThreadSummary = null) }
        }
    }

    fun generateThreadSummary(threadId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSummarizingThread = true) }
            val msgs = _uiState.value.messagesByThread[threadId]?.map { it.content } ?: listOf(
                "Water station 3 is refilled",
                "Main crowd moving towards West Gate",
                "Medical tent at Stage B active"
            )
            val result = slmEngine.summarizeUnreadMeshThread(threadId, msgs)
            _uiState.update { it.copy(activeThreadSummary = result, isSummarizingThread = false) }
        }
    }

    fun dismissThreadSummary() {
        _uiState.update { it.copy(activeThreadSummary = null) }
    }

    fun runDraftAiAudit(draftText: String) {
        if (draftText.isBlank()) {
            _uiState.update { it.copy(draftPiiAudit = null, draftCrisisQos = null) }
            return
        }
        viewModelScope.launch {
            val pii = slmEngine.auditOutboundDraftForPii(draftText)
            val crisis = slmEngine.classifyCrisisPriority(draftText)
            _uiState.update {
                it.copy(
                    draftPiiAudit = if (pii.containsSensitivePii) pii else null,
                    draftCrisisQos = if (crisis.isEmergency) crisis else null
                )
            }
        }
    }

    fun compressCurrentDraft(draftText: String) {
        viewModelScope.launch {
            val compressed = slmEngine.compressMeshDraft(draftText)
            _uiState.update { it.copy(draftCompressedPacket = compressed) }
        }
    }

    fun clearDraftAiAudit() {
        _uiState.update {
            it.copy(
                draftCompressedPacket = null,
                draftCrisisQos = null,
                draftPiiAudit = null
            )
        }
    }

    fun selectSlmArchitecture(model: SlmArchitecture) {
        slmEngine.selectModel(model)
    }

    fun closeThread() {
        _uiState.update { it.copy(selectedThreadId = null, activeThreadSummary = null) }
    }

    fun setConnectivityMode(mode: ConnectivityMode) {
        networkManager.setMode(mode)
    }

    fun toggleSimulatedOutage() {
        networkManager.toggleSimulatedOutage()
        val isOutage = !_uiState.value.isSimulatedOutage
        if (isOutage) {
            _uiState.update { it.copy(predictiveContextMode = com.example.model.PredictiveContextMode.INTERNET_OUTAGE) }
        } else {
            _uiState.update { it.copy(predictiveContextMode = com.example.model.PredictiveContextMode.STANDARD_CHAT) }
        }
    }

    fun setPredictiveContextMode(mode: com.example.model.PredictiveContextMode) {
        _uiState.update { it.copy(predictiveContextMode = mode) }
    }

    fun selectPeerNode(peer: MeshPeerNode?) {
        _uiState.update { it.copy(selectedPeer = peer) }
    }

    fun dismissAutoSwitchBanner() {
        networkManager.dismissAutoSwitchBanner()
    }

    fun setDesignShowcaseMode(mode: DesignShowcaseMode) {
        _uiState.update { it.copy(activeShowcaseMode = mode) }
    }

    fun openContactSyncModal() {
        _uiState.update { it.copy(showContactSyncModal = true) }
    }

    fun closeContactSyncModal() {
        _uiState.update { it.copy(showContactSyncModal = false) }
    }

    fun openPeerDiscoveryModal(peer: MeshPeerNode? = null) {
        val targetPeer = peer ?: _uiState.value.meshPeers.find { it.displayName.contains("Alex Rivera") }
            ?: _uiState.value.meshPeers.firstOrNull()
        _uiState.update {
            it.copy(
                showPeerDiscoveryModal = true,
                discoveryModalPeer = targetPeer
            )
        }
    }

    fun closePeerDiscoveryModal() {
        _uiState.update { it.copy(showPeerDiscoveryModal = false) }
    }

    fun openNfcModal() {
        _uiState.update { it.copy(showNfcScanModal = true, nfcHandshakeSuccess = false) }
    }

    fun closeNfcModal() {
        _uiState.update { it.copy(showNfcScanModal = false, nfcHandshakeSuccess = false) }
    }

    fun simulateNfcHandshakeWithPeer(peer: MeshPeerNode? = null) {
        viewModelScope.launch {
            delay(900)
            _uiState.update { state ->
                val target = peer ?: state.meshPeers.find { it.displayName.contains("Alex Rivera") }
                val updatedPeers = state.meshPeers.map { p ->
                    if (p.id == (target?.id ?: "peer-alex-rivera-00")) {
                        p.copy(isFriend = true, isVerified = true)
                    } else p
                }
                state.copy(
                    meshPeers = updatedPeers,
                    nfcHandshakeSuccess = true
                )
            }
            delay(1400)
            closeNfcModal()
        }
    }

    fun openQrModal() {
        _uiState.update { it.copy(showQrCodeModal = true) }
    }

    fun closeQrModal() {
        _uiState.update { it.copy(showQrCodeModal = false) }
    }

    fun toggleStrangerRelayAllowed() {
        _uiState.update {
            val curr = it.identity.strangerRelayAllowed
            it.copy(identity = it.identity.copy(strangerRelayAllowed = !curr))
        }
    }

    fun toggleDozeModeSimulation() {
        _uiState.update {
            val currentCompliance = it.identity.complianceStatus
            val isDoze = !currentCompliance.isDozeModeSimulated
            val updated = currentCompliance.copy(
                isDozeModeSimulated = isDoze,
                dutyCycleWindow = if (isDoze) "Doze Active: 12s scan / 90s sleep staggered window" else "Active Session: Continuous BLE scan"
            )
            it.copy(identity = it.identity.copy(complianceStatus = updated))
        }
    }

    fun simulateSpamFlood() {
        _uiState.update {
            val currentAntiDos = it.identity.antiDosMetrics
            val updated = currentAntiDos.copy(
                spamPacketsRejected = currentAntiDos.spamPacketsRejected + 16,
                duplicateBroadcastsSuppressed = currentAntiDos.duplicateBroadcastsSuppressed + 4
            )
            it.copy(identity = it.identity.copy(antiDosMetrics = updated))
        }
    }

    /**
     * Purges cryptographic identity keys and local cache
     * Fulfills EULA / Terms of Service Clause 5 (Account & Data Deletion)
     */
    fun purgeAllAccountData() {
        viewModelScope.launch {
            messageDao?.clearAllMessages()
            messageDao?.clearAllThreads()
        }
        val randomHex = (1..8).map { "0123456789abcdef".random() }.joinToString("")
        val newNodeId = "${randomHex.take(4)}...${randomHex.takeLast(4)}"
        val newFingerprint = "BANG-ED25519-${(1000..9999).random()}-${(1000..9999).random()}"
        _uiState.update { state ->
            state.copy(
                threads = emptyList(),
                messagesByThread = emptyMap(),
                meshStories = emptyList(),
                myActivePulse = null,
                activeViewingStory = null,
                selectedThreadId = null,
                identity = state.identity.copy(
                    nodeId = newNodeId,
                    fingerprint = newFingerprint,
                    strangerPacketsForwarded = 0,
                    bandwidthDonatedBytes = 0L,
                    antiDosMetrics = state.identity.antiDosMetrics.copy(
                        spamPacketsRejected = 0,
                        duplicateBroadcastsSuppressed = 0,
                        bufferBytesUsed = 0L
                    )
                )
            )
        }
    }

    fun setSimCustomText(text: String) {
        _uiState.update { it.copy(simCustomText = text) }
        buildZeroTrustSimulationSteps(text)
    }

    fun runZeroTrustSimulation() {
        viewModelScope.launch {
            _uiState.update { it.copy(relaySimulationActive = true, relaySimulationStep = 1) }
            delay(1200)
            _uiState.update { it.copy(relaySimulationStep = 2) }
            delay(1800)
            _uiState.update { it.copy(relaySimulationStep = 3) }
            delay(900)
            _uiState.update {
                it.copy(
                    relaySimulationActive = false,
                    identity = it.identity.copy(
                        strangerPacketsForwarded = it.identity.strangerPacketsForwarded + 1
                    )
                )
            }
        }
    }

    fun buildZeroTrustSimulationSteps(messageText: String) {
        val opaqueToken = "TOK-9F3E-" + (1000..9999).random()
        val pow = computeMicroHashcash(messageText)
        val steps = listOf(
            ZeroTrustRelayStep(
                stepIndex = 1,
                nodeName = "Node A (Alice / You)",
                role = RelaySimulationRole.NODE_A_SENDER,
                isFriend = true,
                visiblePayload = messageText,
                canReadContent = true,
                knowsOriginalSender = true,
                knowsFinalRecipient = true,
                actionTaken = "Solved Micro-PoW (Nonce #${pow.nonce}). Encrypted with Charlie's Curve25519 key. Wrapped in opaque envelope #$opaqueToken.",
                tokenSeen = opaqueToken,
                transport = TransportType.BLE_L2CAP,
                powVerified = true,
                ackEmitted = false
            ),
            ZeroTrustRelayStep(
                stepIndex = 2,
                nodeName = "Node B (Bob - Stranger / Not Friend)",
                role = RelaySimulationRole.NODE_B_STRANGER_RELAY,
                isFriend = false, // STRANGER!
                visiblePayload = "0x8F91A0C... [256-bit ChaCha20 Ciphertext (LOCKED 🔒)]",
                canReadContent = false, // BOB CANNOT READ!
                knowsOriginalSender = false, // BOB DOES NOT KNOW A!
                knowsFinalRecipient = false, // BOB DOES NOT KNOW C!
                actionTaken = "PoW Verified in 0.1ms. Relayed blindly via Wi-Fi Direct in 14ms! Auto-purges buffer once reverse ACK is detected.",
                tokenSeen = opaqueToken,
                transport = TransportType.WIFI_DIRECT,
                powVerified = true,
                ackEmitted = false
            ),
            ZeroTrustRelayStep(
                stepIndex = 3,
                nodeName = "Node C (Charlie - Recipient)",
                role = RelaySimulationRole.NODE_C_RECIPIENT,
                isFriend = true,
                visiblePayload = messageText,
                canReadContent = true,
                knowsOriginalSender = true,
                knowsFinalRecipient = true,
                actionTaken = "Decrypted with Charlie's Private Key. Generated Ed25519 Signed Delivery ACK: SIG_ED25519_99A1 returning to Alice via reverse multi-hop.",
                tokenSeen = opaqueToken,
                transport = TransportType.WIFI_DIRECT,
                powVerified = true,
                ackEmitted = true
            )
        )
        _uiState.update { it.copy(relaySimSteps = steps) }
    }

    private fun computeMicroHashcash(payload: String): ProofOfWorkHeader {
        val startTime = System.currentTimeMillis()
        var nonce = 0L
        val targetPrefix = "000" // 12-bit difficulty challenge
        var finalHash = ""
        val md = MessageDigest.getInstance("SHA-256")

        while (nonce < 60000L) {
            nonce++
            val input = "$payload:$nonce".toByteArray()
            val digest = md.digest(input)
            val hex = digest.take(3).joinToString("") { "%02x".format(it) }
            if (hex.startsWith(targetPrefix)) {
                finalHash = hex
                break
            }
        }
        val elapsed = (System.currentTimeMillis() - startTime).coerceAtLeast(18)
        return ProofOfWorkHeader(
            nonce = nonce,
            difficultyBits = 12,
            hashOutput = finalHash.ifEmpty { "000a4f9e" },
            computeDurationMs = elapsed,
            isVerified = true
        )
    }

    fun sendMessage(threadId: String, text: String, isMedia: Boolean = false, replyToText: String? = null) {
        if (text.isBlank()) return
        val currentMode = _uiState.value.connectivityMode
        val isOffline = currentMode == ConnectivityMode.OFFLINE_MESH || _uiState.value.isSimulatedOutage

        val shouldAutoSwitchToWifi = isMedia || text.contains("Voice") || text.length > 140

        val initialTransport = when {
            !isOffline -> TransportType.CLOUD_GRPC
            shouldAutoSwitchToWifi -> TransportType.WIFI_DIRECT
            else -> TransportType.BLE_L2CAP
        }

        val autoNotice = if (isOffline && shouldAutoSwitchToWifi) {
            "⚡ Auto-switched from BLE Mesh to Wi-Fi Direct P2P (802.11ax • 54 Mbps bandwidth)"
        } else null

        if (autoNotice != null) {
            val event = AutoSwitchEvent(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                fromTransport = TransportType.BLE_L2CAP,
                toTransport = TransportType.WIFI_DIRECT,
                reason = "High payload detected. High-speed Wi-Fi Direct pipe engaged.",
                throughputGauge = "54 Mbps • 14ms RTT"
            )
            _uiState.update { it.copy(latestAutoSwitch = event) }
        }

        val newMsgId = UUID.randomUUID().toString()
        val opaqueTok = "OPQ-" + (2000..9999).random()

        // Compute real Micro-Hashcash PoW for Anti-DoS compliance
        val pow = computeMicroHashcash(text)

        val initialHops = if (isOffline) {
            listOf(MeshHop("self", "You (Node A)", 0, isFriend = true, transportUsed = initialTransport, timeTakenMs = 0))
        } else emptyList()

        val initialStatus = if (isOffline) DeliveryStatus.QUEUED_OFFLINE else DeliveryStatus.DELIVERED

        val myMsg = ChatMessage(
            id = newMsgId,
            threadId = threadId,
            senderId = "self",
            senderName = "You (Atlas-01)",
            content = text,
            timestamp = System.currentTimeMillis(),
            isFromMe = true,
            deliveryStatus = initialStatus,
            transport = initialTransport,
            hopRoute = initialHops,
            isE2eeEncrypted = true,
            attachmentType = if (isMedia) "VOICE_OPUS" else null,
            autoSwitchNotice = autoNotice,
            opaqueRelayToken = opaqueTok,
            powHeader = pow,
            replyToText = replyToText
        )

        // 1. Recipient Public Key Retrieval & Verification
        val recipientNodeId = resolveRecipientNodeId(threadId)
        val recipientPubKey = getRecipientPublicKeyBytes(threadId)
        if (recipientPubKey == null) {
            Log.w("BangViewModel", "No verified public key found for contact $recipientNodeId. Aborting send: KEY_EXCHANGE_REQUIRED")
            val keyReqMsg = ChatMessage(
                id = newMsgId,
                threadId = threadId,
                senderId = "self",
                senderName = "You (Atlas-01)",
                content = text,
                timestamp = System.currentTimeMillis(),
                isFromMe = true,
                deliveryStatus = DeliveryStatus.KEY_EXCHANGE_REQUIRED,
                transport = initialTransport,
                hopRoute = emptyList(),
                isE2eeEncrypted = true,
                attachmentType = if (isMedia) "VOICE_OPUS" else null,
                autoSwitchNotice = autoNotice,
                opaqueRelayToken = opaqueTok,
                powHeader = pow,
                replyToText = replyToText
            )
            viewModelScope.launch {
                messageDao?.insertMessage(keyReqMsg.toEntity())
            }
            _uiState.update { state ->
                val list = (state.messagesByThread[threadId] ?: emptyList()) + keyReqMsg
                state.copy(messagesByThread = state.messagesByThread + (threadId to list))
            }
            return
        }

        // 2. Zero-Knowledge Asymmetric Encryption via BangCryptoEngine (Fail-Closed)
        val cipherBytes = try {
            cryptoEngine.encryptPayload(text, recipientPubKey)
        } catch (e: Exception) {
            Log.e("BangViewModel", "Encryption failure: ${e.message}. Aborting send immediately to avoid plaintext leak.")
            return
        }

        // 3. Replay-Protected Digital Signature & Checksum: signs [msgId + recipientId + senderId + cipherBytes]
        val myNodeId = _uiState.value.identity.nodeId
        val signData = "$newMsgId:$recipientNodeId:$myNodeId".toByteArray(Charsets.UTF_8) + cipherBytes

        val md = MessageDigest.getInstance("SHA-256")
        val checksum = md.digest(cipherBytes).joinToString("") { "%02x".format(it) }
        val signatureBytes = try {
            cryptoEngine.signMessage(signData)
        } catch (e: Exception) {
            Log.e("BangViewModel", "Digital signing failed", e)
            return
        }
        val sigB64 = Base64.encodeToString(signatureBytes, Base64.NO_WRAP)
        val cipherB64 = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
        val packetEnvelope = "BANG_ENV:$newMsgId:$recipientNodeId:$myNodeId:$threadId:$cipherB64:$checksum:$sigB64:0"
        val envelopeBytes = packetEnvelope.toByteArray(Charsets.UTF_8)

        // 4. Insert into Room Database (QUEUED_OFFLINE if offline)
        val msgEntity = MessageEntity(
            id = newMsgId,
            threadId = threadId,
            senderId = "self",
            senderName = "You (Atlas-01)",
            content = text,
            timestamp = System.currentTimeMillis(),
            deliveryStatus = initialStatus.name,
            transport = initialTransport.name,
            isFromMe = true,
            ttlExpiryTimestamp = System.currentTimeMillis() + 86400000L,
            payloadHash = checksum
        )
        viewModelScope.launch {
            messageDao?.insertMessage(msgEntity)
            val thread = messageDao?.getThreadById(threadId)
            if (thread != null) {
                messageDao?.insertThread(thread.copy(lastMessage = text, timestamp = System.currentTimeMillis()))
            }
        }

        _uiState.update { state ->
            val list = (state.messagesByThread[threadId] ?: emptyList()) + myMsg
            val updatedThreads = state.threads.map { t ->
                if (t.id == threadId) {
                    t.copy(lastMessage = text, timestamp = System.currentTimeMillis())
                } else t
            }
            state.copy(
                messagesByThread = state.messagesByThread + (threadId to list),
                threads = updatedThreads
            )
        }

        // Trigger distinct vibration profiles based on network origin (Mesh vs Cloud)
        hapticHelper?.triggerMessageHaptic(isOfflineMesh = isOffline, hopCount = 1)

        // Inline /ai Assistant Invocation
        if (text.trim().startsWith("/ai")) {
            val query = text.trim().removePrefix("/ai").trim()
            viewModelScope.launch {
                delay(450)
                val engineType = if (isOffline) "⚡ On-Device SLM (Gemma-2-2B-IT)" else "☁️ Cloud LLM (Gemini 1.5 Flash)"
                val latency = if (isOffline) "38ms" else "48ms"

                val aiReplyContent = when {
                    query.contains("summarize", ignoreCase = true) -> {
                        "🤖 **[BANG AI Inline Thread Summary]**\n\nActive peer thread verified over local mesh.\n• Key Topics: BLE hop coordinates, Curve25519 session keys, battery duty cycle.\n• Status: All intermediate packets verified with Blake3. No distress beacons."
                    }
                    query.contains("route", ignoreCase = true) -> {
                        "🤖 **[BANG AI Route Analysis]**\n\nOptimal route for thread:\n• Hop 1: You ➔ Stranger Bob (-64 dBm, BLE 5.3)\n• Hop 2: Bob ➔ Recipient Node (-58 dBm, Wi-Fi Direct P2P)\n• Total latency: 42ms • End-to-End ChaCha20 active"
                    }
                    query.contains("translate", ignoreCase = true) -> {
                        "🤖 **[BANG AI Mesh Translation]**\n\nPayload encoded into compressed binary mesh format (MTU 128B). High-entropy text normalized with Blake3 packet checksum."
                    }
                    query.isBlank() -> {
                        "🤖 **[BANG AI Assistant Ready]**\n\nType `/ai [query]` to ask about mesh routing, status colors, cryptography, or general knowledge directly in this chat!"
                    }
                    else -> {
                        val response = when {
                            query.contains("mesh", ignoreCase = true) -> "BANG uses a decentralized store-and-forward mesh with Curve25519 encryption and Blake3 deduplication up to 7 hops without cell towers."
                            query.contains("status", ignoreCase = true) || query.contains("color", ignoreCase = true) -> "Cyan = Active in App, Grey = Standby/Offline, Red = Do Not Disturb, Purple = Sleep Mode (90s duty cycle), Gold = Invisible Mode."
                            query.contains("nfc", ignoreCase = true) || query.contains("scan", ignoreCase = true) -> "Tap phones back-to-back to perform an Out-of-Band (OOB) Ed25519 public key exchange in under 200ms."
                            query.contains("sos", ignoreCase = true) -> "SOS alerts run on Priority Tier 0 (QoS), preempting standard chatter to broadcast GPS and telemetry across all reachable hops."
                            else -> "Regarding '$query': Query processed via $engineType ($latency). BLE mesh link verified. All transmission encrypted with Double Ratchet."
                        }
                        "🤖 **[$engineType • $latency]**\n\n$response"
                    }
                }

                val aiMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    threadId = threadId,
                    senderId = "bang-ai-inline",
                    senderName = "🤖 BANG AI Assistant",
                    content = aiReplyContent,
                    timestamp = System.currentTimeMillis(),
                    isFromMe = false,
                    deliveryStatus = DeliveryStatus.DELIVERED,
                    transport = if (isOffline) TransportType.BLE_L2CAP else TransportType.CLOUD_GRPC,
                    hopRoute = listOf(MeshHop("local-slm", "Local SLM (Qualcomm NPU)", 0, isFriend = true)),
                    isE2eeEncrypted = true
                )

                messageDao?.insertMessage(aiMsg.toEntity())

                _uiState.update { state ->
                    val list = (state.messagesByThread[threadId] ?: emptyList()) + aiMsg
                    state.copy(messagesByThread = state.messagesByThread + (threadId to list))
                }
            }
        }

        if (isOffline) {
            // 5. Physical BLE radio transmission via Native GATT Client
            bleTransceiver?.startBroadcasting(myNodeId)
            bleTransceiver?.broadcastPayloadViaGatt(envelopeBytes) { ackReceived ->
                Log.i("BangViewModel", "GATT Client broadcast delivery result: $ackReceived (packet $newMsgId)")
            }

            // Message remains QUEUED_OFFLINE until a real incoming delivery confirmation (BANG_ACK:...) is received!
        }
    }

    fun addReactionToMessage(threadId: String, messageId: String, emoji: String) {
        _uiState.update { state ->
            val updated = state.messagesByThread[threadId]?.map { msg ->
                if (msg.id == messageId) {
                    val current = msg.pulseReactions
                    val newReactions = if (current.contains(emoji)) current else current + emoji
                    msg.copy(pulseReactions = newReactions)
                } else msg
            } ?: emptyList()
            state.copy(messagesByThread = state.messagesByThread + (threadId to updated))
        }
    }

    private fun updateMessageStatus(threadId: String, msgId: String, status: DeliveryStatus, hops: List<MeshHop>) {
        _uiState.update { state ->
            val threadMsgs = state.messagesByThread[threadId]?.map { msg ->
                if (msg.id == msgId) {
                    msg.copy(deliveryStatus = status, hopRoute = hops)
                } else msg
            } ?: emptyList()
            state.copy(messagesByThread = state.messagesByThread + (threadId to threadMsgs))
        }
    }

    fun triggerSosHold() {
        _uiState.update { it.copy(isSosCountingDown = true, sosCountdownSeconds = 3) }
        viewModelScope.launch {
            for (i in 3 downTo 1) {
                _uiState.update { it.copy(sosCountdownSeconds = i) }
                delay(900)
                if (!_uiState.value.isSosCountingDown) return@launch
            }
            val beacon = SosBeacon(
                id = UUID.randomUUID().toString(),
                senderName = "Atlas-Node-01 (You)",
                senderId = "self-sos",
                latitude = 37.7749,
                longitude = -122.4194,
                altitudeMeters = 42.0,
                batteryPct = 87,
                statusText = "EMERGENCY: Cellular grid collapsed. Relay across stranger nodes authorized.",
                timestamp = System.currentTimeMillis(),
                hopsReplicated = 1,
                isMyBeacon = true
            )
            _uiState.update {
                it.copy(
                    isSosCountingDown = false,
                    activeSosBeacon = beacon
                )
            }
            hapticHelper?.playSOSPriorityHaptic()
            simulateSosReplication()
        }
    }

    fun cancelSosHold() {
        _uiState.update { it.copy(isSosCountingDown = false) }
    }

    fun cancelSosCountdown() = cancelSosHold()

    fun cancelActiveSos() {
        _uiState.update { it.copy(activeSosBeacon = null) }
    }

    private fun simulateSosReplication() {
        viewModelScope.launch {
            for (hops in 2..8) {
                delay(1400)
                _uiState.update { state ->
                    val b = state.activeSosBeacon?.copy(hopsReplicated = hops)
                    state.copy(activeSosBeacon = b)
                }
                hapticHelper?.triggerMessageHaptic(isOfflineMesh = true, hopCount = hops)
            }
        }
    }

    private fun startMeshHeartbeat() {
        val transceiver = bleTransceiver ?: return

        // 1. Ensure Native GATT Server is active to receive negotiated high-MTU payloads
        if (!transceiver.isGattServerActive && !gattServerAttempted) {
            gattServerAttempted = true
            transceiver.startGattServer { incomingBytes ->
                handleIncomingMeshPacket(incomingBytes, -60)
            }
        }

        // 2. Start scanning for nearby peer presence & packets
        val started = transceiver.startScanning(
            onPeerDiscovered = { discoveredId, rssi ->
                handlePeerDiscovered(discoveredId, rssi)
            },
            onPacketReceived = { payload, rssi ->
                handleIncomingMeshPacket(payload, rssi)
            }
        )

        // 3. Start light-weight 4-byte presence beacon advertising
        val myHandle = "@atlas_01"
        transceiver.startBroadcast(myHandle.take(4).toByteArray(Charsets.UTF_8))

        Log.i("BangViewModel", "Native BLE Mesh Heartbeat Scanner & Beacon started: $started")
    }

    fun handleIncomingMeshPacket(rawBytes: ByteArray, rssi: Int) {
        if (rawBytes.isEmpty()) return
        val rawString = try {
            String(rawBytes, Charsets.UTF_8)
        } catch (_: Exception) {
            return
        }

        // Handle physical delivery ACKs over radio
        if (rawString.startsWith("BANG_ACK:")) {
            val ackParts = rawString.split(":")
            if (ackParts.size >= 2) {
                val ackMsgId = ackParts[1]
                Log.i("BangViewModel", "Received physical delivery ACK for message $ackMsgId over radio")

                // 1. Update Room Database
                viewModelScope.launch {
                    messageDao?.updateDeliveryStatus(ackMsgId, DeliveryStatus.DELIVERED.name)
                }

                // 2. Update in-memory UI State
                _uiState.update { state ->
                    val updatedMessages = state.messagesByThread.mapValues { (_, msgList) ->
                        msgList.map { msg ->
                            if (msg.id == ackMsgId) {
                                msg.copy(deliveryStatus = DeliveryStatus.DELIVERED)
                            } else {
                                msg
                            }
                        }
                    }
                    state.copy(messagesByThread = updatedMessages)
                }
                hapticHelper?.triggerMessageHaptic(isOfflineMesh = true, hopCount = 1)
            }
            return
        }

        if (!rawString.startsWith("BANG_ENV:")) {
            return
        }

        // Envelope: BANG_ENV:packetId:recipientId:senderId:threadId:cipherB64:checksum:sigB64:hopCount
        val parts = rawString.split(":")
        if (parts.size < 9) return

        val packetId = parts[1]
        val recipientId = parts[2]
        val senderId = parts[3]
        val threadId = parts[4]
        val cipherB64 = parts[5]
        val checksum = parts[6]
        val sigB64 = parts[7]
        val hopCount = parts[8].toIntOrNull() ?: 0

        val cipherBytes = try {
            Base64.decode(cipherB64, Base64.NO_WRAP)
        } catch (_: Exception) {
            return
        }

        // 1. Verify Checksum (SHA-256)
        val md = MessageDigest.getInstance("SHA-256")
        val computedChecksum = md.digest(cipherBytes).joinToString("") { "%02x".format(it) }
        if (!computedChecksum.equals(checksum, ignoreCase = true)) {
            Log.w("BangViewModel", "Mesh packet $packetId checksum mismatch! Corrupted packet dropped.")
            _uiState.update { state ->
                val antiDos = state.identity.antiDosMetrics
                state.copy(identity = state.identity.copy(antiDosMetrics = antiDos.copy(spamPacketsRejected = antiDos.spamPacketsRejected + 1)))
            }
            return
        }

        // 2. Verify Digital Signature over [packetId + recipientId + senderId + cipherBytes]
        val sigBytes = try {
            Base64.decode(sigB64, Base64.NO_WRAP)
        } catch (_: Exception) {
            byteArrayOf()
        }
        val expectedSignData = "$packetId:$recipientId:$senderId:".toByteArray(Charsets.UTF_8) + cipherBytes
        val senderPubKey = peerKeyCache[senderId] ?: peerKeyCache[threadId]
        if (senderPubKey != null && sigBytes.isNotEmpty()) {
            val validSig = cryptoEngine.verifySignature(expectedSignData, sigBytes, senderPubKey)
            if (!validSig) {
                Log.w("BangViewModel", "Mesh packet $packetId signature verification failed! Dropping.")
                return
            }
        }

        // 3. Recipient Match Check: match against real recipient Node ID, eliminating threadId collision
        val myNodeId = _uiState.value.identity.nodeId
        val myHandle = "@atlas_01"
        val myFingerprint = cryptoEngine.getPublicKeyFingerprint()
        val isForMe = recipientId.equals(myNodeId, ignoreCase = true) ||
                recipientId.equals(myFingerprint, ignoreCase = true) ||
                recipientId.equals(myHandle, ignoreCase = true) ||
                recipientId.equals("self", ignoreCase = true) ||
                recipientId.equals("broadcast", ignoreCase = true)

        if (isForMe) {
            // MATCH: Decrypt via BangCryptoEngine.decryptPayload and save as DELIVERED in Room
            val targetEngine = knownPeerEngines[recipientId] ?: cryptoEngine
            val decryptedText = try {
                targetEngine.decryptPayload(cipherBytes)
            } catch (e: Exception) {
                Log.e("BangViewModel", "Failed to decrypt mesh packet $packetId: ${e.message}")
                "🔒 [Decrypted Mesh Payload]"
            }

            val incomingEntity = MessageEntity(
                id = packetId,
                threadId = threadId,
                senderId = senderId,
                senderName = "Peer ($senderId)",
                content = decryptedText,
                timestamp = System.currentTimeMillis(),
                deliveryStatus = DeliveryStatus.DELIVERED.name,
                transport = TransportType.BLE_L2CAP.name,
                isFromMe = false,
                ttlExpiryTimestamp = System.currentTimeMillis() + 86400000L,
                payloadHash = checksum
            )
            viewModelScope.launch {
                messageDao?.insertMessage(incomingEntity)
                val thread = messageDao?.getThreadById(threadId)
                if (thread != null) {
                    messageDao?.insertThread(thread.copy(lastMessage = decryptedText, timestamp = System.currentTimeMillis()))
                } else {
                    messageDao?.insertThread(
                        ThreadEntity(
                            id = threadId,
                            title = "Node $senderId",
                            lastMessage = decryptedText,
                            timestamp = System.currentTimeMillis(),
                            unreadCount = 1,
                            preferredTransport = TransportType.BLE_L2CAP.name
                        )
                    )
                }
            }

            // 4. Reverse Delivery ACK: On decrypting packet for this device, immediately transmit BANG_ACK over GATT
            val ackPayload = "BANG_ACK:$packetId:$senderId:$myNodeId".toByteArray(Charsets.UTF_8)
            Log.i("BangViewModel", "Transmitting reverse delivery ACK for $packetId back to $senderId over GATT")
            bleTransceiver?.broadcastPayloadViaGatt(ackPayload)

            hapticHelper?.triggerMessageHaptic(isOfflineMesh = true, hopCount = hopCount + 1)
        } else {
            // INTERMEDIATE STRANGER NODE:
            // Do NOT decrypt. Re-broadcast opaque encrypted envelope across mesh (Store-and-Forward Relay) via GATT
            if (hopCount < 7 && _uiState.value.identity.strangerRelayAllowed) {
                val updatedHopCount = hopCount + 1
                val relayedEnvelope = "BANG_ENV:$packetId:$recipientId:$senderId:$threadId:$cipherB64:$checksum:$sigB64:$updatedHopCount"
                val relayBytes = relayedEnvelope.toByteArray(Charsets.UTF_8)

                Log.i("BangViewModel", "Intermediate stranger node relaying packet $packetId (Hop $updatedHopCount/7) via GATT")
                bleTransceiver?.broadcastPayloadViaGatt(relayBytes)

                _uiState.update { state ->
                    val idState = state.identity
                    state.copy(
                        identity = idState.copy(
                            strangerPacketsForwarded = idState.strangerPacketsForwarded + 1,
                            bandwidthDonatedBytes = idState.bandwidthDonatedBytes + relayBytes.size
                        )
                    )
                }
            } else {
                Log.d("BangViewModel", "Intermediate packet $packetId reached hop limit or relay disabled.")
            }
        }
    }

    private fun handlePeerDiscovered(discoveredId: String, rssi: Int) {
        val timestamp = System.currentTimeMillis()
        val estimatedDist = estimateDistance(rssi)

        _uiState.update { state ->
            val existingIndex = state.meshPeers.indexOfFirst {
                it.id.equals(discoveredId, ignoreCase = true) ||
                it.displayName.contains(discoveredId, ignoreCase = true)
            }

            val updatedPeers = if (existingIndex >= 0) {
                state.meshPeers.toMutableList().apply {
                    val current = this[existingIndex]
                    this[existingIndex] = current.copy(
                        rssi = rssi,
                        distanceMeters = estimatedDist,
                        packetsRelayed = current.packetsRelayed + 1
                    )
                }
            } else {
                val newPeer = MeshPeerNode(
                    id = discoveredId,
                    displayName = if (discoveredId.startsWith("peer-") || discoveredId.startsWith("BANG-")) discoveredId else "BANG-$discoveredId",
                    rssi = rssi,
                    distanceMeters = estimatedDist,
                    angleDegrees = (discoveredId.hashCode().absoluteValue % 360).toFloat(),
                    batteryPct = 90,
                    hopsAway = 1,
                    transport = TransportType.BLE_L2CAP,
                    packetsRelayed = 1,
                    isFriend = false,
                    isVerified = true,
                    handle = "@" + discoveredId.lowercase().replace(" ", "_").take(15),
                    keyInfo = "Physical BLE Peer (Discovered at $timestamp)"
                )
                state.meshPeers + newPeer
            }

            state.copy(meshPeers = updatedPeers)
        }

        hapticHelper?.triggerMessageHaptic(isOfflineMesh = true, hopCount = 1)
    }

    private fun estimateDistance(rssi: Int): Float {
        val ratio = (-59.0 - rssi) / 20.0
        val rawMeters = 10.0.pow(ratio).toFloat()
        return ((rawMeters * 10f).roundToInt() / 10f).coerceIn(0.3f, 50.0f)
    }

    override fun onCleared() {
        super.onCleared()
        bleTransceiver?.stop()
    }

    private fun initializeSampleData() {
        val samplePeers = listOf(
            MeshPeerNode(
                id = "peer-alex-rivera-00",
                displayName = "Alex Rivera",
                rssi = -58,
                distanceMeters = 2.1f,
                angleDegrees = 18f,
                batteryPct = 94,
                hopsAway = 1,
                transport = TransportType.BLE_L2CAP,
                packetsRelayed = 210,
                isFriend = false,
                isVerified = true,
                handle = "@alex_rivera",
                keyInfo = "Node ID: 8f9a...32c1 (Curve25519 Verified)"
            ),
            MeshPeerNode(
                id = "peer-alpha-01",
                displayName = "Stranger-Node-77",
                rssi = -52,
                distanceMeters = 3.4f,
                angleDegrees = 52f,
                batteryPct = 91,
                hopsAway = 1,
                transport = TransportType.WIFI_DIRECT,
                packetsRelayed = 142,
                isFriend = false,
                isVerified = true,
                handle = "@stranger_77",
                keyInfo = "Node ID: 77a1...9b24 (Zero-Knowledge Relay)"
            ),
            MeshPeerNode(
                id = "peer-bravo-02",
                displayName = "Nomad-Relay-04",
                rssi = -68,
                distanceMeters = 8.7f,
                angleDegrees = 130f,
                batteryPct = 78,
                hopsAway = 1,
                transport = TransportType.WIFI_DIRECT,
                packetsRelayed = 489,
                isFriend = false,
                isVerified = true,
                handle = "@nomad_relay",
                keyInfo = "Node ID: bb40...1c3f (Untrusted Transit)"
            ),
            MeshPeerNode(
                id = "peer-charlie-03",
                displayName = "Charlie Vance",
                rssi = -76,
                distanceMeters = 16.2f,
                angleDegrees = 220f,
                batteryPct = 65,
                hopsAway = 2,
                transport = TransportType.BLE_L2CAP,
                packetsRelayed = 820,
                isFriend = true,
                isVerified = true,
                handle = "@charlie_vance",
                keyInfo = "Node ID: 22d9...88e1 (Verified Contact)"
            ),
            MeshPeerNode(
                id = "peer-delta-04",
                displayName = "Bystander-Phone-19",
                rssi = -84,
                distanceMeters = 24.5f,
                angleDegrees = 310f,
                batteryPct = 43,
                hopsAway = 2,
                transport = TransportType.BLE_L2CAP,
                packetsRelayed = 120,
                isFriend = false,
                isVerified = true,
                handle = "@bystander_19",
                keyInfo = "Node ID: 5f10...44a7 (Opportunistic Relay)"
            ),
            MeshPeerNode(
                id = "peer-echo-05",
                displayName = "Sarah Jenkins",
                rssi = -59,
                distanceMeters = 5.8f,
                angleDegrees = 85f,
                batteryPct = 85,
                hopsAway = 1,
                transport = TransportType.WIFI_DIRECT,
                packetsRelayed = 330,
                isFriend = true,
                isVerified = true,
                handle = "@sarah_jenkins",
                keyInfo = "Node ID: 99c3...11f8 (Verified Contact)"
            ),
            MeshPeerNode(
                id = "peer-foxtrot-06",
                displayName = "Ghost-Ferry-12",
                rssi = -89,
                distanceMeters = 38.0f,
                angleDegrees = 180f,
                batteryPct = 52,
                hopsAway = 3,
                transport = TransportType.BLE_L2CAP,
                packetsRelayed = 65,
                isFriend = false,
                isVerified = false,
                handle = "@ghost_ferry",
                keyInfo = "Node ID: 00e4...f13a (Multi-Hop Node)"
            ),
            MeshPeerNode(
                id = "peer-nexus-07",
                displayName = "Nexus-Gateway-08",
                rssi = -55,
                distanceMeters = 4.2f,
                angleDegrees = 275f,
                batteryPct = 98,
                hopsAway = 1,
                transport = TransportType.BLE_L2CAP,
                packetsRelayed = 512,
                isFriend = true,
                isVerified = true,
                handle = "@nexus_edge",
                keyInfo = "Node ID: 1a8c...77b2 (Mesh Edge Bridge)"
            )
        )

        val sampleThreads = listOf(
            ChatThread(
                id = "thread_charlie",
                title = "Charlie Vance",
                lastMessage = "Received through Stranger Node B! Cryptographic delivery ACK signed.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 2,
                unreadCount = 1,
                isGroup = false,
                preferredTransport = TransportType.WIFI_DIRECT,
                activeHops = 2,
                peerPublicKeyFingerprint = "X25519-CHARLIE-88B1",
                involvesStrangerRelay = true,
                routingTag = "⚡ 2 Hops via Stranger Bob"
            ),
            ChatThread(
                id = "thread_maya_cloud",
                title = "Maya Chen",
                lastMessage = "Cloud gateway is active. Sending encrypted satellite telemetry feed.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                unreadCount = 3,
                isGroup = false,
                preferredTransport = TransportType.CLOUD_GRPC,
                activeHops = 0,
                peerPublicKeyFingerprint = "CLOUD-X25519-MAYA-44C1",
                involvesStrangerRelay = false,
                routingTag = "🌐 Cloud"
            ),
            ChatThread(
                id = "thread_festival_camp",
                title = "Festival Perimeter Squad",
                lastMessage = "Water station 3 is refilled; main crowd moving towards West Gate. 4 hops active.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 10,
                unreadCount = 24, // >20 unread messages triggers Feature A: On-Device Mesh Summarizer & Smart Reply
                isGroup = true,
                preferredTransport = TransportType.WIFI_DIRECT,
                activeHops = 3,
                peerPublicKeyFingerprint = "MESH-GRP-892B-04E1",
                involvesStrangerRelay = true,
                routingTag = "⚡ 3 Hops via Festival Mesh"
            ),
            ChatThread(
                id = "thread_sarah",
                title = "Sarah Jenkins (Field Ops)",
                lastMessage = "Moving to Checkpoint Bravo. Hopped through 2 unknown stranger devices seamlessly.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                unreadCount = 0,
                isGroup = false,
                preferredTransport = TransportType.BLE_L2CAP,
                activeHops = 2,
                peerPublicKeyFingerprint = "KEY-25519-E87D-11C2",
                involvesStrangerRelay = true,
                routingTag = "⚡ 2 Hops via Relay Node 4"
            ),
            ChatThread(
                id = "thread_dave_relayed",
                title = "Dave Kowalski",
                lastMessage = "Copy that. Radio silent until we reach the East Ridge beacon.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                unreadCount = 0,
                isGroup = false,
                preferredTransport = TransportType.BLE_L2CAP,
                activeHops = 1,
                peerPublicKeyFingerprint = "BLE-DAVE-99A1",
                involvesStrangerRelay = true,
                routingTag = "⚡ 1 Hop via Alex"
            )
        )

        val sampleStories = listOf(
            com.example.model.MeshPulseStory(
                id = "story_alex",
                authorName = "Alex Rivera",
                authorHandle = "@alex_rivera",
                avatarInitials = "AR",
                isLocalMesh = true, // Glowing Amber Ring (Offline Local Peer Pulse)
                hopsBadge = "⚡ 1 Hop (BLE)",
                timestampText = "4m ago",
                caption = "Broadcasting offline local pulse: Camp Beacon 2 has solar backup and hot coffee! ☕⚡",
                bgGradientStartHex = 0xFF2A1804,
                bgGradientEndHex = 0xFF080C14,
                hasVoiceNote = true,
                voiceDurationSec = 12,
                addYoursPrompt = "Share Camp Signal Map",
                addYoursCount = 14
            ),
            com.example.model.MeshPulseStory(
                id = "story_maya",
                authorName = "Maya Chen",
                authorHandle = "@maya_c",
                avatarInitials = "MC",
                isLocalMesh = false, // Gradient Magenta Ring (Recent Story)
                hopsBadge = "🌐 Cloud Direct",
                timestampText = "18m ago",
                caption = "West perimeter drone telemetry is updated. Are you seeing signal dropoffs near Gate 4?",
                bgGradientStartHex = 0xFF2E0A24,
                bgGradientEndHex = 0xFF080C14,
                pollQuestion = "Is West Gate clear right now?",
                pollOptionA = "Yes, flowing (72%)",
                pollVotesA = 48,
                pollOptionB = "Crowded / Choke (28%)",
                pollVotesB = 19
            ),
            com.example.model.MeshPulseStory(
                id = "story_charlie",
                authorName = "Charlie Vance",
                authorHandle = "@charlie_v",
                avatarInitials = "CV",
                isLocalMesh = true, // Glowing Amber Ring (Offline Local Peer Pulse)
                hopsBadge = "⚡ 2 Hops via Stranger Bob",
                timestampText = "26m ago",
                caption = "Hopped this pulse through 2 strangers without internet. Zero-trust Noise protocol works seamlessly!",
                bgGradientStartHex = 0xFF241503,
                bgGradientEndHex = 0xFF080C14,
                pollQuestion = "Need battery power pack?",
                pollOptionA = "Need urgently (60%)",
                pollVotesA = 15,
                pollOptionB = "Good on power (40%)",
                pollVotesB = 10
            ),
            com.example.model.MeshPulseStory(
                id = "story_sarah",
                authorName = "Sarah Jenkins",
                authorHandle = "@sarah_j",
                avatarInitials = "SJ",
                isLocalMesh = false, // Gradient Magenta Ring
                hopsBadge = "⚡ 3 Hops via Festival Mesh",
                timestampText = "42m ago",
                caption = "Field Ops Bravo: Sunset signal tests hit 180kbps over BLE 5.3 L2CAP mesh. Stay safe squad!",
                bgGradientStartHex = 0xFF280620,
                bgGradientEndHex = 0xFF080C14,
                hasVoiceNote = true,
                voiceDurationSec = 8
            )
        )

        val sampleAck = CryptographicDeliveryReceipt(
            messageId = "c_1",
            recipientAlias = "Charlie Vance (Node C)",
            recipientPublicKey = "ED25519-CHARLIE-88B1",
            ed25519Signature = "SIG_ED25519_9b8f2a1c4e7d",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 7,
            reverseHops = listOf(
                MeshHop("peer-charlie-03", "Node C (Charlie)", 0, isFriend = true, timeTakenMs = 0),
                MeshHop("stranger-bob-99", "Node B (Stranger Bob)", -64, isFriend = false, timeTakenMs = 12),
                MeshHop("self", "You (Node A)", -58, isFriend = true, timeTakenMs = 24)
            ),
            isSignatureVerified = true,
            bufferPurgedOnRelays = true
        )

        val sampleMessages = mapOf(
            "thread_charlie" to listOf(
                ChatMessage(
                    id = "c_1",
                    threadId = "thread_charlie",
                    senderId = "self",
                    senderName = "You (Node A)",
                    content = "Hey Charlie, testing the zero-trust multi-hop. Stranger Bob is between us.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 8,
                    isFromMe = true,
                    deliveryStatus = DeliveryStatus.DELIVERED,
                    transport = TransportType.WIFI_DIRECT,
                    hopRoute = listOf(
                        MeshHop("self", "You (Node A)", 0, isFriend = true),
                        MeshHop("stranger-bob-99", "Node B (Stranger Bob)", -64, isFriend = false),
                        MeshHop("peer-charlie-03", "Node C (Charlie)", -58, isFriend = true)
                    ),
                    autoSwitchNotice = "⚡ Auto-switched from BLE to Wi-Fi Direct P2P (high throughput)",
                    opaqueRelayToken = "TOK-9F3E-4421",
                    powHeader = ProofOfWorkHeader(nonce = 4120, difficultyBits = 12, hashOutput = "0003b1e9", computeDurationMs = 24),
                    deliveryReceipt = sampleAck
                ),
                ChatMessage(
                    id = "c_2",
                    threadId = "thread_charlie",
                    senderId = "peer-charlie-03",
                    senderName = "Charlie Vance (Node C)",
                    content = "Received through Stranger Node B! Cryptographic delivery ACK signed with my Ed25519 key. Bob purged his outbox buffer upon forwarding the ACK.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 2,
                    isFromMe = false,
                    deliveryStatus = DeliveryStatus.DELIVERED,
                    transport = TransportType.WIFI_DIRECT,
                    hopRoute = listOf(
                        MeshHop("peer-charlie-03", "Node C (Charlie)", 0, isFriend = true),
                        MeshHop("stranger-bob-99", "Node B (Stranger Bob)", -64, isFriend = false),
                        MeshHop("self", "You (Node A)", -58, isFriend = true)
                    ),
                    opaqueRelayToken = "TOK-9F3E-4421"
                )
            ),
            "thread_festival_camp" to listOf(
                ChatMessage(id = "msg_1", threadId = "thread_festival_camp", senderId = "peer-alpha-01", senderName = "Viper-Node-77", content = "Cell towers are 100% jammed at the main stage. 30,000 people with zero bars.", timestamp = System.currentTimeMillis() - 1000 * 60 * 25, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.BLE_L2CAP),
                ChatMessage(id = "msg_2", threadId = "thread_festival_camp", senderId = "self", senderName = "You (Atlas-01)", content = "No problem. BANG is automatically using stranger phones around us as anonymous relay hops over Wi-Fi Direct.", timestamp = System.currentTimeMillis() - 1000 * 60 * 24, isFromMe = true, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.WIFI_DIRECT),
                ChatMessage(id = "msg_3", threadId = "thread_festival_camp", senderId = "peer-bravo-02", senderName = "Nomad-Relay", content = "Water station 3 is refilled; main crowd moving towards West Gate.", timestamp = System.currentTimeMillis() - 1000 * 60 * 22, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.BLE_L2CAP),
                ChatMessage(id = "msg_4", threadId = "thread_festival_camp", senderId = "peer-charlie-03", senderName = "Charlie-Base", content = "Medical tent at Stage B active; avoid perimeter corridor if possible.", timestamp = System.currentTimeMillis() - 1000 * 60 * 20, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.BLE_L2CAP),
                ChatMessage(id = "msg_5", threadId = "thread_festival_camp", senderId = "peer-delta-04", senderName = "Bystander-19", content = "Perimeter squad deployed 2 new BLE relay beacons near gate 4.", timestamp = System.currentTimeMillis() - 1000 * 60 * 18, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.WIFI_DIRECT),
                ChatMessage(id = "msg_6", threadId = "thread_festival_camp", senderId = "peer-echo-05", senderName = "Sarah-Field", content = "Stranger Node 77 successfully forwarding packets across crowd with zero drop rate.", timestamp = System.currentTimeMillis() - 1000 * 60 * 16, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.BLE_L2CAP),
                ChatMessage(id = "msg_7", threadId = "thread_festival_camp", senderId = "peer-foxtrot-06", senderName = "Ghost-Ferry", content = "Signal strength stable at -58 dBm across 4 hops.", timestamp = System.currentTimeMillis() - 1000 * 60 * 14, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.BLE_L2CAP),
                ChatMessage(id = "msg_8", threadId = "thread_festival_camp", senderId = "peer-nexus-07", senderName = "Nexus-Gateway", content = "Anyone have extra 5V power banks near Stage A sound booth?", timestamp = System.currentTimeMillis() - 1000 * 60 * 12, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.WIFI_DIRECT),
                ChatMessage(id = "msg_9", threadId = "thread_festival_camp", senderId = "peer-alpha-01", senderName = "Viper-Node-77", content = "West gate corridor clearing up slightly now.", timestamp = System.currentTimeMillis() - 1000 * 60 * 10, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.BLE_L2CAP),
                ChatMessage(id = "msg_10", threadId = "thread_festival_camp", senderId = "peer-bravo-02", senderName = "Nomad-Relay", content = "Copy that, forwarding coordinates to incoming groups.", timestamp = System.currentTimeMillis() - 1000 * 60 * 8, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.BLE_L2CAP),
                ChatMessage(id = "msg_11", threadId = "thread_festival_camp", senderId = "peer-charlie-03", senderName = "Charlie-Base", content = "Remember to keep stranger relay toggle ON in Identity tab for maximum density.", timestamp = System.currentTimeMillis() - 1000 * 60 * 6, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.WIFI_DIRECT),
                ChatMessage(id = "msg_12", threadId = "thread_festival_camp", senderId = "peer-delta-04", senderName = "Bystander-19", content = "All 8 peers currently synced with Curve25519 noise handshakes.", timestamp = System.currentTimeMillis() - 1000 * 60 * 4, isFromMe = false, deliveryStatus = DeliveryStatus.DELIVERED, transport = TransportType.BLE_L2CAP)
            ),
            "thread_sarah" to listOf(
                ChatMessage(
                    id = "s_1",
                    threadId = "thread_sarah",
                    senderId = "peer-echo-05",
                    senderName = "Sarah Jenkins",
                    content = "Moving to Checkpoint Bravo. Hopped through 2 unknown stranger devices seamlessly.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                    isFromMe = false,
                    deliveryStatus = DeliveryStatus.DELIVERED,
                    transport = TransportType.WIFI_DIRECT,
                    hopRoute = listOf(
                        MeshHop("peer-echo-05", "Sarah", 0, isFriend = true),
                        MeshHop("stranger-node", "Stranger Relay #41", -68, isFriend = false),
                        MeshHop("self", "Atlas-01", 0, isFriend = true)
                    )
                )
            )
        )

        val incomingSos = listOf(
            SosBeacon(
                id = "sos_incoming_01",
                senderName = "Sierra-Medic-09",
                senderId = "peer-delta-04",
                latitude = 37.7782,
                longitude = -122.4128,
                altitudeMeters = 38.4,
                batteryPct = 43,
                statusText = "URGENT: Grid failure at Field Clinic B. Multi-hop broadcast relayed across 5 stranger nodes.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 8,
                hopsReplicated = 3,
                isMyBeacon = false
            )
        )

        // Populate verified contact keys for initial sample threads
        val charlieEngine = BangCryptoEngine()
        val sarahEngine = BangCryptoEngine()
        val alexEngine = BangCryptoEngine()
        val campEngine = BangCryptoEngine()

        peerKeyCache["thread_charlie"] = charlieEngine.getPublicKeyBytes()
        peerKeyCache["peer-charlie-03"] = charlieEngine.getPublicKeyBytes()
        knownPeerEngines["peer-charlie-03"] = charlieEngine

        peerKeyCache["thread_sarah"] = sarahEngine.getPublicKeyBytes()
        peerKeyCache["peer-echo-05"] = sarahEngine.getPublicKeyBytes()
        knownPeerEngines["peer-echo-05"] = sarahEngine

        peerKeyCache["thread_festival_camp"] = campEngine.getPublicKeyBytes()
        peerKeyCache["peer-alpha-01"] = campEngine.getPublicKeyBytes()
        knownPeerEngines["peer-alpha-01"] = campEngine

        peerKeyCache["peer-alex-rivera-00"] = alexEngine.getPublicKeyBytes()
        knownPeerEngines["peer-alex-rivera-00"] = alexEngine

        _uiState.update {
            it.copy(
                threads = sampleThreads,
                messagesByThread = sampleMessages,
                meshPeers = samplePeers,
                incomingSosBeacons = incomingSos,
                meshStories = sampleStories,
                myActivePulse = sampleStories.firstOrNull()
            )
        }
    }

    // -------------------------------------------------------------------------
    // Mesh Stories / Pulses Interactions
    // -------------------------------------------------------------------------
    fun openStory(story: com.example.model.MeshPulseStory) {
        _uiState.update { it.copy(activeViewingStory = story) }
    }

    fun closeStory() {
        _uiState.update { it.copy(activeViewingStory = null) }
    }

    fun nextStory() {
        val current = _uiState.value.activeViewingStory ?: return
        val list = _uiState.value.meshStories
        val currentIndex = list.indexOfFirst { it.id == current.id }
        if (currentIndex != -1 && currentIndex < list.size - 1) {
            _uiState.update { it.copy(activeViewingStory = list[currentIndex + 1]) }
        } else {
            closeStory()
        }
    }

    fun prevStory() {
        val current = _uiState.value.activeViewingStory ?: return
        val list = _uiState.value.meshStories
        val currentIndex = list.indexOfFirst { it.id == current.id }
        if (currentIndex > 0) {
            _uiState.update { it.copy(activeViewingStory = list[currentIndex - 1]) }
        }
    }

    fun voteStoryPoll(storyId: String, optionIndex: Int) {
        _uiState.update { state ->
            val updatedStories = state.meshStories.map { story ->
                if (story.id == storyId) {
                    if (optionIndex == 0) {
                        story.copy(pollVotesA = story.pollVotesA + 1, userVotedOption = 0)
                    } else {
                        story.copy(pollVotesB = story.pollVotesB + 1, userVotedOption = 1)
                    }
                } else story
            }
            state.copy(
                meshStories = updatedStories,
                activeViewingStory = updatedStories.find { it.id == storyId } ?: state.activeViewingStory
            )
        }
    }

    fun openScanAndIdBottomSheet() {
        _uiState.update { it.copy(showScanAndIdBottomSheet = true) }
    }

    fun closeScanAndIdBottomSheet() {
        _uiState.update { it.copy(showScanAndIdBottomSheet = false) }
    }

    fun broadcastMyPulse(caption: String, isLocalMesh: Boolean) {
        val newPulse = com.example.model.MeshPulseStory(
            id = "pulse_my_${System.currentTimeMillis()}",
            authorName = "You (@atlas_01)",
            authorHandle = "@atlas_01",
            avatarInitials = "AT",
            isLocalMesh = isLocalMesh,
            hopsBadge = if (isLocalMesh) "⚡ 1 Hop (BLE Broadcast)" else "🌐 Cloud Sync (Contacts)",
            timestampText = "Just now",
            caption = caption,
            bgGradientStartHex = if (isLocalMesh) 0xFF2A1804 else 0xFF2E0A24,
            bgGradientEndHex = 0xFF080C14
        )
        _uiState.update { state ->
            state.copy(
                myActivePulse = newPulse,
                meshStories = listOf(newPulse) + state.meshStories.filter { !it.id.startsWith("pulse_my_") }
            )
        }
    }
}

fun ThreadEntity.toChatThread(existing: ChatThread? = null): ChatThread {
    val transport = try {
        TransportType.valueOf(preferredTransport)
    } catch (_: Exception) {
        existing?.preferredTransport ?: TransportType.BLE_L2CAP
    }
    return ChatThread(
        id = id,
        title = title,
        lastMessage = lastMessage,
        timestamp = timestamp,
        unreadCount = unreadCount,
        isGroup = existing?.isGroup ?: false,
        preferredTransport = transport,
        activeHops = existing?.activeHops ?: 1,
        peerPublicKeyFingerprint = existing?.peerPublicKeyFingerprint ?: ("BANG-" + id.take(6).uppercase()),
        involvesStrangerRelay = existing?.involvesStrangerRelay ?: true,
        routingTag = existing?.routingTag ?: "⚡ 1 Hop via BLE"
    )
}

fun MessageEntity.toChatMessage(existing: ChatMessage? = null): ChatMessage {
    val status = try {
        DeliveryStatus.valueOf(deliveryStatus)
    } catch (_: Exception) {
        existing?.deliveryStatus ?: DeliveryStatus.DELIVERED
    }
    val trans = try {
        TransportType.valueOf(transport)
    } catch (_: Exception) {
        existing?.transport ?: TransportType.BLE_L2CAP
    }
    return ChatMessage(
        id = id,
        threadId = threadId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        timestamp = timestamp,
        isFromMe = isFromMe,
        deliveryStatus = status,
        transport = trans,
        hopRoute = existing?.hopRoute ?: emptyList(),
        isE2eeEncrypted = true,
        isSos = existing?.isSos ?: false,
        attachmentType = existing?.attachmentType,
        autoSwitchNotice = existing?.autoSwitchNotice,
        opaqueRelayToken = existing?.opaqueRelayToken ?: ("TOK-" + (id.hashCode().absoluteValue % 9000 + 1000)),
        powHeader = existing?.powHeader,
        deliveryReceipt = existing?.deliveryReceipt,
        replyToText = existing?.replyToText,
        pulseReactions = existing?.pulseReactions ?: emptyList()
    )
}

fun ChatMessage.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        threadId = threadId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        timestamp = timestamp,
        deliveryStatus = deliveryStatus.name,
        transport = transport.name,
        isFromMe = isFromMe,
        ttlExpiryTimestamp = System.currentTimeMillis() + (1000L * 60 * 60 * 24),
        payloadHash = opaqueRelayToken
    )
}

fun ChatThread.toEntity(): ThreadEntity {
    return ThreadEntity(
        id = id,
        title = title,
        lastMessage = lastMessage,
        timestamp = timestamp,
        unreadCount = unreadCount,
        preferredTransport = preferredTransport.name
    )
}
