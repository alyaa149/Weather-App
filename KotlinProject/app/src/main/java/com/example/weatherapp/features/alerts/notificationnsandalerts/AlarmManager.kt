package com.example.weatherapp.features.alerts.notificationnsandalerts

import com.example.weatherapp.Utils.AppContext
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weatherapp.data.models.Reminder
import java.time.ZoneId

class WeatherAlertScheduler {
    private val alarmManager =
        AppContext.getContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleWeatherAlert(reminder: Reminder) {
        val context = AppContext.getContext()

        // Convert LocalDateTime to milliseconds
        val timeInMillis = reminder.time
            .atZone(ZoneId.systemDefault()) // Convert to system's time zone
            .toInstant()
            .toEpochMilli()

        val intent = Intent(context, WeatherAlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id, // Unique ID
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (timeInMillis <= System.currentTimeMillis()) {
            Log.e("response", "Reminder time is in the past: $timeInMillis")
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )

        Log.d("response", "Weather alert scheduled at: $timeInMillis")
    }
}
