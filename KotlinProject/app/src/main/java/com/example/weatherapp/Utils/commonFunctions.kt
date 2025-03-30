package com.example.weatherapp.Utils

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherapp.R
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun fetchCurrentTime(): String {
    val currentDateTime = LocalDateTime.now()
    val formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val currentDay: DayOfWeek = currentDateTime.dayOfWeek
    val dayName = currentDay.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$dayName, $formattedDateTime"
}
@RequiresApi(Build.VERSION_CODES.O)
fun fetchformattedDateTime() : String{
    val currentDateTime = LocalDateTime.now()
    val formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    return formattedDateTime

}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(dtTxt: String?) :String? {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val outputFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val dateTime = LocalDateTime.parse(dtTxt, inputFormatter)
    return  dateTime.format(outputFormatter)

}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalTime.formatTime(): String {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    return this.format(formatter)
}
fun getDrawableResourceId(picPath: String?): Int {
    return when (picPath) {
        "Clear" -> R.drawable.sunny
        "Clouds" -> R.drawable.cloudy
        "Rain" -> R.drawable.rainy
        "Snow" -> R.drawable.snowy
        "storm" -> R.drawable.storm
        else -> R.drawable.back
    }
}
fun getUnit():String{
    if(sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) == AppStrings().CELSIUSKEY) {
        return "C"
    }
    else if(sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) == AppStrings().FAHRENHEITKEY){
        return "F"
    }
    else{
        return "K"
    }
}
fun setAppLocale( language: String) {
    val context = AppContext.getContext()
    val locale = Locale(language)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

