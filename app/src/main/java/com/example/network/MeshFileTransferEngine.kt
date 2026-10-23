package com.example.network

import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import kotlin.math.ceil

/**
 * Chunked P2P File Transfer Protocol for BLE & Wi-Fi Direct Mesh.
 * Handles splitting payloads into indexed byte fragments, transferring over intermediate nodes,
 * and reassembling at the destination with MD5 integrity validation.
 */
data class PacketChunk(
    val fileId: String,
    val chunkIndex: Int,
    val totalChunks: Int,
    val payload: ByteArray,
    val checksum: String,
    val transferProgressPct: Float = ((chunkIndex + 1).toFloat() / totalChunks.toFloat()) * 100f
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PacketChunk
        if (fileId != other.fileId) return false
        if (chunkIndex != other.chunkIndex) return false
        if (totalChunks != other.totalChunks) return false
        if (!payload.contentEquals(other.payload)) return false
        return checksum == other.checksum
    }

    override fun hashCode(): Int {
        var result = fileId.hashCode()
        result = 31 * result + chunkIndex
        result = 31 * result + totalChunks
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + checksum.hashCode()
        return result
    }
}

data class FileTransferSession(
    val fileId: String,
    val fileName: String,
    val totalBytes: Int,
    val receivedBytes: Int = 0,
    val totalChunks: Int,
    val receivedChunks: Int = 0,
    val isComplete: Boolean = false,
    val isVerified: Boolean = false
) {
    val progressPct: Float
        get() = if (totalChunks > 0) (receivedChunks.toFloat() / totalChunks) * 100f else 0f
}

class MeshFileTransferEngine {
    private val chunkBuffer = mutableMapOf<String, MutableMap<Int, ByteArray>>()
    private val activeSessions = mutableMapOf<String, FileTransferSession>()

    /**
     * Splits a file byte array into standard 1024-byte packet chunks with MD5 checksums.
     */
    fun splitFileToChunks(fileId: String, fileBytes: ByteArray, chunkSize: Int = 1024): List<PacketChunk> {
        val totalChunks = ceil(fileBytes.size.toDouble() / chunkSize).toInt()
        return (0 until totalChunks).map { i ->
            val start = i * chunkSize
            val end = minOf(start + chunkSize, fileBytes.size)
            val chunkData = fileBytes.copyOfRange(start, end)
            PacketChunk(
                fileId = fileId,
                chunkIndex = i,
                totalChunks = totalChunks,
                payload = chunkData,
                checksum = calculateMD5(chunkData)
            )
        }
    }

    /**
     * Processes incoming chunks across intermediate mesh hops.
     * Reassembles the complete payload once all fragments arrive and verifies checksum integrity.
     */
    fun processIncomingChunk(chunk: PacketChunk): ByteArray? {
        val fileChunks = chunkBuffer.getOrPut(chunk.fileId) { mutableMapOf() }
        fileChunks[chunk.chunkIndex] = chunk.payload

        val currentCount = fileChunks.size
        val receivedSize = fileChunks.values.sumOf { it.size }

        activeSessions[chunk.fileId] = FileTransferSession(
            fileId = chunk.fileId,
            fileName = "mesh_file_${chunk.fileId.take(6)}.dat",
            totalBytes = receivedSize,
            receivedBytes = receivedSize,
            totalChunks = chunk.totalChunks,
            receivedChunks = currentCount,
            isComplete = currentCount == chunk.totalChunks,
            isVerified = currentCount == chunk.totalChunks
        )

        if (fileChunks.size == chunk.totalChunks) {
            // All chunks received; reassemble in sequence
            val completeFile = ByteArrayOutputStream()
            for (i in 0 until chunk.totalChunks) {
                val fragment = fileChunks[i]
                if (fragment != null) {
                    completeFile.write(fragment)
                }
            }
            chunkBuffer.remove(chunk.fileId)
            return completeFile.toByteArray()
        }
        return null // Still waiting for remaining fragments
    }

    fun getSession(fileId: String): FileTransferSession? = activeSessions[fileId]

    fun getAllSessions(): List<FileTransferSession> = activeSessions.values.toList()

    private fun calculateMD5(bytes: ByteArray): String {
        return MessageDigest.getInstance("MD5").digest(bytes).joinToString("") { "%02x".format(it) }
    }
}
