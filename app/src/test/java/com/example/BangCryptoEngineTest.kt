package com.example

import com.example.security.BangCryptoEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BangCryptoEngineTest {

    @Test
    fun testKeyGenerationAndFingerprint() {
        val alice = BangCryptoEngine()
        assertNotNull(alice.publicKey)
        assertNotNull(alice.privateKey)

        val fingerprint = alice.getPublicKeyFingerprint()
        assertTrue("Fingerprint should be formatted with colons", fingerprint.contains(":"))
        assertTrue("Fingerprint length should be consistent", fingerprint.length > 20)
    }

    @Test
    fun testZeroKnowledgeStoreAndForwardEncryption() {
        val alice = BangCryptoEngine()
        val bob = BangCryptoEngine()
        val eveRelay = BangCryptoEngine()

        val secretMessage = "BANG: Off-grid zero-knowledge mesh payload #4291"

        // Alice encrypts for Bob
        val cipherBytes = alice.encryptPayload(secretMessage, bob.getPublicKeyBytes())
        assertTrue("Ciphertext should contain headers and payload", cipherBytes.size > secretMessage.length)

        // Bob decrypts with his private key
        val decrypted = bob.decryptPayload(cipherBytes, alice.getPublicKeyBytes())
        assertEquals(secretMessage, decrypted)

        // Eve (relay node) attempts to decrypt without Bob's private key -> Must fail
        var eveDecryptionFailed = false
        try {
            eveRelay.decryptPayload(cipherBytes, alice.getPublicKeyBytes())
        } catch (_: Exception) {
            eveDecryptionFailed = true
        }
        assertTrue("Intermediate relay nodes must not be able to decrypt payloads", eveDecryptionFailed)
    }

    @Test
    fun testMessageSignatureAndVerification() {
        val alice = BangCryptoEngine()
        val payload = "Hardware node beacon telemetry: GPS 37.7749, -122.4194".toByteArray(Charsets.UTF_8)

        val signature = alice.signMessage(payload)
        assertTrue("Signature should be non-empty", signature.isNotEmpty())

        // Verify with Alice's public key
        val isValid = alice.verifySignature(payload, signature, alice.getPublicKeyBytes())
        assertTrue("Signature must be valid for unaltered data", isValid)

        // Verify with tampered data
        val tamperedPayload = "Hardware node beacon telemetry: GPS 0.0000, 0.0000".toByteArray(Charsets.UTF_8)
        val isTamperedValid = alice.verifySignature(tamperedPayload, signature, alice.getPublicKeyBytes())
        assertFalse("Signature must fail for tampered data", isTamperedValid)
    }
}
