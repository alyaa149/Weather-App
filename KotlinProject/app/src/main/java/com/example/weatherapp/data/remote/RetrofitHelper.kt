package com.example.weatherapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {
    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: WeatherService by lazy {
        retrofitInstance.create(WeatherService::class.java)
    }
}
