package com.example.weatherapp.data.models



data class WeatherResponse(val main: Main?,
                           val weather: List<Weather>?,
                           val wind: Wind?,
                           val clouds: Clouds?,
                           val sys: Sys?,
                           val name: String?)
