package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Reminder
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    //Favorites
    suspend fun insertWeather(city: City):Long
    suspend fun deleteWeather(city: City):Int
    fun getAllFavs(): Flow<List<City>>
    //Reminder
    suspend fun insertReminder(reminder: Reminder):Long
    fun getAllReminders(): Flow<List<Reminder>>
    suspend fun deleteReminder(reminderId: Int)



}