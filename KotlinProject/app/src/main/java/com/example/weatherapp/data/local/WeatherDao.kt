package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherInHomeUsingRoom
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: City):Long
   // @Query("SELECT * FROM weather_database WHERE cityName = :cityName")
   @Query("SELECT * FROM weather_database")
     fun getAllFavs(): Flow<List<City>>
    @Delete
    suspend fun deleteWeather(weather: City):Int
    @Query("SELECT * FROM weather_database WHERE lat = :lat AND lon = :lon")
    fun getWeatherFromLatLonOffline(lat: Double, lon: Double): Flow<City>
    @Query("UPDATE weather_database SET weatherResponse = :weatherResponse WHERE lat = :lat AND lon = :lon")
    suspend fun updateWeather(lat: Double, lon: Double, weatherResponse: WeatherResponse)
    @Query("SELECT * FROM all_weather_details_table WHERE lat = :lat AND lon = :lon")
    fun getAllDetailsWeatherFromLatLonHome(lat: Double, lon: Double): Flow<WeatherInHomeUsingRoom?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherHome(weather: WeatherInHomeUsingRoom): Long

    @Query("UPDATE all_weather_details_table SET weatherResponse = :weatherResponse, watherForecast = :weatherForecast WHERE lat = :lat AND lon = :lon")
    suspend fun updateWeatherHome(
        lat: Double,
        lon: Double,
        weatherResponse: String?,
        weatherForecast: String?
    )
}