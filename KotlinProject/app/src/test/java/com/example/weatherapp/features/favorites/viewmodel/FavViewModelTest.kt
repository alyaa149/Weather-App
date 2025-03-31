package com.example.weatherapp.features.favorites.viewmodel

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.example.weatherapp.Response
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.repo.Repo
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FavViewModelTest {

    private lateinit var viewModel: FavViewModel
    private val weatherRepository: Repo = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FavViewModel(weatherRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchFavoriteLocations initially emits Loading`() = runTest {
        assert(viewModel.favoriteLocations.value is Response.Loading)
    }

    @Test
    fun `fetchFavoriteLocations emits Success when repository returns data`() = runTest {
        val cities = listOf(City(address = "New York", lat =  40.7128, lon = -74.0060, weatherResponse = mockk()))
        coEvery { weatherRepository.getAllFavs() } returns flowOf(cities)

        viewModel.fetchFavoriteLocations()
        advanceUntilIdle()

        assert(viewModel.favoriteLocations.value is Response.Success)
        assertEquals(cities, (viewModel.favoriteLocations.value as Response.Success).data)
    }

    @Test
    fun `fetchFavoriteLocations emits Success with empty list when no favorites`() = runTest {
        coEvery { weatherRepository.getAllFavs() } returns flowOf(emptyList())

        viewModel.fetchFavoriteLocations()
        advanceUntilIdle()

        assert(viewModel.favoriteLocations.value is Response.Success)
        assert((viewModel.favoriteLocations.value as Response.Success).data.isEmpty())
    }

    @Test
    fun `fetchFavoriteLocations emits Failure when repository throws exception`() = runTest {
        coEvery { weatherRepository.getAllFavs() } returns flow { throw Exception("Database error") }

        viewModel.fetchFavoriteLocations()
        advanceUntilIdle()

        assert(viewModel.favoriteLocations.value is Response.Failure)
    }

//    @Test
//    fun `deleteWeather removes city and updates state`() = runTest {
//        val city = City(address = "Paris", lat = 48.8566, lon = 2.3522, weatherResponse = mockk())
//        coEvery { weatherRepository.deleteWeather(city) } just Runs
//        coEvery { weatherRepository.getAllFavs() } returns flowOf(emptyList())
//
//        val snackbarHostState = SnackbarHostState()
//        val coroutineScope = CoroutineScope(testDispatcher)
//
//        viewModel.deleteWeather(city, snackbarHostState, coroutineScope)
//        advanceUntilIdle()
//
//        coVerify { weatherRepository.deleteWeather(city) }
//        assert(viewModel.favoriteLocations.value is Response.Success)
//        assert((viewModel.favoriteLocations.value as Response.Success).data.isEmpty())
//    }

    @Test
    fun `deleteWeather emits Failure when repository throws exception`() = runTest {
        val city = City(address = "London", lat = 51.5074, lon = -0.1278, weatherResponse = mockk())
        coEvery { weatherRepository.deleteWeather(city) } throws Exception("Delete failed")

        val snackbarHostState = SnackbarHostState()
        val coroutineScope = CoroutineScope(testDispatcher)

        viewModel.deleteWeather(city, snackbarHostState, coroutineScope)
        advanceUntilIdle()

        assert(viewModel.favoriteLocations.value is Response.Failure)
    }
//
//    @Test
//    fun `deleteWeather shows snackbar and restores city on undo`() = runTest {
//        val city = City(address = "Tokyo", lat = 35.6895, lon = 139.6917, weatherResponse = mockk())
//        coEvery { weatherRepository.deleteWeather(city) } just Runs
//        coEvery { weatherRepository.insertWeather(city) } just Runs
//
//        val snackbarHostState = SnackbarHostState()
//        val coroutineScope = CoroutineScope(testDispatcher)
//
//        launch {
//            snackbarHostState.showSnackbar("Place deleted", "Undo", SnackbarDuration.Short)
//        }
//
//        viewModel.deleteWeather(city, snackbarHostState, coroutineScope)
//        advanceUntilIdle()
//
//        coVerify { weatherRepository.deleteWeather(city) }
//        coVerify { weatherRepository.insertWeather(city) }
//    }
}
