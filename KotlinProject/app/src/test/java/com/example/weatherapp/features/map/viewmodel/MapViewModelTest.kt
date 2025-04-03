package com.example.weatherapp.features.map.viewmodel

import android.location.Geocoder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.runner.AndroidJUnit4
import com.example.weatherapp.Response
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.repo.Repo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

import org.junit.rules.TestRule
import org.junit.runner.RunWith

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MapViewModelTest {
    private lateinit var viewModel: MapViewModel
    private lateinit var repo: Repo
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Replace Main dispatcher
        repo = mockk(relaxed = true)
        viewModel = MapViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // Clean up
    }

    @Test
    fun fetchWeatherAndInsert_UpdatesStateOnFailure_invalidCoordinatesfailureState() = runTest {
        // Given: Invalid latitude and longitude
        val lat = Double.NaN
        val lon = Double.NaN

        // When: Fetch weather with invalid coordinates
        viewModel.fetchWeatherAndInsert(lat, lon, "Alexandria")
        testDispatcher.scheduler.advanceUntilIdle() //verify that the coroutine is finished

        // Then: Verify the state is a failure
        val state = viewModel.currentDetails.value
        assertTrue(state is Response.Failure)

    }
}