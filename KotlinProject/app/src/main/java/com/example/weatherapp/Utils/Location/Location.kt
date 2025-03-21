package com.example.weatherapp.Utils.Location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import com.example.weatherapp.Utils.AppContext
import com.example.weatherapp.Utils.MyAppContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Location() {
    val fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(AppContext.getContext())

    suspend fun getCurrentLocation(): Location? {
        val context = AppContext.getContext()
        return withContext(Dispatchers.IO) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@withContext null
            }

            val locationTask = fusedLocationProviderClient.lastLocation
            try {
                Tasks.await(locationTask)
                locationTask.result
            } catch (e: Exception) {
                null
            }
        }
    }
}
