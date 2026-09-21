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
    assertEquals("DFCC GeoTask", appName)
  }

  @Test
  fun `verify location tracker permission helpers`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val tracker = com.example.location.LocationTracker(context)
    // In Robolectric test context without granted permissions, hasLocationPermission is false
    org.junit.Assert.assertNotNull(tracker.currentLocation.value)
    org.junit.Assert.assertFalse(tracker.isTrackingRealGps.value)
  }
}
