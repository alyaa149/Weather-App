package com.example.weatherapp.data.local

import android.util.Log
import com.example.weatherapp.data.models.City
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val weatherDao: WeatherDao) : LocalDataSource  {

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
}