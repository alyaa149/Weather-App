package com.example.weatherapp.features.map.view

import android.location.Address
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.weatherapp.R
import com.example.weatherapp.Response
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.models.City
import com.example.weatherapp.features.home.View.LoadingIndicator
import com.example.weatherapp.features.home.View.WeatherCard
import com.example.weatherapp.features.home.View.WeatherDetails
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
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    val defaultLocation = LatLng(
        sharedPreferencesUtils.getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0,
        sharedPreferencesUtils.getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0
    )

    val searchedLocation by mapViewModel.searchedLocation.collectAsState()
    val searchResults by mapViewModel.searchResults.collectAsState()
    val updatedAddress by mapViewModel.updatedAddress.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 5f)
    }

    LaunchedEffect(searchedLocation) {
        searchedLocation?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 12f))
            selectedLocation = it
            mapViewModel.updateAddress(it)
        }
    }

    LaunchedEffect(updatedAddress) {
        if (source == "from_settings") {
            selectedLocation?.let {
                sharedPreferencesUtils.putData(AppStrings().LATITUDEKEY, it.latitude.toString())
                sharedPreferencesUtils.putData(AppStrings().LONGITUDEKEY, it.longitude.toString())
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
                mapViewModel.updateAddress(latLng)
            }
        ) {
            selectedLocation?.let {
                Marker(state = rememberMarkerState(position = it), title = "Selected Location")
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
        ) {
            SearchBar(searchQuery, onQueryChange = {
                searchQuery = it
                mapViewModel.search(it)
            }, searchResults, mapViewModel)

            selectedLocation?.let {
                updatedAddress?.let { address ->
                    LocationCard(address)
                }
            }
        }

        ConfirmButton(source, selectedLocation, updatedAddress, back, mapViewModel)
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, searchResults: List<Address>, mapViewModel: MapViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search_for_places), color = Blue) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = Blue) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Roze,
                    unfocusedContainerColor = Roze,
                    cursorColor = Blue,
                    focusedTextColor = Blue,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            if (searchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)
                ) {
                    items(searchResults) { address ->
                        val fullAddress = address.getFullAddress()
                        Text(
                            text = fullAddress,
                            modifier = Modifier.fillMaxWidth().clickable {
                                mapViewModel.selectLocation(address)
                                onQueryChange(fullAddress)
                            }.padding(12.dp),
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
}

@Composable
fun LocationCard(address: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = address,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = Blue
        )
    }
}

@Composable
fun ConfirmButton(
    source: String,
    selectedLocation: LatLng?,
    updatedAddress: String?,
    back: () -> Unit,
    mapViewModel: MapViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 550.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue),
            onClick = {
                selectedLocation?.let { latLng ->
                    if (source == "from_favorites") {
                        mapViewModel.fetchWeatherAndInsert(
                            latLng.latitude,
                            latLng.longitude,
                            updatedAddress
                        )
                    }
                    back()
                }
            },
        ) {
            Text(stringResource(R.string.confirm_location))
        }
    }
}

private fun Address.getFullAddress(): String {
    return (0..maxAddressLineIndex).joinToString(", ") { getAddressLine(it) }
}