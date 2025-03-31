package com.example.weatherapp.features.alerts.notificationnsandalerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.fetchCurrentTime
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.local.WeatherDataBase
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.RepoImpl
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//class WeatherAlertReceiver : BroadcastReceiver() {
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onReceive(context: Context, intent: Intent) {
//        Log.d("response", "Weather alert received in ${fetchCurrentTime()}")
//
//        val repo = RepoImpl(
//            RemoteDataSourceImpl(RetrofitHelper.service),
//            LocalDataSourceImpl(
//                WeatherDataBase.getInstance(context).getWeatherDao(),
//                WeatherDataBase.getInstance(context).getReminderDao()
//            ),
//        )
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val weatherResponse = repo.fetchWeatherFromLatLonUnitLang(
//                    sharedPreferencesUtils.getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0,
//                    sharedPreferencesUtils.getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0,
//                    sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) ?: "metric",
//                    sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) ?: "en"
//                ).first()
//                 Log.d("response", "data in receiver is :  $weatherResponse")
//                val serviceIntent = Intent(context, WeatherAlertService::class.java).apply {
//                    putExtra("weather_data", Gson().toJson(weatherResponse)) // Pass data as JSON
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    context.startForegroundService(serviceIntent)
//                } else {
//                    context.startService(serviceIntent)
//                }
//
//            } catch (e: Exception) {
//                Log.e("response", "Error fetching weather data: ${e.message}")
//            }
//        }
//    }
//}


