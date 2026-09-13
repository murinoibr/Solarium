package com.example.ui.viewmodel

import com.example.data.model.ManualSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

import com.example.data.model.LocationCategory
import com.example.data.model.MapLocation
import com.example.data.model.MapViewMode
import com.example.data.model.FloorPlanCategory
import com.example.data.model.FloorPlanPoint
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class HouseViewModelTest {

    private lateinit var viewModel: HouseViewModel

    @Before
    fun setup() {
        viewModel = HouseViewModel()
    }

    @Test
    fun openLaunchPage_updatesState() {
        // Initial state is true
        assertTrue(viewModel.uiState.value.isLaunchPageVisible)

        // Dismiss first
        viewModel.dismissLaunchPage()
        assertFalse(viewModel.uiState.value.isLaunchPageVisible)

        // Now open it
        viewModel.openLaunchPage()
        assertTrue(viewModel.uiState.value.isLaunchPageVisible)
    }

    @Test
    fun selectTab_updatesState() {
        // Initial state is 0
        assertEquals(0, viewModel.uiState.value.currentTab)

        // Select tab 1
        viewModel.selectTab(1)
        assertEquals(1, viewModel.uiState.value.currentTab)

        // Select tab 3
        viewModel.selectTab(3)
        assertEquals(3, viewModel.uiState.value.currentTab)
    }

    @Test
    fun openSection_updatesState() {
        // Initial state is null
        assertEquals(null, viewModel.uiState.value.selectedSection)

        // Create a dummy section
        val dummySection = ManualSection(
            id = 99,
            title = "Dummy Title",
            subtitle = "Dummy Subtitle",
            shortSummary = "Dummy Summary",
            iconName = "dummy_icon"
        )

        // Open section
        viewModel.openSection(dummySection)
        assertNotNull(viewModel.uiState.value.selectedSection)
        assertEquals(99, viewModel.uiState.value.selectedSection?.id)
        assertEquals("Dummy Title", viewModel.uiState.value.selectedSection?.title)
    }

    @Test
    fun openSectionById_updatesState() {
        val sectionId = viewModel.allSections.first().id
        viewModel.openSectionById(sectionId)
        assertEquals(sectionId, viewModel.uiState.value.selectedSection?.id)
    }

    @Test
    fun closeSection_updatesState() {
        viewModel.openSectionById(viewModel.allSections.first().id)
        assertNotNull(viewModel.uiState.value.selectedSection)

        viewModel.closeSection()
        assertNull(viewModel.uiState.value.selectedSection)
    }

    @Test
    fun updateSearchQuery_updatesState() {
        viewModel.updateSearchQuery("Pool")
        assertEquals("Pool", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun filterMapCategory_updatesState() {
        val category = LocationCategory.FOOD
        viewModel.filterMapCategory(category)
        assertEquals(category, viewModel.uiState.value.activeMapCategory)

        // Toggling same category sets it to null
        viewModel.filterMapCategory(category)
        assertNull(viewModel.uiState.value.activeMapCategory)
    }

    @Test
    fun selectMapLocation_updatesState() {
        val dummyLocation = MapLocation(
            id = "1",
            title = "Dummy",
            description = "Dummy Description",
            latitude = 0.0,
            longitude = 0.0,
            category = LocationCategory.FOOD,
            address = "Dummy Address",
            distanceEstimate = "5 min"
        )
        viewModel.selectMapLocation(dummyLocation)
        assertEquals(dummyLocation, viewModel.uiState.value.selectedLocation)
    }

    @Test
    fun setMapViewMode_updatesState() {
        viewModel.setMapViewMode(MapViewMode.NEIGHBORHOOD)
        assertEquals(MapViewMode.NEIGHBORHOOD, viewModel.uiState.value.mapViewMode)
    }

    @Test
    fun filterFloorCategory_updatesState() {
        val category = FloorPlanCategory.COMFORT_SERVICE
        viewModel.filterFloorCategory(category)
        assertEquals(category, viewModel.uiState.value.activeFloorCategory)

        // Toggling same category sets it to null
        viewModel.filterFloorCategory(category)
        assertNull(viewModel.uiState.value.activeFloorCategory)
    }

    @Test
    fun selectFloorPoint_updatesState() {
        val dummyPoint = FloorPlanPoint(
            id = "dummy",
            title = "Dummy",
            normX = 0.5f,
            normY = 0.5f,
            category = FloorPlanCategory.COMFORT_SERVICE,
            roomOrZone = "Dummy Room",
            description = "Dummy Description",
            iconName = "dummy_icon"
        )
        viewModel.selectFloorPoint(dummyPoint)
        assertEquals(dummyPoint, viewModel.uiState.value.selectedFloorPoint)
    }

    @Test
    fun selectFloorPointById_updatesState() {
        if (viewModel.allFloorPoints.isNotEmpty()) {
            val pointId = viewModel.allFloorPoints.first().id
            viewModel.selectFloorPointById(pointId)
            assertEquals(pointId, viewModel.uiState.value.selectedFloorPoint?.id)
            assertEquals(MapViewMode.NEIGHBORHOOD, viewModel.uiState.value.mapViewMode)
        }
    }

    @Test
    fun updateFloorSearch_updatesState() {
        viewModel.updateFloorSearch("Kitchen")
        assertEquals("Kitchen", viewModel.uiState.value.floorSearchQuery)
    }

    @Test
    fun filterRecommendationCategory_updatesState() {
        viewModel.filterRecommendationCategory("Restaurants")
        assertEquals("Restaurants", viewModel.uiState.value.recommendationCategory)
    }

    @Test
    fun toggleFavorite_updatesState() {
        val recId = "rec_1"
        assertFalse(viewModel.uiState.value.favoriteRecIds.contains(recId))

        viewModel.toggleFavorite(recId)
        assertTrue(viewModel.uiState.value.favoriteRecIds.contains(recId))

        viewModel.toggleFavorite(recId)
        assertFalse(viewModel.uiState.value.favoriteRecIds.contains(recId))
    }

    @Test
    fun showFeedback_updatesState() {
        viewModel.showFeedback("Copied!")
        assertEquals("Copied!", viewModel.uiState.value.copyFeedbackMessage)
    }

    @Test
    fun clearFeedback_updatesState() {
        viewModel.showFeedback("Copied!")
        viewModel.clearFeedback()
        assertNull(viewModel.uiState.value.copyFeedbackMessage)
    }

    @Test
    fun clearChat_updatesState() {
        viewModel.clearChat()
        assertEquals(1, viewModel.uiState.value.chatMessages.size)
        assertEquals("msg_reset", viewModel.uiState.value.chatMessages.first().id)
    }
}
