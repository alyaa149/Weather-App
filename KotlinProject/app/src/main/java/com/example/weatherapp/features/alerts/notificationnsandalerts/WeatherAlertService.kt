package com.example.weatherapp.features.alerts.notificationnsandalerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import com.example.weatherapp.R
import com.example.weatherapp.features.alerts.view.WeatherAlertOverlay

class WeatherAlertService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var alertView: ComposeView
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("response", "Weather alert service started")
        return super.onStartCommand(intent, flags, startId)
    }

}