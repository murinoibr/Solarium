package com.example.ui.viewmodel

import com.example.data.model.ManualSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
}
