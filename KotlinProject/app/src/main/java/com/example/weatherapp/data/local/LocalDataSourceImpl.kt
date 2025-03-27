package com.example.weatherapp.data.local

import android.util.Log
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Reminder
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val weatherDao: WeatherDao, private val reminderDao: ReminderDao) : LocalDataSource  {

    override suspend fun insertWeather(city: City) :Long {
        Log.i("response", city.toString())
  return weatherDao.insertWeather(city)
    }
    override suspend fun deleteWeather(city: City):Int {
        return weatherDao.deleteWeather(city)
    }
    override fun getAllFavs(): Flow<List<City>> {
        return weatherDao.getAllFavs()
    }
    //Reminder
    override suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }

    override fun getAllReminders(): Flow<List<Reminder>> {
return reminderDao.getAllReminders()    }

    override suspend fun deleteReminder(reminder: Reminder) {
return reminderDao.deleteReminder(reminder)    }
}