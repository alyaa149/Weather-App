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

    ): Flow<WeatherResponse> {
        return remoteDataSource.getInfoFromLatLonAndUnitAndLang()
    }

    override suspend fun get5DaysWeatherForecast(

    ): Flow<WeatherForecastResponse> {
        return remoteDataSource.get5DaysWeatherForecast()
    }

    override suspend fun getHourlyWeatherForecast(): Flow<WeatherForecastResponse> {
        return remoteDataSource.getHourlyWeatherForecast()
    }
}