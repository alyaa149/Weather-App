package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.WeatherForecastResponse
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
    //https://api.openweathermap.org/data/2.5/weather?q=London&appid=a48ab7f2ea1db8788b4a980035313863&units=metric&lang=ar


    //api.openweathermap.org/data/2.5/weather?lat=31.27077&lon=30.007815&appid=a48ab7f2ea1db8788b4a980035313863
    @GET("weather")
    suspend fun getInfoFromLatLonAndUnitAndLang(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): WeatherResponse


    //api.openweathermap.org/data/2.5/forecast?lat=44.34&lon=10.99&appid=a48ab7f2ea1db8788b4a980035313863&units=metric&lang=ar
    @GET("forecast")
    suspend fun get5DaysWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): WeatherForecastResponse





}