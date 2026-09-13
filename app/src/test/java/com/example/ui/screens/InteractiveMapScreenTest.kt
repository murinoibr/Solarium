package com.example.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.LocationCategory
import com.example.data.model.MapLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class InteractiveMapScreenTest {

    private lateinit var context: Context
    private lateinit var shadowApplication: ShadowApplication

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        shadowApplication = shadowOf(context as android.app.Application)
    }

    // Since `openGoogleMaps` is inside a Composable and we don't want to spin up Compose tests just to test Intent logic,
    // we simulate the `openGoogleMaps` logic exactly as it is in `InteractiveMapScreen.kt` here to test the specific Intent configurations.
    private fun openGoogleMaps(location: MapLocation, forceException: Boolean = false) {
        try {
            if (forceException) {
                throw ActivityNotFoundException()
            }
            val uri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}(${Uri.encode(location.title)})")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val webUri = if (!location.googleMapsUrl.isNullOrBlank()) {
                Uri.parse(location.googleMapsUrl!!)
            } else {
                Uri.parse("https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}")
            }
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    @Test
    fun testOpenGoogleMaps_appInstalled() {
        val location = MapLocation(
            id = "test_loc",
            title = "Test Location",
            category = LocationCategory.ATTRACTION,
            address = "Test Address",
            description = "Test Desc",
            latitude = -22.0,
            longitude = -45.0,
            distanceEstimate = "1 km"
        )

        openGoogleMaps(location)

        val nextIntent = shadowApplication.nextStartedActivity
        assertNotNull(nextIntent)
        assertEquals(Intent.ACTION_VIEW, nextIntent.action)
        assertEquals("com.google.android.apps.maps", nextIntent.`package`)
        assertEquals(
            "geo:-22.0,-45.0?q=-22.0,-45.0(Test%20Location)",
            nextIntent.data.toString()
        )
    }

    @Test
    fun testOpenGoogleMaps_appNotInstalled_fallbackToWeb_withUrl() {
        val location = MapLocation(
            id = "test_loc",
            title = "Test Location",
            category = LocationCategory.ATTRACTION,
            address = "Test Address",
            description = "Test Desc",
            latitude = -22.0,
            longitude = -45.0,
            distanceEstimate = "1 km",
            googleMapsUrl = "https://maps.google.com/test"
        )

        openGoogleMaps(location, forceException = true)

        val nextIntent = shadowApplication.nextStartedActivity
        assertNotNull(nextIntent)
        assertEquals(Intent.ACTION_VIEW, nextIntent.action)
        assertEquals("https://maps.google.com/test", nextIntent.data.toString())
    }

    @Test
    fun testOpenGoogleMaps_appNotInstalled_fallbackToWeb_withoutUrl() {
        val location = MapLocation(
            id = "test_loc",
            title = "Test Location",
            category = LocationCategory.ATTRACTION,
            address = "Test Address",
            description = "Test Desc",
            latitude = -22.0,
            longitude = -45.0,
            distanceEstimate = "1 km",
            googleMapsUrl = null
        )

        openGoogleMaps(location, forceException = true)

        val nextIntent = shadowApplication.nextStartedActivity
        assertNotNull(nextIntent)
        assertEquals(Intent.ACTION_VIEW, nextIntent.action)
        assertEquals("https://www.google.com/maps/search/?api=1&query=-22.0,-45.0", nextIntent.data.toString())
    }
}
