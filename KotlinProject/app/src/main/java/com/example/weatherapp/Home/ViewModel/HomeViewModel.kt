package com.example.weatherapp.Home.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Response
import com.example.weatherapp.Utils.Location.LocationRepository
import com.example.weatherapp.WeatherViewModel
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.repo.RepoImpl
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepoImpl,private val context: android.content.Context,private val locationRepo: LocationRepository) : ViewModel() {

    private val _currentDetails = MutableStateFlow<Response<WeatherResponse>>(Response.Loading)
    val currentDetails: StateFlow<Response<WeatherResponse>> = _currentDetails


    private val _currentDetailsList = MutableStateFlow<Response<WeatherForecastResponse>>(Response.Loading)
    val currentDetailsList: StateFlow<Response<WeatherForecastResponse>> = _currentDetailsList

    init {
        fetchWeatherForCurrentLocation()
    }
    private fun fetchWeatherForCurrentLocation() {
        viewModelScope.launch {
            val location = locationRepo.getCurrentLocation(context)
            location?.let {
                fetchWeatherFromLatLonUnitLang(it.latitude, it.longitude)
             //   get5DaysWeatherForecast(it.latitude, it.longitude)
            } ?: run {
                _currentDetails.value = Response.Failure(Exception("Location not available"))
            }
        }
    }

    fun fetchWeatherFromLatLonUnitLang(lat: Double, lon: Double, units: String = "metric", lang: String = "en") {
        viewModelScope.launch(Dispatchers.IO) {
            _currentDetails.value = Response.Loading
            try {
                repo.fetchWeatherFromLatLonUnitLang(lat, lon, units, lang)
                    .collect { response ->
                        _currentDetails.value = Response.Success(response)
                        Log.i("response", response.toString())
                    }
            } catch (e: Exception) {
                _currentDetails.value = Response.Failure(e)
                Log.e("WeatherError", e.message.toString())
            }
        }
    }

//    fun get5DaysWeatherForecast(lat: Double, lon: Double, units: String = "metric", lang: String = "en") {
//        viewModelScope.launch(Dispatchers.IO) {
//            _currentDetailsList.value = Response.Loading
//            try {
//                repo.get5DaysWeatherForecast(lat, lon, units, lang)
//                    .collect { response ->
//                        _currentDetailsList.value = Response.Success(response)
//                        Log.i("response", response.toString())
//                    }
//            } catch (e: Exception) {
//                _currentDetailsList.value = Response.Failure(e)
//                Log.e("WeatherError", e.message.toString())
//            }
//        }
//    }
}

class HomeViewModelFactory(private val repo: RepoImpl,private val context: android.content.Context,private val locationRepo: LocationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo,context,locationRepo) as T
    }
}
