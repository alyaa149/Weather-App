package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse

interface RemoteDataSource  {
    suspend fun getInfoFromLatLonAndUnitAndLang(lat: Double, lon: Double, units: String, lang: String): WeatherResponse
    suspend fun get5DaysWeatherForecast(lat: Double, lon: Double, units: String, lang: String): WeatherForecastResponse

}