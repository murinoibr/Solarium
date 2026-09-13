package com.example.ui.viewmodel

import com.example.data.model.ManualSection
import org.junit.Before
import org.junit.Test
import kotlin.system.measureTimeMillis

class HouseViewModelPerfTest {

    private lateinit var viewModel: HouseViewModel

    @Before
    fun setup() {
        viewModel = HouseViewModel()
    }

    @Test
    fun benchmarkOpenSectionById() {
        // Find by ID and update state logic runs.

        val iterations = 1000000
        val time = measureTimeMillis {
            for (i in 1..iterations) {
                // Testing worst case assuming max id is 10
                viewModel.openSectionById(10)
            }
        }

        println("Time to run openSectionById $iterations times: ${time}ms")
    }
}
