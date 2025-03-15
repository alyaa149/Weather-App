package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.WeatherResponse

interface RemoteDataSource  {
    suspend fun getInfoFromLatLonAndUnitAndLang(lat: Double, lon: Double, units: String, lang: String): WeatherResponse
    suspend fun getCurrentFiveDaysWather()
}