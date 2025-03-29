package com.example.weatherapp.features.alerts.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapp.Response
import com.example.weatherapp.Utils.AppContext
import com.example.weatherapp.Utils.fetchCurrentTime
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.repo.Repo
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.features.alerts.notificationnsandalerts.NotificationWorker
import com.example.weatherapp.features.alerts.notificationnsandalerts.WeatherAlertReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class AlertViewModel(private val repo: Repo) : ViewModel() {
    private val _reminders = MutableStateFlow<Response<List<Reminder>>>(Response.Loading)
    val reminders: StateFlow<Response<List<Reminder>>> = _reminders.asStateFlow()
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        fetchAlerts()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            _reminders.value = Response.Loading
            try {
                repo.getAllReminders().collect { remindersList ->
                    val now = LocalDateTime.now()
                    val (expired, active) = remindersList.partition { it.time.isBefore(now) }

                    if (expired.isNotEmpty()) {
                        expired.forEach { expiredReminder ->
                            repo.deleteReminder(expiredReminder.id)
                            Log.i("response", "Deleted expired reminder: ${expiredReminder.id}")
                        }
                        _eventFlow.emit("${expired.size} expired reminders cleared")
                    }

                    _reminders.value = Response.Success(active)
                }
            } catch (e: Exception) {
                _reminders.value = Response.Failure(e)
                Log.e("response", "Fetch error: ${e.message}")
                _eventFlow.emit("Error fetching reminders: ${e.localizedMessage}")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlert(
        reminder: Reminder,
        snackbarHostState: SnackbarHostState,
        coroutineScope: CoroutineScope
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.insertReminder(reminder)
                Log.i("response", "Inserted reminder ID: $result")
                fetchAlerts()
                withContext(Dispatchers.Main) {
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Reminder added",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short
                        )

                        if (result == SnackbarResult.ActionPerformed) {
                            deleteAlert(reminder, snackbarHostState, coroutineScope)
                        }
                        if (reminder.type == "NOTIFICATION") {
                            scheduleNotification(reminder)
                        } else {
                            setWeatherAlert(
                                reminder.time.atZone(ZoneId.systemDefault()).toInstant()
                                    .toEpochMilli()
                            )

                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("response", "Insert error: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteAlert(
        reminder: Reminder,
        snackbarHostState: SnackbarHostState,
        coroutineScope: CoroutineScope
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                workManager.cancelAllWorkByTag("notification_worker_${reminder.id}").result.addListener(
                    { Log.d("response", "Cancellation completed for ${reminder.id}") },
                    { Log.e("response", "Cancellation failed for ${reminder.id}") }
                )
                repo.deleteReminder(reminder.id)
                Log.i("response", "Deleted reminder")

                fetchAlerts()

                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Reminder deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        addAlert(reminder, snackbarHostState, coroutineScope)
                    }

                }

            } catch (e: Exception) {
                _reminders.value = Response.Failure(e)
                Log.e("response", "Delete error: ${e.message}")
            }
        }
    }

    private val workManager = WorkManager.getInstance(AppContext.getContext())


    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleNotification(reminder: Reminder) {
        val now = LocalDateTime.now()
        val duration = Duration.between(now, reminder.time).toMillis()
        Log.i("AlertViewModel", "Scheduling notification: Duration = $duration ms")

        if (duration <= 0) {
            Log.e("AlertViewModel", "Scheduled time is in the past. Skipping notification.")
            return
        }
        val data = workDataOf(
            "title" to "Weather Alert",
        )
        Log.i("response", "Weather details: ${data.getString("message")}")

        val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .addTag("notification_worker_${reminder.id}")
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        workManager.enqueue(notificationRequest)
        Log.i("response", "Notification scheduled successfully. ${fetchCurrentTime()}")
    }
}

fun setWeatherAlert(triggerTime: Long) {
    val context = AppContext.getContext()
    val intent = Intent(context, WeatherAlertReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)

    Log.i("AlarmManager", "Weather alert set for: $triggerTime")
}




class AlertViewModelFactory(private val repo: RepoImpl) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(repo) as T
    }
}