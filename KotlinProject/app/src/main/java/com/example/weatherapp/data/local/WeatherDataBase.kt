package com.example.weatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.Utils.converters.Converters
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.models.WeatherInHomeUsingRoom


@Database(entities = [City::class, Reminder::class, WeatherInHomeUsingRoom::class], version = 8)
@TypeConverters(Converters::class)
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