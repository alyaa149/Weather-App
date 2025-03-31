package com.example.weatherapp.features.alerts.notificationnsandalerts

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
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
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.weatherapp.R
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.fetchCurrentTime
import com.example.weatherapp.WeatherAlert
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.local.WeatherDataBase
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.features.alerts.view.WeatherAlertScreen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//abstract class WeatherAlertService : Service(), LifecycleOwner {
//    private lateinit var windowManager: WindowManager
//    private lateinit var alertView: ComposeView
//    private val lifecycleRegistry = LifecycleRegistry(this)
//
//    override fun onCreate() {
//        super.onCreate()
//        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//
//        // Mark lifecycle as CREATED
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val weatherJson = intent?.getStringExtra("weather_data")
//        val weatherResponse: WeatherResponse? = weatherJson?.let {
//            Gson().fromJson(it, object : TypeToken<WeatherResponse>() {}.type)
//        }
//
//        if (weatherResponse != null) {
//            Log.d("response", "Weather data service received: $weatherResponse")
//            showWeatherAlert(weatherResponse)
//        } else {
//            Log.e("response", "Failed to parse weather data")
//            stopSelf()
//        }
//
//        return START_NOT_STICKY
//    }
//
//    private fun showWeatherAlert(weather: WeatherResponse) {
//        if (::alertView.isInitialized) {
//            windowManager.removeView(alertView)
//        }
//
//        alertView = ComposeView(this).apply {
//            setViewTreeLifecycleOwner(this@WeatherAlertService) // ✅ Attach LifecycleOwner
//            setContent {
//                WeatherAlertScreen(weather) {
//                    removeWeatherAlert()
//                }
//            }
//        }
//
//        val layoutParams = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//            PixelFormat.TRANSLUCENT
//        ).apply {
//            gravity = Gravity.TOP
//        }
//
//        windowManager.addView(alertView, layoutParams)
//
//        // Mark lifecycle as STARTED
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
//    }
//
//    private fun removeWeatherAlert() {
//        if (::alertView.isInitialized) {
//            windowManager.removeView(alertView)
//        }
//        stopSelf()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY) // Cleanup
//    }
//
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}
