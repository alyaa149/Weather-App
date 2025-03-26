package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface Repo {
    suspend fun fetchWeatherFromLatLonUnitLang(lat: Double, lon: Double, units: String = "metric", lang: String = "en"): Flow<WeatherResponse>
    suspend fun get5DaysWeatherForecast(lat: Double, lon: Double, units: String = "metric", lang: String = "en"): Flow<WeatherForecastResponse>
    //Room
    suspend fun insertWeather(city: City):Long
    suspend fun deleteWeather(city: City):Int
    fun getAllFavs(): Flow<List<City>>



}