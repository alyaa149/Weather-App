package com.example.weatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Reminder


@Database(entities = [City::class, Reminder::class], version = 3)
abstract class WeatherDataBase : RoomDatabase()  {
    abstract fun getWeatherDao(): WeatherDao
    abstract fun getReminderDao(): ReminderDao
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