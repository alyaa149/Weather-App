package com.example.weatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.data.models.City


@Database(entities = [City::class], version = 2)
abstract class WeatherDataBase : RoomDatabase()  {
    abstract fun getWeatherDao(): WeatherDao
    companion object {
        const val DATABASE_NAME = "weather_database"
        @Volatile
        private var instance: WeatherDataBase? = null
        fun getInstance(context: Context): WeatherDataBase {
            return instance ?: synchronized(this){
                val INSTANCE = Room.databaseBuilder(context, WeatherDataBase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration().build()
                instance = INSTANCE
                INSTANCE
            }
        }

    }
}