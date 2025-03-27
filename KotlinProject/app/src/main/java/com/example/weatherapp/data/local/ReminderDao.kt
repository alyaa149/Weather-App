package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.models.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder):Long
    @Query("SELECT * FROM reminders")
    fun getAllReminders(): Flow<List<Reminder>>
    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteReminder(reminderId: Int)
}