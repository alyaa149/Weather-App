package com.example.weatherapp.Utils

import android.app.Application
import android.content.Context
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.sharedPrefrences.SharedPreferencesDataSource

class MyAppContext : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
        sharedPreferencesUtils.init(this)
        SharedPreferencesDataSource.getInstance().init(this)
    }
}

object AppContext {
    private lateinit var context: Context
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    fun getContext(): Context = context
}