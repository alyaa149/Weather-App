package com.example.weatherapp.features.alerts.notificationnsandalerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weatherapp.R
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.fetchCurrentTime
import com.example.weatherapp.Utils.formatNumberBasedOnLanguage
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
import kotlinx.coroutines.flow.first


class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        Log.i("NotificationWorker", "Worker started with ID: $id")

        if (isStopped) {
            Log.i("NotificationWorker", "Worker stopped before execution")
            return Result.failure()
        }

        return try {
            val notificationId = inputData.getInt("notification_id", id.hashCode())
            val title = inputData.getString("title") ?: context.getString(R.string.weather_alert)

            val weatherResponse = fetchWeatherData()
            val weatherCondition = weatherResponse?.weather?.firstOrNull()?.main ?: "Clear"

            val message = buildWeatherMessage(weatherResponse)

            showNotification(
                id = notificationId,
                weatherCondition = weatherCondition,
                title = title,
                message = message
            )

            Log.i("NotificationWorker", "Notification shown successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error in doWork: ${e.message}")
            Result.failure()
        }
    }

    private suspend fun fetchWeatherData(): WeatherResponse? {
        return try {
            val repo = RepoImpl(
                RemoteDataSourceImpl(RetrofitHelper.service),
                LocalDataSourceImpl(
                    WeatherDataBase.getInstance(context).getWeatherDao(),
                    WeatherDataBase.getInstance(context).getReminderDao()
                )
            )

            repo.fetchWeatherFromLatLonUnitLang(
                lat = sharedPreferencesUtils.getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0,
                lon = sharedPreferencesUtils.getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0,
                units = sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) ?: "metric",
                lang = sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) ?: "en"
            ).first()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Weather fetch error: ${e.message}")
            null
        }
    }

    private fun buildWeatherMessage(weather: WeatherResponse?): String {
        return context.getString(
            R.string.the_wind_is_and_the_temperature_is,
            formatNumberBasedOnLanguage(weather?.wind?.speed.toString()),
            sharedPreferencesUtils.getData(AppStrings().WINDUNITKEY),
            formatNumberBasedOnLanguage(weather?.main?.temp.toString()),
            getUnit()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(
        id: Int,
        weatherCondition: String,
        title: String,
        message: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        // Create notification channel (required for Android O+)
        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(getDrawableResourceId(weatherCondition))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(id, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            "reminder_channel",
            "Weather Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Weather alert notifications"
            enableLights(true)
        }

        notificationManager.createNotificationChannel(channel)
    }


}