package com.example.weatherapp.features.favorites.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.Response
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


@Composable
fun FavLocUI(
    navigateToFavDetails: (Double, Double) -> Unit,
    viewModel: FavViewModel,
    navigateToMap: () -> Unit
) {
    val favoriteLocations by viewModel.favoriteLocations.collectAsState()

    Scaffold(
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
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(favoriteLocations) { location ->
                LocationItem(
                    location = location,
                    onRemove = { viewModel.deleteWeather(location) },
                    onClick = { lon, lat ->
                        navigateToFavDetails(lat, lon)
                    }
                )

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
            .clickable { onClick(location.lon, location.lat) },
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

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavoritesDetailsScreen(
    viewModel: DetailsViewModel,
    favoriteLat: Double,
    favoriteLon: Double,
    onBackClick: () -> Unit
) {
    val uiState = viewModel.currentDetails.collectAsStateWithLifecycle().value
    val hourlyForecast = viewModel.nextHoursDetailsList.collectAsStateWithLifecycle().value
    val futureDays = viewModel.futureDays.collectAsStateWithLifecycle().value
    val currentDate = viewModel.currentDate

    LaunchedEffect(Unit) {
        viewModel.fetchWeatherFromLatLonUnitLang(favoriteLat, favoriteLon)
        viewModel.getFutureWeatherForecast(favoriteLat, favoriteLon)
        viewModel.getFutureDaysWeatherForecast(favoriteLat, favoriteLon)
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
                        Log.i(
                            "response",
                            "from fav ,, Latitude in fav details: ${favoriteLat}, Longitude: $favoriteLon"
                        )
                    }

                    is Response.Failure -> Log.e("WeatherError", uiState.message.message.toString())
                }
            }
            item {
                when (hourlyForecast) {
                    is Response.Loading -> LoadingIndicator()
                    is Response.Success -> {
                        val forecastData = hourlyForecast.data
                        SectionTitle(stringResource(R.string.next_hours))
                        HourlyForecast(forecastData)
                        Log.i("response", "in favHome Latitude: $forecastData")
                    }

                    is Response.Failure -> {
                        Log.e("WeatherError", hourlyForecast.message.message.toString())
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
                        Log.e("WeatherError", futureDays.message.message.toString())
                    }
                }
            }
        }
    }
}
