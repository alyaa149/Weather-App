package com.example.weatherapp.features.home.ViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Response
import com.example.weatherapp.Utils.NetworkUtils
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.fetchCurrentTime
import com.example.weatherapp.Utils.fetchformattedDateTime
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.repo.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class DetailsViewModel(private val repo: Repo) : ViewModel() {

    private val _currentDetails = MutableStateFlow<Response<WeatherResponse>>(Response.Loading)
    val currentDetails: StateFlow<Response<WeatherResponse>> = _currentDetails
    @RequiresApi(Build.VERSION_CODES.O)
    val currentDate= fetchCurrentTime()
    @RequiresApi(Build.VERSION_CODES.O)
    val currentTime= fetchformattedDateTime()


    private val _nextHoursDetailsList = MutableStateFlow<Response<List<WeatherResponse>>>(Response.Loading)
    val nextHoursDetailsList: StateFlow<Response<List<WeatherResponse>>> = _nextHoursDetailsList

    private val _futureDaysList = MutableStateFlow<Response<List<WeatherResponse>>>(Response.Loading)
    val futureDaysList: StateFlow<Response<List<WeatherResponse>>> = _futureDaysList

    private val _futureDays =MutableStateFlow<Response<List<WeatherResponse>>>(Response.Loading)
    val futureDays: StateFlow<Response<List<WeatherResponse>>> = _futureDays


    @RequiresApi(Build.VERSION_CODES.O)
     fun getFutureDaysWeatherForecast(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _futureDays.value = Response.Loading
            try {
                repo.get5DaysWeatherForecast(lat, lon,sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) ?: "metric" ,sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) ?: "en")
                    .collect { response ->
                        val currentDateTime = fetchformattedDateTime()
                        val currentDate = LocalDateTime.parse(currentDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val futureForecasts = response.list.filter { forecastItem ->
                            try {
                                val forecastDateTime = LocalDateTime.parse(forecastItem.dt_txt, formatter)
                                forecastDateTime.toLocalDate().isAfter(currentDate)  // Keep only future days
                            } catch (e: Exception) {
                                Log.e("ParsingError", "Failed to parse date: ${forecastItem.dt_txt}")
                                false
                            }
                        }
//                        groupedByDay = {
//                            "2025-11-10": [
//                            {"dt_txt": "2025-11-10 00:00:00", "temp": 15},
//                            {"dt_txt": "2025-11-10 03:00:00", "temp": 16},
//                            {"dt_txt": "2025-11-10 06:00:00", "temp": 17}
//                            ],
//                            "2025-11-11": [
//                            {"dt_txt": "2025-11-11 00:00:00", "temp": 14},
//                            {"dt_txt": "2025-11-11 03:00:00", "temp": 15}
//                            ],
//                            "2025-11-12": [
//                            {"dt_txt": "2025-11-12 00:00:00", "temp": 13},
//                            {"dt_txt": "2025-11-12 03:00:00", "temp": 14}
//                            ]
//                        }
                        val groupedByDay = futureForecasts.groupBy { forecastItem ->
                            forecastItem.dt_txt?.substring(0, 10) // Extract "yyyy-MM-dd"
                        }

                        val futureDaysList = groupedByDay.mapNotNull { (_, forecasts) ->
                            val maxTemp = forecasts.mapNotNull { it.main?.temp_max }.maxOrNull() ?: Double.MIN_VALUE
                            val minTemp = forecasts.mapNotNull { it.main?.temp_min }.minOrNull() ?: Double.MAX_VALUE
                            val firstForecast = forecasts.firstOrNull() ?: return@mapNotNull null
                            val firstMain = firstForecast.main ?: return@mapNotNull null

                            firstForecast.copy(
                                main = firstMain.copy(
                                    temp_max = maxTemp,
                                    temp_min = minTemp
                                )
                            )
                        }

                        _futureDays.value = Response.Success(futureDaysList)
                        Log.i("response", "futureDaysList: $futureDaysList")

                    }
            } catch (e: Exception) {
                _futureDays.value = Response.Failure(e)
                Log.e("WeatherError", e.message.toString())
            }
        }
    }



     fun getFutureWeatherForecast(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _nextHoursDetailsList.value = Response.Loading
            try {
                repo.get5DaysWeatherForecast(lat,lon,sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) ?: "metric" ,sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) ?: "en")
                    .collect { response ->

                        _nextHoursDetailsList.value = Response.Success(response.list)
                        Log.i("response", response.list.toString())
                    }
            } catch (e: Exception) {
                _nextHoursDetailsList.value = Response.Failure(e)
                Log.e("WeatherError", e.message.toString())
            }
        }
    }

     fun fetchWeatherFromLatLonUnitLang(lat: Double, lon: Double, ) {
         viewModelScope.launch(Dispatchers.IO) {
             _currentDetails.value = Response.Loading
             try {
                 if (NetworkUtils.isNetworkAvailable()) {
                     repo.fetchWeatherFromLatLonUnitLang(
                         lat, lon,
                         sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) ?: "metric",
                         sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) ?: "en"
                     ).collect { response ->
                         _currentDetails.value = Response.Success(response)
                       //  repo.updateWeather(lat, lon, response)
                     }
                 } else {
                    repo.getWeatherFromLatLonOffline(lat, lon)
                        .collect{
                            _currentDetails.value = Response.Success(it.weatherResponse)
                        }
                 }
             } catch (e: Exception) {
                 _currentDetails.value = Response.Failure(e)
             }
         }
    }




}


class DetailsViewModelFactory(private val repo: Repo) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailsViewModel(repo) as T
    }
}


