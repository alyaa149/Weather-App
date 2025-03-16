package com.example.weatherapp.data.models

data class WeatherForecastResponse(  val cod: String,
                                     val message: Int,
                                     val cnt: Int,
                                     val list: List<WeatherResponse>,
                                     val city: City)
