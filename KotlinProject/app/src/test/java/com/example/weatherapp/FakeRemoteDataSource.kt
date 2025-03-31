package com.example.weatherapp

import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Main
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRemoteDataSource : RemoteDataSource {
    private val forecastData = mutableMapOf<Pair<Double, Double>, WeatherForecastResponse>()


    override suspend fun get5DaysWeatherForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherForecastResponse> {
        println("Fetching forecast for lat: $lat, lon: $lon")
        val response = forecastData[Pair(lat, lon)] ?: WeatherForecastResponse(
            cod = "200",
            message = 0,
            cnt = 0,
            list = emptyList(),
            city = City(
                id = 0,
                address = "Unknown",
                lat = 0.0,
                lon = 0.0,
                weatherResponse = WeatherResponse(
                    main = null,
                    weather = null,
                    wind = null,
                    clouds = null,
                    sys = null,
                    name = "Unknown",
                    dt_txt = null,
                    visibility = null
                )))


        println("Returned forecast list size: ${response.list.size}")
        return flow { emit(response) }
    }

    fun addForecast(lat: Double, lon: Double, forecastResponse: WeatherForecastResponse) {
            println("Adding forecast for lat: $lat, lon: $lon")
            forecastData[Pair(lat, lon)] = forecastResponse
        }



    // Stub implementation for required function
    override suspend fun getInfoFromLatLonAndUnitAndLang(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherResponse> = flow { emit(WeatherResponse(null, null, null, null, null, "", "", 0)) }
}