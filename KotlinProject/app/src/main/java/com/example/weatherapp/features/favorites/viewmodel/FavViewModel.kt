package com.example.weatherapp.features.favorites.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.repo.Repo
import com.example.weatherapp.data.repo.RepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavViewModel(private val weatherRepository: Repo) : ViewModel() {

    private val _favoriteLocations = MutableStateFlow<List<City>>(emptyList())
    val favoriteLocations: StateFlow<List<City>> = _favoriteLocations.asStateFlow()

    init {
        fetchFavoriteLocations()
    }

    private fun fetchFavoriteLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.getAllFavs().collect { cities ->
                Log.i("response", "Fetched cities: $cities")
                _favoriteLocations.value = cities
            }
        }

    }


    fun deleteWeather(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = weatherRepository.deleteWeather(city)
                Log.i("response", "Deleted city ID: $result")
            } catch (e: Exception) {
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