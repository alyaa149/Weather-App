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
        ///lazy-->  Without lazy, retrofitInstance would be initialized
        // as soon as RetrofitHelper is loaded, even if it's never used.
        // With lazy, it is only created when service is accessed for the first time.
        retrofitInstance.create(WeatherService::class.java)
    }
}
