package com.example.weatherapp.data.models

import java.util.Date


data class WeatherResponse(val main: Main?,
                           val weather: List<Weather>?,
                           val wind: Wind?,
                           val clouds: Clouds?,
                           val sys: Sys?,
                           val name: String?,
                          val dt_txt
                           :String?,
                         val visibility:Int?,
)




