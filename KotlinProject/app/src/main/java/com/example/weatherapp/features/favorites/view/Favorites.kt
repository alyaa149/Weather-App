package com.example.weatherapp.features.favorites.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.models.City
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.Response
import com.example.weatherapp.Utils.NetworkUtils
import com.example.weatherapp.features.home.View.FutureDaysForecast
import com.example.weatherapp.features.home.View.HourlyForecast
import com.example.weatherapp.features.home.View.LoadingIndicator
import com.example.weatherapp.features.home.View.SectionTitle
import com.example.weatherapp.features.home.View.WeatherCard
import com.example.weatherapp.features.home.View.WeatherDetails
import com.example.weatherapp.features.home.ViewModel.DetailsViewModel
import com.example.weatherapp.features.favorites.viewmodel.FavViewModel
import com.example.weatherapp.ui.theme.Blue
import com.example.weatherapp.ui.theme.Roze
import kotlinx.coroutines.launch


@Composable
fun FavLocUI(
    navigateToFavDetails: (Double, Double) -> Unit,
    viewModel: FavViewModel,
    navigateToMap: () -> Unit
) {
    val favoriteLocations by viewModel.favoriteLocations.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val eventFlow = viewModel.eventFlow
    val coroutineScope = rememberCoroutineScope()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation))

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

        floatingActionButton = {
            FloatingActionButton(
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                onClick = { navigateToMap() }) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Add Location",
                    tint = Blue
                )
            }
        },
        modifier = Modifier.systemBarsPadding()


        ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val favoritesState = favoriteLocations) {
                is Response.Loading -> {
                    LoadingIndicator()
                }

                is Response.Success -> {
                    if (favoritesState.data.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.size(200.dp)
                            )
                            Text(
                                text = stringResource(R.string.no_favorite_locations_yet),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn {
                            items(favoritesState.data) { location ->
                                LocationItem(
                                    location = location,
                                    onRemove = {
                                        viewModel.deleteWeather(
                                            location,
                                            snackbarHostState,
                                            coroutineScope
                                        )
                                    },
                                    onClick = { lat, lon ->
                                        navigateToFavDetails(lat, lon)
                                    }
                                )
                            }
                        }
                    }
                }

                is Response.Failure -> {
                    Text(text = "Error: ${favoritesState.message}")
                }
            }
        }
    }
}

@Composable
fun LocationItem(
    location: City,
    onRemove: () -> Unit,
    onClick: (Double, Double) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(location.lat, location.lon) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Roze
        )
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.locationn),
                contentDescription = "Alert",
                modifier = Modifier
                    .size(30.dp)
                    .fillMaxHeight()
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 7.dp)
            ) {
                Text(
                    text = location.address.substringAfterLast(","),
                    style = TextStyle(
                        color = Blue,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(start = 5.dp)
                )
                Text(
                    text = location.address.substringBeforeLast(","),
                    style = TextStyle(
                        color = Blue,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                    ),
                    modifier = Modifier.padding(start = 17.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Blue)
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavoritesDetailsScreen(
    viewModel: DetailsViewModel,
    favoriteLat: Double,
    favoriteLon: Double,
) {
    val uiState = viewModel.currentDetails.collectAsStateWithLifecycle().value
    val hourlyForecast = viewModel.nextHoursDetails.collectAsStateWithLifecycle().value
    val futureDays = viewModel.futureDays.collectAsStateWithLifecycle().value
    val currentDate = viewModel.currentDate

    val scope = rememberCoroutineScope()
    var showError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            viewModel.loadWeatherData(favoriteLon, favoriteLat)
        } catch (e: Exception) {
            showError = e.message ?: "Failed to load weather data"
        }
    }

    // Show error dialog if needed
    showError?.let { error ->
        AlertDialog(
            onDismissRequest = { showError = null },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        try {
                            viewModel.loadWeatherData(favoriteLon, favoriteLat)
                            showError = null
                        } catch (e: Exception) {
                            showError = e.message ?: "Failed to load weather data"
                        }
                    }
                }) {
                    Text("Retry")
                }
            },
            dismissButton = {
                Button(onClick = { showError = null }) {
                    Text("Dismiss")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Blue)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                when (uiState) {
                    is Response.Loading -> LoadingIndicator()
                    is Response.Success -> {
                        val weatherData = uiState.data
                        WeatherCard(weatherData, currentDate)
                        WeatherDetails(weatherData)
                    }

                    is Response.Failure -> {
                        // Error is shown in the dialog
                    }
                }
            }

            item {
                when (hourlyForecast) {
                    is Response.Loading -> LoadingIndicator()
                    is Response.Success -> {
                        val forecastData = hourlyForecast.data
                        SectionTitle(stringResource(R.string.next_hours))
                        HourlyForecast(forecastData)
                    }

                    is Response.Failure -> {
                        // Error is shown in the dialog
                    }
                }
            }

            item {
                when (futureDays) {
                    is Response.Loading -> LoadingIndicator()
                    is Response.Success -> {
                        val forecastData = futureDays.data
                        SectionTitle(stringResource(R.string.future_days))
                        FutureDaysForecast(forecastData)
                    }

                    is Response.Failure -> {
                        // Error is shown in the dialog
                    }
                }
            }
        }

    }
}
