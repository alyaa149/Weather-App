package com.example.weatherapp.data.remote

import com.example.weatherapp.Response
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl  (
 private var weatherService: WeatherService
) : RemoteDataSource{
    private val apiKey = "a48ab7f2ea1db8788b4a980035313863"

    override suspend fun getInfoFromLatLonAndUnitAndLang(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {
        return flow {
            val response = weatherService.getInfoFromLatLonAndUnitAndLang(lat, lon, apiKey, units, lang)
            emit(response)
        }.catch { e->
            Response.Failure(e)
        }
    }

    override suspend fun get5DaysWeatherForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherForecastResponse> {
        return flow {
            val response = weatherService.get5DaysWeatherForecast(lat, lon, apiKey, units, lang)
            emit(response)
        }.catch { e->
            Response.Failure(e)
        }
    }
    override suspend fun getHourlyWeatherForecast(
        lat: Double,
        lon: Double,
    ): Flow<WeatherForecastResponse> {
        return flow {
            val response = weatherService.getHourlyWeatherForecast(lat, lon, apiKey)
            emit(response)
        }.catch { e ->
            Response.Failure(e)
        }
    }


}


