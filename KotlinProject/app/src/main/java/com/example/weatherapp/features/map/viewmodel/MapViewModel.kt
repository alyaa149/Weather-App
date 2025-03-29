package com.example.weatherapp.features.map.viewmodel

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Response
import com.example.weatherapp.Utils.AppContext
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.repo.Repo
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class MapViewModel(private val repo: Repo) : ViewModel() {
    private val geocoder by lazy {
        Geocoder(AppContext.getContext(), Locale.getDefault())
    }
    private val _searchFlow = MutableSharedFlow<String>()
    private val _searchedLocation = MutableStateFlow<LatLng?>(null)
    private val _searchResults = MutableStateFlow<List<Address>>(emptyList())
    private val _updatedAddress = MutableStateFlow<String?>(null)
    private val _currentDetails = MutableStateFlow<Response<WeatherResponse>>(Response.Loading)
    val currentDetails: StateFlow<Response<WeatherResponse>> = _currentDetails

    val searchedLocation = _searchedLocation.asStateFlow()
    val searchResults = _searchResults.asStateFlow()
    val updatedAddress = _updatedAddress.asStateFlow()

    init {
        observeSearchFlow()
    }


    fun fetchWeatherAndInsert(lat: Double, lon: Double, address: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentDetails.value = Response.Loading
            try {
                repo.fetchWeatherFromLatLonUnitLang(
                    lat,
                    lon,
                    sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) ?: "metric",
                    sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) ?: "en"
                ).collectLatest { response ->  //Cancels previous collections
                    _currentDetails.value = Response.Success(response)
                    insertWeather(
                        City(
                            address = address ?: "Unknown",
                            lat = lat,
                            lon = lon,
                            weatherResponse = response
                        )
                    )
                }
            } catch (e: Exception) {
                _currentDetails.value = Response.Failure(e)
                Log.e("WeatherError", e.message.toString())
            }
        }
    }
    private fun observeSearchFlow() {
        viewModelScope.launch {
            _searchFlow
                .debounce(500)
                .collectLatest { query ->
                    searchLocation(query)
                }
        }
    }

     fun search(query: String) {
         viewModelScope.launch {
             _searchFlow.emit(query)
         }
    }

    private fun searchLocation(query: String) {
        if (query.isBlank()) {
            _searchedLocation.value = null
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val addresses = geocoder.getFromLocationName(query, 5) ?: emptyList()

                if (addresses.isNotEmpty()) {
                    _searchResults.value = addresses
                    if (addresses.size == 1) {
                        _searchedLocation.value = addresses.first().toLatLng()
                    }
                } else {
                    val fallbackQuery = query.split(",").first().trim()
                    geocoder.getFromLocationName(fallbackQuery, 1)?.firstOrNull()?.let {
                        _searchedLocation.value = it.toLatLng()
                    }
                }
            } catch (e: IOException) {
                Log.e("Geocoder", "Error fetching location: ${e.message}")
            }
        }
    }

    fun selectLocation(address: Address) {
        _searchedLocation.value = address.toLatLng()
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun updateAddress(latLng: LatLng) {
        viewModelScope.launch {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                _updatedAddress.value = addresses?.firstOrNull()?.getFullAddress() ?: "Address not available"
            } catch (e: Exception) {
                _updatedAddress.value = "Address lookup failed: ${e.message}"
            }
        }
    }

    fun insertWeather(city: City) {
        viewModelScope.launch {
            try {
                val result = repo.insertWeather(city)
                Log.i("response", "Inserted city ID: $result")
            } catch (e: Exception) {
                Log.e("response", "Insert error: ${e.message}")
            }
        }
    }


    private fun Address.toLatLng() = LatLng(latitude, longitude)
    private fun Address.getFullAddress(): String {
        return (0..maxAddressLineIndex).joinToString { getAddressLine(it) }
    }
}
class MapViewModelFactory(private val repo: Repo) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(repo) as T
    }
}

