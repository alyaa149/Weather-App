package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.models.City
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeather(weather: City):Long
   // @Query("SELECT * FROM weather_database WHERE cityName = :cityName")
   @Query("SELECT * FROM weather_database")
     fun getAllFavs(): Flow<List<City>>
    @Delete
    suspend fun deleteWeather(weather: City):Int


}