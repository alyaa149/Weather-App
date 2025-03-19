package com.example.weatherapp.Favorites.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.weatherapp.WeatherViewModel

@Composable
fun FavLocUI(viewModel: WeatherViewModel, navController: NavHostController) {
//    // Observe LiveData as State
//    val weatherDetailsState = viewModel.currentDetailsList.observeAsState()
//    val messageState = viewModel.message.observeAsState()
//
//
//        viewModel.get5DaysWeatherForecast(44.34, 10.99, "metric", "en")
//
//
//    val snackBarHostState = remember { SnackbarHostState() }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackBarHostState) }
//    ) { innerPadding ->
//        Column(modifier = Modifier.padding(innerPadding)) {
//            // Display weather details
//            weatherDetailsState.value?.let { weatherList ->
//                //  if (weatherList.) {
//                // Display the list of weather responses
//                weatherList.list.forEach { weatherResponse ->
//                    if (weatherResponse != null) {
//                        Text(text = "Temperature: ${weatherResponse.weather?.get(0)?.id ?: "N/A"}")
//                    } else {
//                        Text(text = "No weather data available")
//                    }
//                }
//            }?: Text(text = "Loading...")
//
//
//            messageState.value?.let { message ->
//                Text(text = "Error: $message", color = Color.Red)
//            }
//        }
//    }
}