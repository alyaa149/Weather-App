package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface Repo {
    suspend fun fetchWeather(city: String)
    suspend fun fetchWeatherFromLatLonUnitLang(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherResponse>
    suspend fun get5DaysWeatherForecast(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherForecastResponse>
    suspend fun getHourlyWeatherForecast(lat: Double, lon: Double): Flow<WeatherForecastResponse>



}