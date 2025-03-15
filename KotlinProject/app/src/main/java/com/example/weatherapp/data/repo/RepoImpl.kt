package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSourceImpl

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
    ): WeatherResponse {
        return remoteDataSource.getInfoFromLatLonAndUnitAndLang(lat, lon, units, lang)
    }
}