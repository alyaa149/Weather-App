package com.example.weatherapp.Settings.View

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.sharedprefrences.WeatherSharedPrefrences
import com.example.weatherapp.ui.theme.Blue
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(navController: NavHostController) {
    val defaultLocation = LatLng((WeatherSharedPrefrences().getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0), (WeatherSharedPrefrences().getData(
        AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0) )
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            selectedLocation = latLng
        }
    ) {
        selectedLocation?.let { location ->
            Marker(
                state = rememberMarkerState(position = location),
                title = "Selected Location"
            )
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 50.dp)
        ,

        contentAlignment = Alignment.BottomCenter) {
        Button(
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Blue,
                contentColor = Color.White
            ),
            onClick = {
                selectedLocation?.let { latLng ->
                    WeatherSharedPrefrences().putData(AppStrings().LATITUDEKEY, latLng.latitude.toString())
                    WeatherSharedPrefrences().putData(AppStrings().LONGITUDEKEY, latLng.longitude.toString())

                    // Navigate back to settings screen
                    navController.popBackStack()
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Confirm Location")
        }
    }
}


