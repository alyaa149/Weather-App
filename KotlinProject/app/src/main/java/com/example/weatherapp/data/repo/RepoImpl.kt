package com.example.weatherapp.data.repo

import android.util.Log
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherInHomeUsingRoom
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
        return localDataSource.getAllReminders()
    }

    override suspend fun deleteReminder(reminderId: Int) {
        return localDataSource.deleteReminder(reminderId)
    }



    override fun getWeatherFromLatLonOffline(lat: Double, lon: Double): Flow<City> {
        return localDataSource.getWeatherFromLatLonOffline(lat, lon)
    }

    override suspend fun updateWeather(lat: Double, lon: Double, weatherResponse: WeatherResponse) {
         localDataSource.updateWeather(lat, lon, weatherResponse)
    }

    //Home

    override suspend fun insertWeatherHome(weather: WeatherInHomeUsingRoom): Long {
        return localDataSource.insertWeatherHome(weather)
    }

    override fun getAllDetailsWeatherFromLatLonHome(lat: Double, lon: Double): Flow<WeatherInHomeUsingRoom?> {
        return localDataSource.getAllDetailsWeatherFromLatLonHome(lat, lon)
    }

    override suspend fun updateWeatherHome(lat: Double, lon: Double, weatherResponse: WeatherResponse?, weatherForecastResponse: WeatherForecastResponse?) {
        localDataSource.updateWeatherHome(lat, lon, weatherResponse, weatherForecastResponse)
    }

}