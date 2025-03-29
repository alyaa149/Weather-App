package com.example.weatherapp.features.alerts.notificationnsandalerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class WeatherAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("response", "Weather alert received")
        val serviceIntent = Intent(context, WeatherAlertService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent) // For newer Android versions
        } else {
            context.startService(serviceIntent)
        }
    }
}

