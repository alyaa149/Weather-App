package com.example.weatherapp.features.map.viewmodel

import android.location.Geocoder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.runner.AndroidJUnit4
import com.example.weatherapp.Response
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.repo.Repo
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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MapViewModel
    private lateinit var repo: Repo

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        repo = mockk(relaxed = true)
        viewModel = MapViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

//    @Test
//    fun fetchWeatherAndInsert_updatesStateOnSuccess() = runTest {
//        // Given
//        val lat = 40.7128
//        val lon = -74.0060
//        val address = "Alexandria"
//
//        // When
//        viewModel.fetchWeatherAndInsert(lat, lon, address)
//
//        // Then
//        val state = viewModel.currentDetails.drop(1).first()
//       assert(state is Response.Success)
//    }

    @Test
    fun fetchWeatherAndInsert_UpdatesStateOnFailure() = runTest {
        // Given
        val lat = Double.NaN
        val lon = Double.NaN
        val address = "Alexandria"

        // When
        viewModel.fetchWeatherAndInsert(lat, lon, address)

        // Then
        val state = viewModel.currentDetails.first()
        assert(state is Response.Failure)
    }
}