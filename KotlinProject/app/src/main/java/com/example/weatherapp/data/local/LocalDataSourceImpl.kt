package com.example.weatherapp.data.local

import android.util.Log
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.models.WeatherResponse
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
    override fun getWeatherFromLatLonOffline(lat: Double, lon: Double): Flow<City> {
        return weatherDao.getWeatherFromLatLonOffline(lat, lon)
    }

    override suspend fun updateWeather(lat: Double, lon: Double, weatherResponse: WeatherResponse) {
        return weatherDao.updateWeather(lat, lon, weatherResponse)
    }
    //Reminder
    override suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }

    override fun getAllReminders(): Flow<List<Reminder>> {
return reminderDao.getAllReminders()    }

    override suspend fun deleteReminder(reminderId: Int) {
return reminderDao.deleteReminder(reminderId)    }


}