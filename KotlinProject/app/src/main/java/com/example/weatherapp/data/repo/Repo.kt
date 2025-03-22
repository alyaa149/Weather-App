package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface Repo {
    suspend fun fetchWeather(city: String)
    suspend fun fetchWeatherFromLatLonUnitLang(): Flow<WeatherResponse>
    suspend fun get5DaysWeatherForecast(): Flow<WeatherForecastResponse>
    suspend fun getHourlyWeatherForecast(): Flow<WeatherForecastResponse>



}