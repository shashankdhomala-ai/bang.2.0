package com.example.security

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import java.security.KeyStore

/**
 * Zero-Knowledge Key Shredder ("Panic Wipe") & Disguise Mode Manager.
 * Instantly purges hardware keystores, local databases, and caches upon emergency trigger,
 * and toggles application disguise between BANG Messenger and a functional Calculator stealth alias.
 */
class SecurityShredder(private val context: Context) {

    fun executePanicWipe(onWipeComplete: () -> Unit = {}) {
        try {
            // 1. Wipe Encrypted SQLite & Room Database
            context.deleteDatabase("bang_mesh_vault.db")
            context.deleteDatabase("bang_encrypted_db.db")
            context.deleteDatabase("bang_mesh_storage.db")

            // 2. Clear Android Hardware KeyStore Keys
            try {
                val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
                val aliases = keyStore.aliases()
                while (aliases.hasMoreElements()) {
                    val alias = aliases.nextElement()
                    keyStore.deleteEntry(alias)
                }
            } catch (_: Exception) {
                // KeyStore may be uninitialized or empty
            }

            // 3. Purge Local Cache & Shared Preferences
            context.cacheDir.deleteRecursively()
            context.getSharedPreferences("bang_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            context.getSharedPreferences("bang_security", Context.MODE_PRIVATE).edit().clear().apply()

            // 4. Trigger UI callback
            onWipeComplete()
        } catch (e: Exception) {
            onWipeComplete()
        }
    }
}

object DisguiseModeManager {

    /**
     * Toggles launcher app icon disguise dynamically at runtime via Android activity-alias.
     * Switches between standard BANG Messenger and Calculator disguise.
     */
    fun toggleDisguiseMode(context: Context, enableCalculatorDisguise: Boolean): Boolean {
        return try {
            val packageManager = context.packageManager
            val packageName = context.packageName
            val mainComponent = ComponentName(packageName, "$packageName.MainActivity")
            val aliasComponent = ComponentName(packageName, "$packageName.MainActivityCalculatorAlias")

            if (enableCalculatorDisguise) {
                packageManager.setComponentEnabledSetting(
                    mainComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
                packageManager.setComponentEnabledSetting(
                    aliasComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            } else {
                packageManager.setComponentEnabledSetting(
                    aliasComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
                packageManager.setComponentEnabledSetting(
                    mainComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isDisguiseModeEnabled(context: Context): Boolean {
        return try {
            val packageManager = context.packageManager
            val packageName = context.packageName
            val aliasComponent = ComponentName(packageName, "$packageName.MainActivityCalculatorAlias")
            val state = packageManager.getComponentEnabledSetting(aliasComponent)
            state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } catch (e: Exception) {
            false
        }
    }
}
