package com.example.network

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Intelligent Auto-Gzip Packet Compressor for BLE Mesh Payloads.
 * Compresses string payloads before dispatch over bandwidth-constrained BLE L2CAP MTU channels (~512 bytes).
 */
object PacketCompressor {

    data class CompressionMetrics(
        val originalBytes: Int,
        val compressedBytes: Int,
        val savingsPct: Float,
        val fitsSingleMtu: Boolean
    )

    fun compress(data: String): ByteArray {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).bufferedWriter(Charsets.UTF_8).use { it.write(data) }
        return bos.toByteArray()
    }

    fun decompress(compressedBytes: ByteArray): String {
        return GZIPInputStream(compressedBytes.inputStream()).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    /**
     * Computes compression efficiency for a given payload text.
     */
    fun analyzeCompression(data: String, mtuBytes: Int = 512): CompressionMetrics {
        val original = data.toByteArray(Charsets.UTF_8).size
        val compressed = compress(data).size
        val savings = if (original > 0) ((original - compressed).toFloat() / original.toFloat()) * 100f else 0f
        return CompressionMetrics(
            originalBytes = original,
            compressedBytes = compressed,
            savingsPct = savings.coerceAtLeast(0f),
            fitsSingleMtu = compressed <= mtuBytes
        )
    }
}
