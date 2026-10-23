package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BANG", appName)
  }

  @Test
  fun `verify switching between mesh and internet mode`() {
    val viewModel = com.example.ui.BangViewModel()
    // Default is HYBRID_AUTO
    assertEquals(com.example.model.ConnectivityMode.HYBRID_AUTO, viewModel.uiState.value.connectivityMode)

    // Switch to Off-grid Mesh
    viewModel.setConnectivityMode(com.example.model.ConnectivityMode.OFFLINE_MESH)
    assertEquals(com.example.model.ConnectivityMode.OFFLINE_MESH, viewModel.uiState.value.connectivityMode)

    // Switch to Internet Cloud
    viewModel.setConnectivityMode(com.example.model.ConnectivityMode.CLOUD_ONLINE)
    assertEquals(com.example.model.ConnectivityMode.CLOUD_ONLINE, viewModel.uiState.value.connectivityMode)
  }

  @Test
  fun `verify high-fidelity UI design showcase modes and Alex Rivera discovery`() {
    val viewModel = com.example.ui.BangViewModel()

    // 1. Showcase modes switching
    assertEquals(com.example.ui.DesignShowcaseMode.NONE, viewModel.uiState.value.activeShowcaseMode)
    viewModel.setDesignShowcaseMode(com.example.ui.DesignShowcaseMode.LOGIN)
    assertEquals(com.example.ui.DesignShowcaseMode.LOGIN, viewModel.uiState.value.activeShowcaseMode)

    viewModel.setDesignShowcaseMode(com.example.ui.DesignShowcaseMode.SIGNUP)
    assertEquals(com.example.ui.DesignShowcaseMode.SIGNUP, viewModel.uiState.value.activeShowcaseMode)

    viewModel.setDesignShowcaseMode(com.example.ui.DesignShowcaseMode.CONTACT_SYNC)
    assertEquals(com.example.ui.DesignShowcaseMode.CONTACT_SYNC, viewModel.uiState.value.activeShowcaseMode)

    viewModel.setDesignShowcaseMode(com.example.ui.DesignShowcaseMode.PEER_DISCOVERY)
    assertEquals(com.example.ui.DesignShowcaseMode.PEER_DISCOVERY, viewModel.uiState.value.activeShowcaseMode)

    // 2. Alex Rivera peer verification
    val alexPeer = viewModel.uiState.value.meshPeers.find { it.displayName == "Alex Rivera" }
    org.junit.Assert.assertNotNull(alexPeer)
    assertEquals("@alex_rivera", alexPeer?.handle)
    org.junit.Assert.assertTrue(alexPeer?.keyInfo?.contains("Curve25519 Verified") == true)

    // 3. Peer discovery modal open/close
    viewModel.openPeerDiscoveryModal(alexPeer)
    org.junit.Assert.assertTrue(viewModel.uiState.value.showPeerDiscoveryModal)
    assertEquals("Alex Rivera", viewModel.uiState.value.discoveryModalPeer?.displayName)
    viewModel.closePeerDiscoveryModal()
    org.junit.Assert.assertFalse(viewModel.uiState.value.showPeerDiscoveryModal)

    // 4. Contact sync modal open/close
    viewModel.openContactSyncModal()
    org.junit.Assert.assertTrue(viewModel.uiState.value.showContactSyncModal)
    viewModel.closeContactSyncModal()
    org.junit.Assert.assertFalse(viewModel.uiState.value.showContactSyncModal)
  }

  @Test
  fun `verify user presence modes and automatic active idle detection`() {
    val viewModel = com.example.ui.BangViewModel()

    // Default presence mode is USING_APP
    assertEquals(com.example.model.UserPresenceMode.USING_APP, viewModel.uiState.value.userPresenceMode)
    org.junit.Assert.assertTrue(viewModel.uiState.value.isAutoPresenceDetection)

    // Switch through all requested user modes: Not Using App, Do Not Disturb, Sleep Mode, Invisible Mode
    viewModel.setUserPresenceMode(com.example.model.UserPresenceMode.NOT_USING_APP)
    assertEquals(com.example.model.UserPresenceMode.NOT_USING_APP, viewModel.uiState.value.userPresenceMode)

    viewModel.setUserPresenceMode(com.example.model.UserPresenceMode.DO_NOT_DISTURB)
    assertEquals(com.example.model.UserPresenceMode.DO_NOT_DISTURB, viewModel.uiState.value.userPresenceMode)

    viewModel.setUserPresenceMode(com.example.model.UserPresenceMode.SLEEP_MODE)
    assertEquals(com.example.model.UserPresenceMode.SLEEP_MODE, viewModel.uiState.value.userPresenceMode)

    viewModel.setUserPresenceMode(com.example.model.UserPresenceMode.INVISIBLE_MODE)
    assertEquals(com.example.model.UserPresenceMode.INVISIBLE_MODE, viewModel.uiState.value.userPresenceMode)

    // Test automatic active vs idle background switching
    viewModel.toggleAutoPresenceDetection(true)
    viewModel.simulateIdleBackground(true)
    assertEquals(com.example.model.UserPresenceMode.NOT_USING_APP, viewModel.uiState.value.userPresenceMode)

    viewModel.simulateIdleBackground(false)
    assertEquals(com.example.model.UserPresenceMode.USING_APP, viewModel.uiState.value.userPresenceMode)
  }

  @Test
  fun `verify EULA clause 5 account and cryptographic data deletion`() {
    val viewModel = com.example.ui.BangViewModel()
    val initialFingerprint = viewModel.uiState.value.identity.fingerprint

    // Trigger account and data deletion
    viewModel.purgeAllAccountData()

    // Ensure all local chat threads and pulses are purged
    org.junit.Assert.assertTrue(viewModel.uiState.value.threads.isEmpty())
    org.junit.Assert.assertTrue(viewModel.uiState.value.meshStories.isEmpty())
    org.junit.Assert.assertNull(viewModel.uiState.value.selectedThreadId)

    // Ensure new zero-knowledge ephemeral cryptographic identity keys are generated
    val newFingerprint = viewModel.uiState.value.identity.fingerprint
    org.junit.Assert.assertNotEquals(initialFingerprint, newFingerprint)
    org.junit.Assert.assertTrue(newFingerprint.startsWith("BANG-ED25519-"))
    assertEquals(0, viewModel.uiState.value.identity.strangerPacketsForwarded)
    assertEquals(0L, viewModel.uiState.value.identity.bandwidthDonatedBytes)
  }
}

