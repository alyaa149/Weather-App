package com.example.weatherapp.features.alerts.viewmodel

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Response
import com.example.weatherapp.data.models.Reminder
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertViewModel(private val repo: Repo): ViewModel() {
    private val _reminders = MutableStateFlow<Response<List<Reminder>>>(Response.Loading)
    val reminders: StateFlow<Response<List<Reminder>>> = _reminders.asStateFlow()
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        fetchAlerts()
    }

     private fun fetchAlerts(){
        viewModelScope.launch(Dispatchers.IO) {
            _reminders.value = Response.Loading
            try {
                repo.getAllReminders().collect { remindersList ->
                    _reminders.value = Response.Success(remindersList)
                }
            } catch (e: Exception) {
                _reminders.value = Response.Failure(e)
                Log.e("response", "Fetch error: ${e.message}")
            }
        }
    }
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
                    }
                }

            } catch (e: Exception) {
                Log.e("response", "Insert error: ${e.message}")
            }
        }
    }

    fun deleteAlert(reminder: Reminder, snackbarHostState: SnackbarHostState, coroutineScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
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

}
class AlertViewModelFactory(private val repo: RepoImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(repo) as T
    }
}