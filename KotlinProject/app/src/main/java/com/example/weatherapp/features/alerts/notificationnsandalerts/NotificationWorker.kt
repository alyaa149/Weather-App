package com.example.weatherapp.features.alerts.notificationnsandalerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.fetchCurrentTime
import com.example.weatherapp.Utils.getDrawableResourceId
import com.example.weatherapp.Utils.getUnit
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.local.WeatherDataBase
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.RepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class NotificationWorker (val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params){
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
         val _currentDetails = MutableStateFlow<WeatherResponse?>(null)
         val currentDetails: StateFlow<WeatherResponse?> = _currentDetails
        Log.i("response", "Worker started")
        return try {
            if (isStopped) {
                return Result.failure()
            }
            val repo =
                RepoImpl(
                    RemoteDataSourceImpl(RetrofitHelper.service),
                    LocalDataSourceImpl(
                        WeatherDataBase.getInstance(context).getWeatherDao(),
                        WeatherDataBase.getInstance(context).getReminderDao()
                    ),
                )
            try {
                repo.fetchWeatherFromLatLonUnitLang(
                    sharedPreferencesUtils.getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0,
                    sharedPreferencesUtils.getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0,
                    sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) ?: "metric",
                    sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) ?: "en"
                ).collect { response ->
                    _currentDetails.value = response
                    Log.i("AlertViewModel", "Weather details updated: $response")
                }
            } catch (e: Exception) {
                Log.e("AlertViewModel", "Error fetching weather: ${e.message}")
            }

            val pic = currentDetails.value?.weather?.get(0)?.main ?: "Clear"
            val title = inputData.getString("title") ?: "Weather Alert"
            val message = " the wind is ${currentDetails.value?.wind?.speed  ?: "0"} ${sharedPreferencesUtils.getData(AppStrings().WINDUNITKEY)} and the temperature is ${currentDetails.value?.main?.temp ?: "0"}°${getUnit()}"

            showNotification(pic, title, message)
            Log.i("response", "Received data in managerrr: $title - $message ${fetchCurrentTime()}")
            Result.success()
        }catch (e: Exception) {
            Log.e("response", "Error showing notification: ${e.message}")
            Result.failure()

        }
    }

    private fun showNotification(pic:String?, title: String, message: String) {
        val channelId = "weather_alert_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(getDrawableResourceId(pic))
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
        Log.i("response", "Notification sent successfully!")

    }

}