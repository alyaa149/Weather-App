package com.example.weatherapp.features.alerts.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.Blue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.R
import com.example.weatherapp.Response
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.formatTime
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.features.alerts.viewmodel.AlertViewModel
import com.example.weatherapp.features.favorites.view.LocationItem
import com.example.weatherapp.features.home.View.LoadingIndicator
import com.example.weatherapp.ui.theme.Roze
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlertsScreen(viewModel: AlertViewModel) {
    var isSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val eventFlow = viewModel.eventFlow
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        eventFlow.collect {
            message ->
                snackbarHostState.showSnackbar(message)
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                onClick = { isSheetOpen = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Reminder",
                    tint = Blue
                )
            }
        } ,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val reminders = viewModel.reminders.collectAsState().value) {
                is Response.Loading -> {
                    LoadingIndicator()
                }
                is Response.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(reminders.data) { reminder ->  // Safe access to .data
                            AlertItem(
                                reminder = reminder,
                                onRemove = { viewModel.deleteAlert(reminder, snackbarHostState, coroutineScope) },
                            )
                        }
                    }
                }
                is Response.Failure -> {
                    Text(text = "Error: ${reminders.message}")
                }
            }

            if (isSheetOpen) {
                ModalBottomSheet(
                    onDismissRequest = { isSheetOpen = false },
                    sheetState = sheetState
                ) {
                    PopUpAlert(
                        sheetState = sheetState,
                        onDismiss = { isSheetOpen = false },
                        onConfirm = { time, type ->
                            val reminder = Reminder(time = time, type = type)
                            viewModel.addAlert(reminder, snackbarHostState, coroutineScope)
                            Log.i("response", "Calling addAlert with time outside if: $time, type: $type")
                            isSheetOpen = false
                        }
                    )
                }
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlertItem(reminder: Reminder, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Roze
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 7.dp)
            ) {
                Text(
                    text = "${reminder.time.dayOfWeek.toString()} ${reminder.time.dayOfMonth}/${reminder.time.month}",
                    style = TextStyle(
                        color = Blue,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(start = 5.dp)
                )
                Text(
                    text = "${reminder.time.hour}:${reminder.time.minute}  ${reminder.type}",
                    style = TextStyle(
                        color = Blue,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                    ),
                    modifier = Modifier.padding(start = 17.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Blue)
            }
        }
    }


}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopUpAlert(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime, String) -> Unit
) {
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()

    var selectedDate by remember { mutableStateOf(currentDate) }
    var selectedTime by remember { mutableStateOf(currentTime) }
    var selectedReminderType by remember { mutableStateOf("ALARM") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val reminderTypes = listOf("ALARM", "NOTIFICATION")
    val snackbarHostState = remember { SnackbarHostState() }

    ModalBottomSheet(
        containerColor = Roze,
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Reminder Type",
                color = Blue,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 15.dp)
            )

            Column(Modifier.padding(start = 8.dp)) {
                reminderTypes.forEach { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReminderType = type }
                         //   .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = (type == selectedReminderType),
                            onClick = { selectedReminderType = type },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Blue,
                                unselectedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = type,
                            tint = if (type == selectedReminderType) Blue else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = type,
                            color = if (type == selectedReminderType) Blue else Color.Gray
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    DatePickerSection(
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it }
                    )
                }
                Spacer(modifier = Modifier.width(16.dp)) // Add space between date and time pickers
                Box(modifier = Modifier.weight(1f)) {
                    TimePickerSection(
                        selectedTime = selectedTime,
                        onTimeSelected = { newTime ->
                            if (selectedDate == currentDate && newTime.isBefore(currentTime)) {
                                errorMessage = "Please choose a future time"
                            } else {
                                selectedTime = newTime
                                errorMessage = null
                            }
                        }
                    )
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Button(
                onClick = {
                    val selectedDateTime = LocalDateTime.of(selectedDate, selectedTime)
                    if (selectedDate == currentDate && selectedTime.isBefore(currentTime)) {
                        errorMessage = "Please choose a future time"
                    } else {
                        onConfirm(selectedDateTime, selectedReminderType)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("Set Reminder")
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSection(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val today = LocalDate.now()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC)
            .toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectedDate =
                    Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                return !selectedDate.isBefore(today)
            }
        }
    )

    Column(modifier = Modifier.padding(5.dp).border(2.dp,Blue, shape = MaterialTheme.shapes.small).padding(10.dp)) {
        Text(
            text = "Date",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Blue
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
                .padding(vertical = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.calender),
                contentDescription = "Date icon",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp)
            )

            Text(
                color = Blue,
                text = selectedDate.toString(),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (showError) {
            Text(
                text = "Please select today or a future date",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
                showError = false
            },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        if (!selected.isBefore(today)) {
                            onDateSelected(selected)
                            showDatePicker = false
                            showError = false
                        } else {
                            showError = true
                        }
                    }
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerSection(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var displayedTime by remember { mutableStateOf(selectedTime.formatTime()) }

    Column(modifier = Modifier.padding(5.dp).border(2.dp,Blue, shape = MaterialTheme.shapes.small).padding(10.dp)) {
        Text(
            text = "Time",
            modifier = Modifier.padding(bottom = 4.dp),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Blue
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePicker = true }
                .padding(vertical = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.starttime),
                contentDescription = "Time icon",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp)
            )
            Text(
                color = Blue,
                text = displayedTime,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = selectedTime.hour,
                initialMinute = selectedTime.minute,
                is24Hour = false
            )

            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    Button(onClick = {
                        val newTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        onTimeSelected(newTime)
                        displayedTime = newTime.formatTime()
                        showTimePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }
    }
}

@Composable
fun WeatherAlertOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent background
            .clickable { onDismiss() }, // Close when clicked outside
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Weather Alert!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The wind is strong! Stay safe.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        }
    }
}


