package com.example.weatherapp.features.alerts.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.R
import com.example.weatherapp.features.home.View.getDrawableResourceId


class NotificationWorker (val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {
        Log.i("NotificationWorker", "Worker started")
        return try {
            // Check if work was cancelled before proceeding
            if (isStopped) {
                return Result.failure()
            }

            val pic = inputData.getString("pic") ?: "Clear"
            val title = inputData.getString("title") ?: "Weather Alert"
            val message = inputData.getString("message") ?: "Check the weather updates!"

            showNotification(pic, title, message)
            Log.i("NotificationWorker", "Received data: $title - $message")
            Result.success()
        }catch (e: Exception) {
            Log.e("NotificationWorker", "Error showing notification: ${e.message}")
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
        Log.i("NotificationWorker", "Notification sent successfully!")  // Debugging log

    }

}