package com.example.weatherapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.Repo
import com.example.weatherapp.data.repo.RepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel (private val repo: RepoImpl): ViewModel() {



    private val _currentDetails = MutableLiveData<WeatherResponse?>(null)
    val currentDetails: LiveData<WeatherResponse?> = _currentDetails
    private val _currentDetailsList = MutableLiveData<WeatherForecastResponse>(null)
    val currentDetailsList: LiveData<WeatherForecastResponse> = _currentDetailsList

    private val _message = MutableLiveData<String?>(null)
    val message: LiveData<String?> = _message


    fun fetchWeatherFromLatLonUnitLang(lat: Double, lon: Double, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: WeatherResponse =
                    repo.fetchWeatherFromLatLonUnitLang(lat, lon, units, lang)
                _currentDetails.postValue(response)
            } catch (e: Exception) {
                _message.postValue("Error fetching weather: ${e.message}")
            }
        }
    }
    fun get5DaysWeatherForecast(lat: Double, lon: Double, units: String, lang: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: WeatherForecastResponse =
                    repo.get5DaysWeatherForecast(lat, lon, units, lang)
                _currentDetailsList.postValue(response)
            }catch (e: Exception){
                _message.postValue("Error fetching weather: ${e.message}")
            }
        }
    }
}
class WeatherViewModelFactory(private val repo: RepoImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WeatherViewModel(repo) as T
    }
}



