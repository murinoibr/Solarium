package com.example

import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.model.RoomItem
import com.example.ui.components.RoomsGallerySection
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class RoomsFilterPerfTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun benchmarkRoomsGallerySectionRecomposition() {
        // Create 1000 rooms
        val rooms = (1..1000).map { i ->
            RoomItem(
                id = i.toString(),
                title = if (i % 2 == 0) "Quarto Deluxe $i" else "Sala Básica $i",
                floor = "1º Andar",
                subtitle = "Sub",
                bedConfig = "1 cama",
                description = "Desc",
                amenities = emptyList(),
                highlights = emptyList(),
                categoryIcon = if (i % 2 == 0) "Bed" else "Tv",
                gradientColors = Pair(0L, 0L)
            )
        }

        var triggerRecomposition by mutableStateOf(0)

        composeTestRule.setContent {
            // We use triggerRecomposition to force this scope to recompose
            // This wrapper will recompose, and because List<RoomItem> is unstable,
            // RoomsGallerySection will also recompose.
            triggerRecomposition

            RoomsGallerySection(
                rooms = rooms,
                onRoomClick = {},
                onNavigateToMapPoint = {},
                onNavigateToManual = {}
            )
        }

        composeTestRule.waitForIdle()

        // Measure time for 100 recompositions
        val iterations = 100
        val time = measureTimeMillis {
            for (i in 1..iterations) {
                triggerRecomposition++
                composeTestRule.waitForIdle()
            }
        }

        println("Recomposition time for $iterations iterations: ${time}ms")
    }
}
