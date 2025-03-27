package com.example.weatherapp.features.alerts.view

import android.app.TimePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.weatherapp.R
import com.example.weatherapp.data.models.ReminderType
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
fun AlertsScreen() {
    var isSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                onClick = { isSheetOpen = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Location",
                    tint = Blue
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isSheetOpen) {
                PopUpAlert(sheetState, onDismiss = { isSheetOpen = false },   onConfirm = { time, type ->
                    Log.d("response", "Set $type at $time")
                })
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
    onConfirm: (LocalDateTime, ReminderType) -> Unit
) {
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()

    var selectedDate by remember { mutableStateOf(currentDate) }
    var selectedTime by remember { mutableStateOf(currentTime) }
    var selectedReminderType by remember { mutableStateOf(ReminderType.ALARM) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                modifier = Modifier.padding(bottom = 8.dp,start = 15.dp)
            )

            Column(Modifier.padding(start = 8.dp)) {
                ReminderType.values().forEach { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReminderType = type }
                            .padding(vertical = 8.dp)
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
                            imageVector = when (type) {
                                ReminderType.ALARM -> Icons.Default.Notifications
                                ReminderType.NOTIFICATION -> Icons.Default.Notifications
                            },
                            contentDescription = type.toString(),
                            tint = if (type == selectedReminderType) Blue else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = type.toString(),
                            color = if (type == selectedReminderType) Blue else Color.Gray
                        )
                    }
                }
            }

            // Date and Time Pickers
            DatePickerSection(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            Divider(
                color = Blue,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

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

            // Error message
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Confirm Button
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
                    .padding(top = 16.dp, bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("Set Reminder")
            }
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
    var showError by remember { mutableStateOf(false) } // For error message
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

    Column(modifier = Modifier.padding(16.dp)) {
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

    Column(modifier = Modifier.padding(16.dp)) {
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

@RequiresApi(Build.VERSION_CODES.O)
fun LocalTime.formatTime(): String {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    return this.format(formatter)
}
