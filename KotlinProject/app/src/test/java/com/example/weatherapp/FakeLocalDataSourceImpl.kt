package com.example.weatherapp

import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherInHomeUsingRoom
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSourceImpl : LocalDataSource {
    private val weatherHomeData = mutableMapOf<Pair<Double, Double>, WeatherInHomeUsingRoom>()
    private val reminders = mutableListOf<Reminder>()

    override suspend fun insertWeather(city: City): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeather(city: City): Int {
        TODO("Not yet implemented")
    }

    override fun getAllFavs(): Flow<List<City>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        reminders.add(reminder)
        return reminder.id.toLong()
    }

    override fun getAllReminders(): Flow<List<Reminder>> {
        return flow { emit(reminders) }
    }

    override suspend fun deleteReminder(reminderId: Int) {
        reminders.removeIf { it.id == reminderId }
    }

    override fun getWeatherFromLatLonOffline(lat: Double, lon: Double): Flow<City> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWeather(lat: Double, lon: Double, weatherResponse: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override fun getAllDetailsWeatherFromLatLonHome(
        lat: Double,
        lon: Double
    ): Flow<WeatherInHomeUsingRoom?> {
        return flow { emit(weatherHomeData[Pair(lat, lon)]) }
    }

    override suspend fun updateWeatherHome(
        lat: Double,
        lon: Double,
        weatherResponse: WeatherResponse?,
        weatherForecastResponse: WeatherForecastResponse?
    ) {
        val weather = WeatherInHomeUsingRoom(lat, lon, weatherResponse, weatherForecastResponse)
        weatherHomeData[Pair(lat, lon)] = weather  // Ensure it updates correctly
    }


    override suspend fun insertWeatherHome(weather: WeatherInHomeUsingRoom): Long {
        TODO("Not yet implemented")
    }
}