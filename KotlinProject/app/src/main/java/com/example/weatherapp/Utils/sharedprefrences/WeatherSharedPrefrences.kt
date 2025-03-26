package com.example.weatherapp.Utils.sharedprefrences

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.Utils.constants.AppStrings

object sharedPreferencesUtils {

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(
            AppStrings().SHARED_PREFRENCES_KEY, Context.MODE_PRIVATE
        )
    }

    fun putData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }
}
