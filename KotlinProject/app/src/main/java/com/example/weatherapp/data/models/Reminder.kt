package com.example.weatherapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time :LocalDateTime,
    val type: ReminderType,
)
enum class ReminderType {
    ALARM, NOTIFICATION
}