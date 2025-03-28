package com.example.weatherapp.features.alerts.viewmodel

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
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.repo.Repo
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.features.favorites.viewmodel.FavViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class AlertViewModel(private val repo: Repo): ViewModel() {
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
    fun addAlert(reminder: Reminder, snackbarHostState: SnackbarHostState, coroutineScope: CoroutineScope) {
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
                            scheduleNotification(reminder,
                                sharedPreferencesUtils.getData("LATITUDE") ?: "0.0",
                                sharedPreferencesUtils.getData("LONGITUDE") ?: "0.0")
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("response", "Insert error: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteAlert(reminder: Reminder, snackbarHostState: SnackbarHostState, coroutineScope: CoroutineScope) {
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
                        duration = SnackbarDuration.Long
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
    private val _currentDetails = MutableStateFlow<WeatherResponse?>(null)
    private val currentDetails: StateFlow<WeatherResponse?> = _currentDetails
    private fun getCurrentDetails(latitude: String, longitude: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.fetchWeatherFromLatLonUnitLang(latitude.toDouble(),longitude.toDouble(),
                    sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) ?: "metric" ,
                    sharedPreferencesUtils.getData(
                    AppStrings().LANGUAGEKEY) ?: "en"
                )
                    .collect { response ->
                        _currentDetails.value = response
                        Log.i("response","ellly 3ayzah ${response.toString()}")
                    }

            } catch (e: Exception) {
                Log.e("WeatherError", e.message.toString())
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleNotification(reminder: Reminder, latitude: String, longitude: String) {
        val now = LocalDateTime.now()
        val duration = Duration.between(now, reminder.time).toMillis()
        Log.i("response", "Current time: $now, Scheduled time: ${reminder.time}, Duration: $duration ms")

        if (duration <= 0) {
            Log.e("response", "Scheduled time is in the past. Notification won't be scheduled.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            getCurrentDetails(latitude, longitude)

            val details = currentDetails.filterNotNull().first()
            if (details != null) {
                val data = workDataOf(
                    "pic" to details.weather?.get(0)?.main,
                    "title" to "Weather Alert",
                    "message" to "The current weather is ${details.weather?.get(0)?.description}"
                )
                Log.i("response", "Weather details received: ${details.weather?.get(0)?.description}")
                val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .addTag("notification_worker_${reminder.id}")                    .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build()

                workManager.enqueue(notificationRequest)

                Log.i("response", "Notification Scheduled with WorkManager")

            } else {
                Log.e("response", "Failed to fetch weather details, notification not scheduled")
            }
        }
    }
}

class AlertViewModelFactory(private val repo: RepoImpl) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(repo) as T
    }
}