package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel : ViewModel() {
    private val apiKey = "a48ab7f2ea1db8788b4a980035313863"

    fun fetchWeather(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: WeatherResponse = RetrofitHelper.service.getWeather(city, apiKey, "metric")

                withContext(Dispatchers.Main) {
                    Log.i("WeatherViewModel", "Full Response: $response")

                    response.main?.temp?.let { temp ->
                        Log.i("WeatherViewModel", "Weather in $city: ${temp}°C")
                    } ?: Log.i("WeatherViewModel", "Error: 'main' is null")
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather", e)
            }
        }
    }
}



