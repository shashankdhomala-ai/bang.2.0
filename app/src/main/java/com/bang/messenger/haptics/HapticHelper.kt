package com.bang.messenger.haptics

import android.content.Context
import androidx.annotation.RequiresPermission
import com.example.haptics.HapticHelper as BaseHapticHelper

/**
 * Mirror class under com.bang.messenger.haptics matching the specification.
 */
class HapticHelper(context: Context) {
    private val delegate = BaseHapticHelper(context)

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun triggerMessageHaptic(isOfflineMesh: Boolean, hopCount: Int = 1) {
        delegate.triggerMessageHaptic(isOfflineMesh, hopCount)
    }

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun playSOSPriorityHaptic() {
        delegate.playSOSPriorityHaptic()
    }
}
