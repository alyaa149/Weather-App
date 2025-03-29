package com.example.weatherapp.features.alerts.notificationnsandalerts

import com.example.weatherapp.Utils.AppContext
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

class AlarmManager() {
    private val  alarmManager = AppContext.getContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val pendingIntent = PendingIntent.getBroadcast(
AppContext.getContext(),
        1,
        Intent(AppContext.getContext(), WeatherAlertReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )
    fun scheduleAlarm(){
        val fiveSeconds =5000
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            SystemClock.elapsedRealtime() + fiveSeconds,
            pendingIntent)

//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            SystemClock.elapsedRealtime() + fiveSeconds,
//            1000*60,
//            pendingIntent
//        )
    }
    fun cancelAlarm(){
        val pendingIntent = PendingIntent.getBroadcast(
            AppContext.getContext(),
            1,
            Intent(AppContext.getContext(), WeatherAlertReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}