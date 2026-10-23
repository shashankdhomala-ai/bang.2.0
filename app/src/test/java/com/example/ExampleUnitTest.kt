package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun bangAiAssistantEngine_offlineBleMeshQuery_returnsOnDeviceRagResponse() = kotlinx.coroutines.runBlocking {
    val engine = com.example.ai.BangAiAssistantEngine()
    engine.toggleInternetAvailability(false)
    engine.askQuestion("How does BLE mesh work?")
    
    val lastMsg = engine.state.value.messages.last()
    assertFalse(lastMsg.isUser)
    assertEquals(com.example.ai.AiEngineType.OFFLINE_SLM, lastMsg.engineType)
    assertTrue(lastMsg.text.contains("BLE Mesh"))
    assertTrue(lastMsg.text.contains("TTL"))
  }

  @Test
  fun bangAiAssistantEngine_statusRingQuery_returnsColorGuide() = kotlinx.coroutines.runBlocking {
    val engine = com.example.ai.BangAiAssistantEngine()
    engine.askQuestion("What do status ring colors mean?")
    
    val lastMsg = engine.state.value.messages.last()
    assertFalse(lastMsg.isUser)
    assertTrue(lastMsg.text.contains("Electric Cyan"))
    assertTrue(lastMsg.text.contains("Invisible Mode"))
  }

  @Test
  fun bangAiAssistantEngine_onlineHybridSwitching_queriesCloud() = kotlinx.coroutines.runBlocking {
    val engine = com.example.ai.BangAiAssistantEngine()
    engine.toggleInternetAvailability(true)
    engine.askQuestion("Sending SOS alerts")
    
    val lastMsg = engine.state.value.messages.last()
    assertFalse(lastMsg.isUser)
    assertEquals(com.example.ai.AiEngineType.CLOUD_LLM, lastMsg.engineType)
    assertTrue(lastMsg.text.contains("Cloud LLM"))
    assertTrue(lastMsg.text.contains("QOS_P0_CRITICAL"))
  }
}
