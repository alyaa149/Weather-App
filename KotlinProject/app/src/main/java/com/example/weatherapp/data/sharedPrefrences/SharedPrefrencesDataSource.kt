package com.example.weatherapp.data.sharedPrefrences

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesDataSource private constructor() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    companion object {
        @Volatile
        private var instance: SharedPreferencesDataSource? = null

        fun getInstance(): SharedPreferencesDataSource {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesDataSource().also { instance = it }
            }
        }
    }

    fun init(context: Context) {
        if (!this::sharedPreferences.isInitialized) { // Prevent re-initialization
            sharedPreferences = context.getSharedPreferences("WeatherApp", Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
        }
    }

    fun saveData(key: String, value: String) {
        if (this::editor.isInitialized) {
            editor.putString(key, value)
            editor.apply()
        }
    }

    fun getData(key: String): String? {
        return if (this::sharedPreferences.isInitialized) {
            sharedPreferences.getString(key, null)
        } else {
            null
        }
    }

    fun clearData() {
        if (this::editor.isInitialized) {
            editor.clear()
            editor.apply() // Ensure changes are saved
        }
    }
}
