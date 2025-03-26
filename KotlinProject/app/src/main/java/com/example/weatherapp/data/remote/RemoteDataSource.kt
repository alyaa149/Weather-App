package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RemoteDataSource  {
    suspend fun getInfoFromLatLonAndUnitAndLang(
          lat: Double,
          lon: Double,
          units: String,
          lang: String
    ): Flow<WeatherResponse>
    suspend fun get5DaysWeatherForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherForecastResponse>

}