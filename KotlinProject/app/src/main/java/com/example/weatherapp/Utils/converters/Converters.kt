package com.example.weatherapp.Utils.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherInHomeUsingRoom
import com.example.weatherapp.data.models.WeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
    private val gson = Gson()
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
    @TypeConverter
    fun fromWeatherResponse(weatherResponse: WeatherResponse): String {
        return gson.toJson(weatherResponse)
    }
    @TypeConverter
    fun toWeatherResponse(weatherResponseString: String): WeatherResponse {
        return gson.fromJson(weatherResponseString, WeatherResponse::class.java)
    }


    @TypeConverter
    fun fromWeatherForecastResponse(weatherForecastResponse: WeatherForecastResponse?): String? {
        return gson.toJson(weatherForecastResponse)
    }

    @TypeConverter
    fun toWeatherForecastResponse(weatherForecastResponseString: String?): WeatherForecastResponse? {
        return weatherForecastResponseString?.let {
            gson.fromJson(it, object : TypeToken<WeatherForecastResponse?>() {}.type)
        }
    }
}
