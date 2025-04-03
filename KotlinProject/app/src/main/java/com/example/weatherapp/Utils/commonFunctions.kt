package com.example.weatherapp.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Address
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.google.android.gms.maps.model.LatLng
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun fetchCurrentTime(): String {
    val currentDateTime = LocalDateTime.now()
  //  val formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val currentDay: DayOfWeek = currentDateTime.dayOfWeek
    val dayName = currentDay.name.lowercase().replaceFirstChar { it.uppercase() }
    return dayName
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
    val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a")

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
fun getUnit(): String {
    return when (sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY)) {
        AppStrings().CELSIUSKEY -> if (sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) == AppStrings().ARABICKEY) "س" else AppContext.getContext().getString(R.string.c)
        AppStrings().FAHRENHEITKEY -> if (sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) == AppStrings().ARABICKEY) "ف" else AppContext.getContext().getString(R.string.f)
        else -> if (sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) == AppStrings().ARABICKEY) "ك" else AppContext.getContext().getString(R.string.k)
    }
}



 fun Address.toLatLng() = LatLng(latitude, longitude)
 fun Address.getFullAddress(): String {
    return (0..maxAddressLineIndex).joinToString { getAddressLine(it) }
}


fun restartActivity() {
    val intent = Intent(AppContext.getContext(), MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    AppContext.getContext().startActivity(intent)
    (AppContext.getContext() as? Activity)?.finish()
}




fun convertToArabicNumbers(number: String): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
}
@RequiresApi(Build.VERSION_CODES.O)
fun getTheDayOfTheWeek(dateString: String?): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val outputFormatter = DateTimeFormatter.ofPattern("EEEE")
    val date = LocalDateTime.parse(dateString, inputFormatter).toLocalDate()
    return outputFormatter.format(date)
}
fun formatNumberBasedOnLanguage(number: String): String {
    val language = sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) ?: "en"

    return if (language == "ar") {
        convertToArabicNumbers(number)
    } else {
        number
    }
}

