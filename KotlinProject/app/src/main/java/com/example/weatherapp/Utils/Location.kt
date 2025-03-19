package com.example.weatherapp.Utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat

//public fun isLocationEnabled(): Boolean {
//    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//}
//
//public fun enableLocationServices() {
//    Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
//    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//    startActivity(intent)
//}
//
//public fun checkPermissions(): Boolean {
//    return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED) ||
//            (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED)
//}
//
//
//
//public fun getFreshLocation() {
//    if (ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        return
//    }
//    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//        location?.let {
//            locationState.value = it
//        } ?: run {
//            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
//
//
//
//
//
//fun getAddressFromLocation(lat: Double, lon: Double, context: Context): String {
//    var address = ""
//
//    val geocoder = Geocoder(context)
//    val addresses = geocoder.getFromLocation(lat, lon, 1)
//    if (!addresses.isNullOrEmpty()) {
//        address = addresses[0].getAddressLine(0) ?: "Address not found"
//    } else {
//        address= "Address not found"
//    }
//
//    return address
//}
//
//
//fun openLocationInMap(lat: Double, lon: Double, context: Context) {
//    val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
//    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//    context.startActivity(mapIntent)
//}
//fun openSmsWithAddress(address: String, phoneNumber: String, context: Context) {
//    val smsUri = Uri.parse("smsto:$phoneNumber")
//    val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
//    smsIntent.putExtra("sms_body", address)
//    context.startActivity(smsIntent)
//}