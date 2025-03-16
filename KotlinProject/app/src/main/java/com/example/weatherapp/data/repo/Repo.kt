package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse

interface Repo {
    suspend fun fetchWeather(city: String)
    suspend fun fetchWeatherFromLatLonUnitLang(lat: Double, lon: Double, units: String, lang: String): WeatherResponse
    suspend fun get5DaysWeatherForecast(lat: Double, lon: Double, units: String, lang: String): WeatherForecastResponse


}