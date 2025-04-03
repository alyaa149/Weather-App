package com.example.weatherapp.features.alerts.viewmodel

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.work.WorkManager
import com.example.weatherapp.Response
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.repo.Repo
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import java.time.LocalDateTime


@ExperimentalCoroutinesApi
class AlertViewModelTest {

    private lateinit var viewModel: AlertViewModel
    private val repo: Repo = mockk(relaxed = true)
    private val workManager: WorkManager = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    val snackbarHostState = SnackbarHostState()
    val coroutineScope = CoroutineScope(testDispatcher)
    @Before
    fun setUp() {
        val mockContext: Context = mockk(relaxed = true)
        viewModel = AlertViewModel(repo, workManager, mockContext)
    }



    @Test
    fun addAlert_insertAlert_reminderInsertedSuccessfully() = runTest {
        // Given: A reminder to insert
        val reminder = Reminder(id = 3, time = LocalDateTime.now().plusDays(3), type = "NOTIFICATION")
        // When: Insert the reminder
        viewModel.addAlert(reminder, snackbarHostState, coroutineScope)
        viewModel.fetchAlerts()
        val result =viewModel.reminders.value
        // Then: Verify the reminder is inserted
        assertThat(result, not(nullValue()))

    }
}
