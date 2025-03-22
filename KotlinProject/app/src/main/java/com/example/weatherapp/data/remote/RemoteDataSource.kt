package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource  {
    suspend fun getInfoFromLatLonAndUnitAndLang(): Flow<WeatherResponse>
    suspend fun get5DaysWeatherForecast(): Flow<WeatherForecastResponse>
    suspend fun getHourlyWeatherForecast(): Flow<WeatherForecastResponse>

}