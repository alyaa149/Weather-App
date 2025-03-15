package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("weather")
    suspend fun getInfoFromLatLonAndUnitAndLang(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ar"
    ): WeatherResponse
    //https://api.openweathermap.org/data/2.5/weather?q=London&appid=a48ab7f2ea1db8788b4a980035313863&units=metric&lang=ar
}