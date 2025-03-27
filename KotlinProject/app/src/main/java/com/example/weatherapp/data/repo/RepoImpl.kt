package com.example.weatherapp.data.repo

import android.util.Log
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow


class RepoImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : Repo {

    override suspend fun fetchWeatherFromLatLonUnitLang(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {
        return remoteDataSource.getInfoFromLatLonAndUnitAndLang(
            lat,
            lon,
            units,
            lang
        )
    }

    override suspend fun get5DaysWeatherForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherForecastResponse> {
        return remoteDataSource.get5DaysWeatherForecast(
            lat,
            lon,
            units,
            lang
        )
    }


    override suspend fun insertWeather(city: City): Long {
        Log.i("response", city.toString())
        return localDataSource.insertWeather(city)
    }

    override suspend fun deleteWeather(city: City): Int {
        return localDataSource.deleteWeather(city)
    }

    override fun getAllFavs(): Flow<List<City>> {
        return localDataSource.getAllFavs()
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        return localDataSource.insertReminder(reminder)
    }

    override fun getAllReminders(): Flow<List<Reminder>> {
return localDataSource.getAllReminders()    }

    override suspend fun deleteReminder(reminder: Reminder) {
return localDataSource.deleteReminder(reminder)        }

}