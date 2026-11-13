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
    assertEquals("OstaApp", appName)
  }

  @Test
  fun `repository session creation and user services loading`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.data.repository.PanelRepository(context)
    
    // Initial services should be loaded
    val services = repository.userServices.value
    org.junit.Assert.assertTrue("User services should not be empty", services.isNotEmpty())
    
    // Session should be created asynchronously
    val latch = java.util.concurrent.CountDownLatch(1)
    repository.createAuthSession(context = null, openTelegramApp = false) {
      latch.countDown()
    }
    val completed = latch.await(5, java.util.concurrent.TimeUnit.SECONDS)
    org.junit.Assert.assertTrue("Session creation timed out", completed)
    val session = repository.currentAuthSession.value
    org.junit.Assert.assertNotNull(session)
    org.junit.Assert.assertTrue(session.sessionId.isNotBlank())
    org.junit.Assert.assertTrue(session.verificationCode.length == 6)
  }
}
