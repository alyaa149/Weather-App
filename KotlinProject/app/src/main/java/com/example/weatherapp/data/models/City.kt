package com.example.weatherapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weather_database")
data class City(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val address: String,
    val lat: Double,
    val lon: Double,
)

