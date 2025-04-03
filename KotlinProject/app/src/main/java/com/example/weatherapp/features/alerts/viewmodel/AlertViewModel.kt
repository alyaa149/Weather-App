package com.example.weatherapp.features.alerts.viewmodel

import android.app.AlarmManager
import android.app.NotificationManager
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
import androidx.work.await
import androidx.work.workDataOf
import com.example.weatherapp.R
import com.example.weatherapp.Response
import com.example.weatherapp.Utils.AppContext
import com.example.weatherapp.Utils.MyAppContext
import com.example.weatherapp.Utils.fetchCurrentTime
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.repo.Repo
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.features.alerts.notificationnsandalerts.NotificationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class AlertViewModel(private val repo: Repo,private val workManager: WorkManager,private val context: Context) : ViewModel() {
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
                        _eventFlow.emit(
                            context.getString(
                                R.string.expired_reminders_cleared,
                            ))
                    }

                    _reminders.value = Response.Success(active)
                }
            } catch (e: Exception) {
                _reminders.value = Response.Failure(e)
                Log.e("response", "Fetch error: ${e.message}")
                _eventFlow.emit(
                    context.getString(
                        R.string.error_fetching_reminders,
                        e.localizedMessage
                    ))
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

                // Update state optimistically first
                val currentList = (_reminders.value as? Response.Success)?.data ?: emptyList()
                _reminders.value = Response.Success(currentList + reminder)

                // Then refresh from source
                fetchAlerts()

                val snackBarResult = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.reminder_added),
                            actionLabel = context.getString(R.string.undo),
                            duration = SnackbarDuration.Short
                        )

                        if (snackBarResult == SnackbarResult.ActionPerformed) {
                            deleteAlert(reminder, snackbarHostState, coroutineScope)
                        }
                        if (reminder.type == "NOTIFICATION") {
                            scheduleNotification(reminder)
                        } else {
                         //   scheduler.scheduleWeatherAlert(reminder)
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
        viewModelScope.launch {
            try {
                workManager.cancelAllWorkByTag("reminder_${reminder.id}").await()
                Log.d("response", "Work cancelled for reminder ${reminder.id}")

                val notificationId = reminder.id.hashCode()
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
                notificationManager.cancel(notificationId)
                Log.d("response", "Notification cancelled with ID: $notificationId")

                withContext(Dispatchers.IO) {
                    repo.deleteReminder(reminder.id)
                    fetchAlerts()
                }

                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Reminder deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        scheduleNotification(reminder)
                    }
                }

            } catch (e: Exception) {
                Log.e("response", "Error deleting reminder: ${e.message}")
                _reminders.value = Response.Failure(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleNotification(reminder: Reminder) {
        val now = LocalDateTime.now()
        val duration = Duration.between(now, reminder.time).toMillis()

        if (duration <= 0) {
            Log.e("response", "Scheduled time is in the past. Skipping notification.")
            return
        }

        val notificationId = reminder.id.hashCode()

        val data = workDataOf(
            "title" to context.getString(R.string.weather_alert),
            "notification_id" to notificationId
        )

        val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .addTag("reminder_${reminder.id}")
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        workManager.enqueue(notificationRequest)
        Log.i("response", "Notification scheduled with ID: $notificationId")
    }

}






class AlertViewModelFactory(private val repo: RepoImpl,private val workManager: WorkManager,private val context: Context) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(repo,workManager,context) as T
    }
}