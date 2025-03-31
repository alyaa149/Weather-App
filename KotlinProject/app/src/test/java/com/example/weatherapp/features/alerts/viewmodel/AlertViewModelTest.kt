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
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import java.time.LocalDateTime


@ExperimentalCoroutinesApi
class AlertViewModelTest {

    private lateinit var viewModel: AlertViewModel
    private val repo: Repo = mockk(relaxed = true)
    private val workManager: WorkManager = mockk(relaxed = true) // Mock WorkManager
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val mockContext: Context = mockk(relaxed = true)
        viewModel = AlertViewModel(repo, workManager, mockContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `deleteAlert removes reminder and updates state`() = runTest {
        val reminder = Reminder(id = 1, time = LocalDateTime.now().plusDays(1), type = "NOTIFICATION")

        coEvery { repo.deleteReminder(reminder.id) } just Runs
        coEvery { repo.getAllReminders() } returns flowOf(emptyList())

        val snackbarHostState = SnackbarHostState()
        val coroutineScope = CoroutineScope(testDispatcher)

        viewModel.deleteAlert(reminder, snackbarHostState, coroutineScope)
        advanceUntilIdle()

        // ✅ Add timeout to ensure coroutine execution
        coVerify(timeout = 5000) { repo.deleteReminder(reminder.id) }

        assert(viewModel.reminders.value is Response.Success)
        assert((viewModel.reminders.value as Response.Success).data.isEmpty())
    }


    @Test
    fun `deleteAlert emits Failure when repository throws exception`() = runTest {
        val reminder = Reminder(id = 2, time = LocalDateTime.now().plusDays(2), type = "NOTIFICATION")

        coEvery { repo.deleteReminder(reminder.id) } throws Exception("Delete failed")

        val snackbarHostState = SnackbarHostState()
        val coroutineScope = CoroutineScope(testDispatcher)

        viewModel.deleteAlert(reminder, snackbarHostState, coroutineScope)
        advanceUntilIdle()

        assert(viewModel.reminders.value is Response.Failure)
    }

    @Test
    fun `deleteAlert shows snackbar and restores reminder on undo`() = runTest {
        val reminder = Reminder(id = 3, time = LocalDateTime.now().plusDays(3), type = "NOTIFICATION")

        coEvery { repo.deleteReminder(reminder.id) } just Runs
        coEvery { repo.insertReminder(reminder) } returns 1

        val snackbarHostState = SnackbarHostState()
        val coroutineScope = CoroutineScope(testDispatcher)

        coroutineScope.launch {
            snackbarHostState.showSnackbar("Reminder deleted", "Undo", duration = SnackbarDuration.Short)
        }.join() // Ensures snackbar is processed before continuing

        viewModel.deleteAlert(reminder, snackbarHostState, coroutineScope)
        advanceUntilIdle()

        coVerify { repo.deleteReminder(reminder.id) }
        coVerify { repo.insertReminder(reminder) }
    }
}
