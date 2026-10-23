package com.example.network

import android.content.Context
import android.os.BatteryManager

/**
 * Adaptive Battery Saver & Hop Throttling Governor.
 * Dynamically throttles BLE scanning intervals, packet relaying, and maximum hop TTL
 * based on device battery capacity and charging states.
 */
class MeshBatteryGovernor(private val context: Context) {

    enum class MeshPowerProfile(
        val displayName: String,
        val scanIntervalMs: Long,
        val allowRelay: Boolean,
        val maxHops: Int,
        val dutyCycleDescription: String
    ) {
        HIGH_PERFORMANCE(
            displayName = "⚡ High Performance Mesh",
            scanIntervalMs = 1000L,
            allowRelay = true,
            maxHops = 7,
            dutyCycleDescription = "Continuous BLE 5.3 scan (1.0s interval) • Full stranger packet forwarding enabled"
        ),
        BALANCED(
            displayName = "⚖️ Balanced Adaptive",
            scanIntervalMs = 5000L,
            allowRelay = true,
            maxHops = 4,
            dutyCycleDescription = "Moderate 5.0s scan window • Staggered 4-hop relay to conserve power"
        ),
        BATTERY_SAVER(
            displayName = "🔋 Ultra Battery Saver",
            scanIntervalMs = 15000L,
            allowRelay = false,
            maxHops = 1,
            dutyCycleDescription = "15.0s duty cycle • Stranger relaying paused • Direct 1-hop reception only"
        )
    }

    data class BatteryGovernorMetrics(
        val batteryPct: Int,
        val isCharging: Boolean,
        val currentProfile: MeshPowerProfile,
        val scanIntervalMs: Long,
        val allowRelaying: Boolean,
        val maxTtlHops: Int
    )

    fun getRecommendedProfile(): MeshPowerProfile {
        return try {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            if (batteryManager != null) {
                val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                val isCharging = batteryManager.isCharging

                when {
                    isCharging || level > 50 -> MeshPowerProfile.HIGH_PERFORMANCE
                    level in 20..50 -> MeshPowerProfile.BALANCED
                    else -> MeshPowerProfile.BATTERY_SAVER
                }
            } else {
                MeshPowerProfile.BALANCED
            }
        } catch (e: Exception) {
            MeshPowerProfile.BALANCED
        }
    }

    fun getGovernorMetrics(): BatteryGovernorMetrics {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val level = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 82
        val isCharging = batteryManager?.isCharging ?: false
        val profile = getRecommendedProfile()

        return BatteryGovernorMetrics(
            batteryPct = if (level in 0..100) level else 82,
            isCharging = isCharging,
            currentProfile = profile,
            scanIntervalMs = profile.scanIntervalMs,
            allowRelaying = profile.allowRelay,
            maxTtlHops = profile.maxHops
        )
    }
}
