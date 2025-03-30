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
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherInHomeUsingRoom
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.repo.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)
class DetailsViewModel(private val repo: Repo) : ViewModel() {
    // State Flows
    private val _currentDetails = MutableStateFlow<Response<WeatherResponse>>(Response.Loading)
    val currentDetails: StateFlow<Response<WeatherResponse>> = _currentDetails

    private val _nextHoursDetails = MutableStateFlow<Response<List<WeatherResponse>>>(Response.Loading)
    val nextHoursDetails: StateFlow<Response<List<WeatherResponse>>> = _nextHoursDetails

    private val _futureDays = MutableStateFlow<Response<List<WeatherResponse>>>(Response.Loading)
    val futureDays: StateFlow<Response<List<WeatherResponse>>> = _futureDays

    // Utils
    @RequiresApi(Build.VERSION_CODES.O)
    val currentDate = fetchCurrentTime()

    @RequiresApi(Build.VERSION_CODES.O)
    val currentTime = fetchformattedDateTime()

    private val forecastProcessor = ForecastProcessor()
    private var lastLat: Double = 0.0
    private var lastLon: Double = 0.0

    fun loadWeatherData(lat: Double, lon: Double) {
        lastLat = lat
        lastLon = lon
        viewModelScope.launch {
            Log.d("WeatherLoad", "Starting load for lat:$lat lon:$lon")
            if (NetworkUtils.isNetworkAvailable()) {
                Log.d("Network", "Online mode - fetching fresh data")
                loadOnlineData(lat, lon)
            } else {
                Log.d("Network", "Offline mode - loading cached data")
                loadOfflineData(lat, lon)
            }
        }
    }

    private suspend fun loadOnlineData(lat: Double, lon: Double) {
        try {
            Log.d("Network", "Fetching current weather...")
            val current = repo.fetchWeatherFromLatLonUnitLang(
                lat, lon,
                getTempUnit(),
                getLanguage()
            ).first().also {
                Log.d("Network", "Current weather received")
            }

            _currentDetails.value = Response.Success(current)

            Log.d("Network", "Fetching forecast...")
            val forecast = repo.get5DaysWeatherForecast(
                lat, lon,
                getTempUnit(),
                getLanguage()
            ).first().also {
                Log.d("Network", "Forecast received")
            }

            val (nextHours, futureDays) = forecastProcessor.processForecast(forecast.list).also {
                Log.d("Processing", "Forecast processed")
            }

            _nextHoursDetails.value = Response.Success(nextHours)
            _futureDays.value = Response.Success(futureDays)

            saveWeatherData(lat, lon, current, forecast).also {
                Log.d("Database", "Data saved to local storage")
            }

        } catch (e: Exception) {
            Log.e("OnlineError", "Online fetch failed", e)
            // Only fallback if we haven't already tried offline
            if (_currentDetails.value !is Response.Success) {
                loadOfflineData(lat, lon)
            }
        }
    }

    private suspend fun loadOfflineData(lat: Double, lon: Double) {
        try {
            Log.d("Offline", "Loading cached data...")
            _currentDetails.value = Response.Loading
            _nextHoursDetails.value = Response.Loading
            _futureDays.value = Response.Loading

            val cached = withTimeoutOrNull(3000) {
                repo.getAllDetailsWeatherFromLatLonHome(lat, lon).first()
            } ?: throw Exception("Database timeout")

            if (cached == null) {
                throw Exception("No cached data for location")
            }

            Log.d("Offline", "Cached data found: ${cached.weatherResponse != null}")

            cached.weatherResponse?.let { response ->
                _currentDetails.value = Response.Success(response)
                Log.d("Offline", "Current weather loaded from cache")
            } ?: throw Exception("No current weather in cache")

            cached.watherForecast?.let { forecast ->
                val (nextHours, futureDays) = forecastProcessor.processForecast(forecast.list)
                _nextHoursDetails.value = Response.Success(nextHours)
                _futureDays.value = Response.Success(futureDays)
                Log.d("Offline", "Forecast loaded from cache")
            } ?: Log.w("Offline", "No forecast in cache")

        } catch (e: Exception) {
            Log.e("OfflineError", "Offline load failed", e)
            if (_currentDetails.value !is Response.Success) {
                _currentDetails.value = Response.Failure(e)
            }
            if (_nextHoursDetails.value !is Response.Success) {
                _nextHoursDetails.value = Response.Failure(e)
            }
            if (_futureDays.value !is Response.Success) {
                _futureDays.value = Response.Failure(e)
            }
        }
    }

    private suspend fun saveWeatherData(
        lat: Double,
        lon: Double,
        current: WeatherResponse,
        forecast: WeatherForecastResponse
    ) {
        try {
            val data = WeatherInHomeUsingRoom(
                lat = lat,
                lon = lon,
                weatherResponse = current,
                watherForecast = forecast,
            )

            repo.insertWeatherHome(data)
            Log.d("Database", "Data saved for lat:$lat lon:$lon")

            // Verify save
            val saved = repo.getAllDetailsWeatherFromLatLonHome(lat, lon).first()
            if (saved == null) {
                Log.e("Database", "Data verification failed - nothing saved")
            }
        } catch (e: Exception) {
            Log.e("Database", "Save failed", e)
        }
    }

    fun refresh() {
        loadWeatherData(lastLat, lastLon)
    }

    private fun getTempUnit() =
        sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) ?: "metric"

    private fun getLanguage() =
        sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) ?: "en"
}

class ForecastProcessor {
    @RequiresApi(Build.VERSION_CODES.O)
    fun processForecast(forecastList: List<WeatherResponse>): Pair<List<WeatherResponse>, List<WeatherResponse>> {
        return try {
            val currentDate = LocalDateTime.now().toLocalDate()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            val futureForecasts = forecastList.filter { item ->
                try {
                    LocalDateTime.parse(item.dt_txt, formatter)
                        .toLocalDate()
                        .isAfter(currentDate)
                } catch (e: Exception) {
                    false
                }
            }

            val dailyForecasts = futureForecasts
                .groupBy { it.dt_txt?.substring(0, 10) }
                .mapNotNull { (_, forecasts) ->
                    forecasts.firstOrNull()?.let { first ->
                        first.copy(
                            main = first.main?.copy(
                                temp_max = forecasts.maxOf { it.main?.temp_max ?: Double.MIN_VALUE },
                                temp_min = forecasts.minOf { it.main?.temp_min ?: Double.MAX_VALUE }
                            )
                        )
                    }
                }

            Pair(futureForecasts, dailyForecasts)
        } catch (e: Exception) {
            Log.e("ForecastProcessor", "Error processing forecast", e)
            Pair(emptyList(), emptyList())
        }
    }
}

class DetailsViewModelFactory(private val repo: Repo) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailsViewModel(repo) as T
    }
}


