package com.example.weatherapp.Utils

import android.app.Application
import android.content.Context

class MyAppContext : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
    }
}

object AppContext {
    private lateinit var context: Context
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    fun getContext(): Context = context
}