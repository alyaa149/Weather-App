package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.City
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insertWeather(city: City):Long
    suspend fun deleteWeather(city: City):Int
    fun getAllFavs(): Flow<List<City>>


}