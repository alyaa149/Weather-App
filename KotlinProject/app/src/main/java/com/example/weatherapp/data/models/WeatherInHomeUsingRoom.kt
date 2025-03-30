package com.example.weatherapp.data.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherapp.Utils.converters.Converters

@Entity(
    tableName = "all_weather_details_table",
    primaryKeys = ["lat", "lon"],  // Composite primary key
    indices = [Index(value = ["lat", "lon"], unique = true)]
)
@TypeConverters(Converters::class)
data class WeatherInHomeUsingRoom(
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    var weatherResponse: WeatherResponse? = null,
    var watherForecast: WeatherForecastResponse? = null,

)