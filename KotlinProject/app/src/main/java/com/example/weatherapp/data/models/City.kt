package com.example.weatherapp.data.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherapp.Utils.converters.Converters


@Entity(
    tableName = "weather_database",
    indices = [Index(value = ["lat", "lon"], unique = true)]
)
@TypeConverters(Converters::class)
data class City(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val address: String,
    val lat: Double,
    val lon: Double,
    val weatherResponse: WeatherResponse
)

