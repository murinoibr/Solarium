package com.example.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.model.LocationCategory
import com.example.data.model.MapLocation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class InteractiveMapScreenPerfTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun benchmarkInteractiveMapScreenRecomposition() {
        val categories = LocationCategory.entries
        val locations = (1..5000).map { i ->
            MapLocation(
                id = i.toString(),
                title = "Location $i",
                category = categories[i % categories.size],
                address = "Address $i",
                description = "Desc",
                latitude = 0.0,
                longitude = 0.0,
                distanceEstimate = "1 km"
            )
        }

        var triggerRecomposition by mutableStateOf(0)

        composeTestRule.setContent {
            triggerRecomposition
            InteractiveMapScreen(
                locations = locations,
                selectedLocation = null,
                activeCategory = null,
                onSelectCategory = {},
                onSelectLocation = {},
                onFeedback = {}
            )
        }

        composeTestRule.waitForIdle()

        val iterations = 50
        val time = measureTimeMillis {
            for (i in 1..iterations) {
                triggerRecomposition++
                composeTestRule.waitForIdle()
            }
        }

        println("Optimized Recomposition time for $iterations iterations with 5000 locations: ${time}ms")
    }
}
