package com.example.weatherapp.Home.ViewModel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Response
import com.example.weatherapp.Utils.Location.LocationRepository
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.repo.RepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel(private val repo: RepoImpl, private val context: Context, private val locationRepo: LocationRepository) : ViewModel() {

    private val _currentDetails = MutableStateFlow<Response<WeatherResponse>>(Response.Loading)
    val currentDetails: StateFlow<Response<WeatherResponse>> = _currentDetails
    @RequiresApi(Build.VERSION_CODES.O)
    val currentDate= fetchCurrentTime()
    @RequiresApi(Build.VERSION_CODES.O)
    val currentTime= fetchformattedDateTime()


    private val _nextHoursDetailsList = MutableStateFlow<Response<List<WeatherResponse>>>(Response.Loading)
    val nextHoursDetailsList: StateFlow<Response<List<WeatherResponse>>> = _nextHoursDetailsList

    private val _futureDaysList = MutableStateFlow<Response<List<WeatherResponse>>>(Response.Loading)
    val futureDaysList: StateFlow<Response<List<WeatherResponse>>> = _futureDaysList

    private val _futureDays =MutableStateFlow<Response<List<WeatherResponse>>>(Response.Loading)
    val futureDays: StateFlow<Response<List<WeatherResponse>>> = _futureDays

    init {
        fetchWeatherForCurrentLocation()
    }
    private fun fetchWeatherForCurrentLocation() {
        viewModelScope.launch {
            val location = locationRepo.getCurrentLocation(context)
            location?.let {
                fetchWeatherFromLatLonUnitLang(it.latitude, it.longitude)
                getFutureWeatherForecast(it.latitude, it.longitude)
            } ?: run {
                _currentDetails.value = Response.Failure(Exception("Location not available"))
            }
        }
    }
    private fun getFutureDaysWeatherForecast(latitude: Double, longitude: Double,metric: String = "metric", lang: String = "en") {
        viewModelScope.launch(Dispatchers.IO) {
            _futureDays.value = Response.Loading
            try {
                repo.get5DaysWeatherForecast(latitude, longitude, metric, lang)
                    .collect { response ->
                        _futureDays.value = Response.Success(response.list)
                        Log.i("response", response.list.toString())
                    }


            } catch (e: Exception) {
                _futureDays.value = Response.Failure(e)
                Log.e("WeatherError", e.message.toString())
            }
        }
    }


    private fun getFutureWeatherForecast(latitude: Double, longitude: Double,metric: String = "metric", lang: String = "en") {
        viewModelScope.launch(Dispatchers.IO) {
            _nextHoursDetailsList.value = Response.Loading
            try {
                repo.get5DaysWeatherForecast(latitude, longitude, metric, lang)
                    .collect { response ->

                        _nextHoursDetailsList.value = Response.Success(response.list)
                        Log.i("response", response.list.toString())
                        }
            } catch (e: Exception) {
                _nextHoursDetailsList.value = Response.Failure(e)
                Log.e("WeatherError", e.message.toString())
            }
        }
    }

    private fun fetchWeatherFromLatLonUnitLang(lat: Double, lon: Double, units: String = "metric", lang: String = "en") {
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


        @RequiresApi(Build.VERSION_CODES.O)
        fun fetchCurrentTime(): String {
            val currentDateTime = LocalDateTime.now()
            val formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val currentDay: DayOfWeek = currentDateTime.dayOfWeek
            val dayName = currentDay.name.lowercase().replaceFirstChar { it.uppercase() }
            return "$dayName, $formattedDateTime"
        }
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchformattedDateTime() : String{
        val currentDateTime = LocalDateTime.now()
        val formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
       return formattedDateTime

    }
    }


class HomeViewModelFactory(private val repo: RepoImpl,private val context: android.content.Context,private val locationRepo: LocationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo,context,locationRepo) as T
    }
}
