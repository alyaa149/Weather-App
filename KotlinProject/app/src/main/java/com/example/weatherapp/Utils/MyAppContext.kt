package com.example.weatherapp.Utils

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils

class MyAppContext : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
        sharedPreferencesUtils.init(this)
    }
}

object AppContext {
    private lateinit var context: Context
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    fun getContext(): Context = context
    fun updateContext(newContext: Context) {
        context = newContext
    }
}