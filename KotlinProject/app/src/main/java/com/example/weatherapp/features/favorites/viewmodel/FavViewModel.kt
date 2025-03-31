package com.example.weatherapp.features.favorites.viewmodel

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Response
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.repo.Repo
import com.example.weatherapp.data.repo.RepoImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavViewModel(private val weatherRepository: Repo) : ViewModel() {

    private val _favoriteLocations = MutableStateFlow<Response<List<City>>>(Response.Loading)
    val favoriteLocations: StateFlow<Response<List<City>>> = _favoriteLocations.asStateFlow()
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        fetchFavoriteLocations()
    }

    fun fetchFavoriteLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.getAllFavs().collect { cities ->
                Log.i("response", "Fetched cities: $cities")
                _favoriteLocations.value = Response.Success(cities)
            }
        }

    }



    fun deleteWeather(city: City,snackbarHostState: SnackbarHostState, coroutineScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherRepository.deleteWeather(city)
                Log.i("response", "Deleted location")

                fetchFavoriteLocations()

                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Place deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        weatherRepository.insertWeather(city)
                    }
                }

            } catch (e: Exception) {
                _favoriteLocations.value = Response.Failure(e)
                Log.e("response", "Delete error: ${e.message}")
            }
        }
    }


}


class FavViewModelFactory(private val repo: RepoImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavViewModel(repo) as T
    }
}