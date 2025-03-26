package com.example.weatherapp.features.map.view

import android.location.Address
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weatherapp.R
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.models.City
import com.example.weatherapp.features.map.viewmodel.MapViewModel
import com.example.weatherapp.ui.theme.Blue
import com.example.weatherapp.ui.theme.Roze
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun MapScreen(source: String, back: () -> Unit, mapViewModel: MapViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val defaultLocation = LatLng(
        sharedPreferencesUtils.getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0,
        sharedPreferencesUtils.getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0
    )
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    // StateFlow collections
    val searchedLocation by mapViewModel.searchedLocation.collectAsState()
    val searchResults by mapViewModel.searchResults.collectAsState()
    val updatedAddress by mapViewModel.updatedAddress.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 5f)
    }

    // Update selected address when searchedLocation changes
    LaunchedEffect(searchedLocation) {
        searchedLocation?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 12f))
            selectedLocation = it
            mapViewModel.updateAddress(it) // No callback; result flows to `updatedAddress`
        }
    }

    // Update selectedAddress whenever updatedAddress changes
    LaunchedEffect(updatedAddress) {
        updatedAddress?.let { address ->
            selectedLocation?.let { latLng ->
                if (source == "from_settings") {
                    sharedPreferencesUtils.putData(
                        AppStrings().LATITUDEKEY,
                        latLng.latitude.toString()
                    )
                    sharedPreferencesUtils.putData(
                        AppStrings().LONGITUDEKEY,
                        latLng.longitude.toString()
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLocation = latLng
                mapViewModel.clearSearchResults()
                mapViewModel.updateAddress(latLng) // Auto-fetch address on click
            }
        ) {
            selectedLocation?.let { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Selected Location"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                             mapViewModel.search(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.search_for_places), color = Blue) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Blue
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Roze,
                            unfocusedContainerColor = Roze,
                            disabledContainerColor = Roze,
                            cursorColor = Blue,
                            focusedTextColor = Blue,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    if (searchResults.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {
                            items(searchResults) { address ->
                                val fullAddress = address.getFullAddress()
                                Text(
                                    text = fullAddress,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            mapViewModel.selectLocation(address)
                                            searchQuery = fullAddress
                                        }
                                        .padding(12.dp),
                                    color = Blue,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }

            selectedLocation?.let {
                updatedAddress?.let { address ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = address,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = Blue
                        )
                    }
                }
            }
        }

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Blue),
            onClick = {
                selectedLocation?.let { latLng ->
                    if (source == "from_favorites") {
                        mapViewModel.insertWeather(
                            City(
                                address = updatedAddress ?: "Unknown",
                                lat = latLng.latitude,
                                lon = latLng.longitude
                            )
                        )
                    }
                    back()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .padding(bottom = 30.dp)
        ) {
            Text(stringResource(R.string.confirm_location))
        }
    }
}

private fun Address.getFullAddress(): String {
    return (0..maxAddressLineIndex).joinToString(", ") { getAddressLine(it) }
}