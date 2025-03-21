package com.example.weatherapp.Utils.sharedprefrences

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.Utils.AppContext
import com.example.weatherapp.Utils.constants.AppStrings

class WeatherSharedPrefrences() {

    private val sharedPreferences: SharedPreferences =  AppContext.getContext().getSharedPreferences(
        AppStrings().SHARED_PREFRENCES_KEY, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun putData(key: String,value:String){
        editor.putString(key,value)
        editor.apply()
    }
    fun getData(key:String):String?{
        return sharedPreferences.getString(key,null)
    }
    fun clearData(){
        editor.clear()
        editor.apply()
    }

}