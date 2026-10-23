package com.example.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission

/**
 * Haptic Engine distinguishing offline BLE Mesh packet hops from Cloud messages
 * and High-Priority SOS Emergency Broadcasts.
 */
class HapticHelper(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Triggers distinct vibration profiles based on network origin.
     */
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun triggerMessageHaptic(isOfflineMesh: Boolean, hopCount: Int = 1) {
        val deviceVibrator = vibrator ?: return
        try {
            if (!deviceVibrator.hasVibrator()) return

            if (isOfflineMesh) {
                playMeshPacketHaptic(deviceVibrator, hopCount)
            } else {
                playCloudMessageHaptic(deviceVibrator)
            }
        } catch (e: Exception) {
            // Graceful fallback on restricted environments
        }
    }

    /**
     * ⚡ BLE Mesh Packet Pattern: Double-click pulse mimicking an offline node hop.
     */
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun playMeshPacketHaptic(vibrator: Vibrator? = this.vibrator, hopCount: Int = 1) {
        val deviceVibrator = vibrator ?: this.vibrator ?: return
        try {
            if (!deviceVibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                deviceVibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_CLICK, VibrationEffect.Composition.PRIMITIVE_TICK)
            ) {
                // Modern Haptic Composition (Android 11+)
                val composition = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.8f) // Sharp initial packet arrival
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.5f, 60) // Subtle trail echo

                // Add an extra tick if the message took multi-hops
                if (hopCount > 2) {
                    composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.3f, 40)
                }

                deviceVibrator.vibrate(composition.compose())
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Waveform Fallback (Android 8.0+)
                // Off 0ms, On 30ms, Off 50ms, On 20ms
                val timings = longArrayOf(0, 30, 50, 20)
                val amplitudes = intArrayOf(0, 200, 0, 100)
                deviceVibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                // Legacy Fallback
                @Suppress("DEPRECATION")
                deviceVibrator.vibrate(longArrayOf(0, 30, 50, 20), -1)
            }
        } catch (e: Exception) {
            // Guard
        }
    }

    /**
     * 🌐 Cloud Message Pattern: Smooth single rise/pulse.
     */
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun playCloudMessageHaptic(vibrator: Vibrator? = this.vibrator) {
        val deviceVibrator = vibrator ?: this.vibrator ?: return
        try {
            if (!deviceVibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                deviceVibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE)
            ) {
                val composition = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE, 0.6f)
                deviceVibrator.vibrate(composition.compose())
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceVibrator.vibrate(VibrationEffect.createOneShot(40, 120)) // Single medium bump
            } else {
                @Suppress("DEPRECATION")
                deviceVibrator.vibrate(40)
            }
        } catch (e: Exception) {
            // Guard
        }
    }

    /**
     * 🆘 SOS Emergency Broadcast Haptic Pattern: High-intensity urgent triple-staccato.
     */
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun playSOSPriorityHaptic() {
        val deviceVibrator = vibrator ?: return
        try {
            if (!deviceVibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 100, 50, 100, 50, 200)
                val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
                deviceVibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                deviceVibrator.vibrate(longArrayOf(0, 100, 50, 100, 50, 200), -1)
            }
        } catch (e: Exception) {
            // Guard
        }
    }
}
