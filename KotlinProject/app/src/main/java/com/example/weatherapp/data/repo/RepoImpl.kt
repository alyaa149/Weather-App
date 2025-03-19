package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow

class RepoImpl  (
    private val remoteDataSource: RemoteDataSourceImpl,
    private val localDataSource: LocalDataSourceImpl
):Repo{
    override suspend fun fetchWeather(city: String) {

    }

    override suspend fun fetchWeatherFromLatLonUnitLang (
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {
        return remoteDataSource.getInfoFromLatLonAndUnitAndLang(lat, lon, units, lang)
    }

    override suspend fun get5DaysWeatherForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherForecastResponse> {
        return remoteDataSource.get5DaysWeatherForecast(lat, lon, units, lang)
    }

    override suspend fun getHourlyWeatherForecast(
        lat: Double,
        lon: Double
    ): Flow<WeatherForecastResponse> {
        return remoteDataSource.getHourlyWeatherForecast(lat, lon)
    }
}