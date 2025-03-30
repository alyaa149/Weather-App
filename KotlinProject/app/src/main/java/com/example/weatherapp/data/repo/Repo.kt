package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherInHomeUsingRoom
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
    fun getWeatherFromLatLonOffline(lat: Double, lon: Double): Flow<City>
    suspend fun updateWeather(lat: Double, lon: Double, weatherResponse: WeatherResponse)
    //Reminder
    suspend fun insertReminder(reminder: Reminder):Long
    fun getAllReminders(): Flow<List<Reminder>>
    suspend fun deleteReminder(reminderId: Int)
    //Home Room
    fun getAllDetailsWeatherFromLatLonHome(lat: Double, lon: Double): Flow<WeatherInHomeUsingRoom?>
    suspend fun updateWeatherHome(lat: Double, lon: Double, weatherResponse: WeatherResponse?, weatherForecastResponse: WeatherForecastResponse?)
    suspend fun insertWeatherHome(weather: WeatherInHomeUsingRoom):Long





}