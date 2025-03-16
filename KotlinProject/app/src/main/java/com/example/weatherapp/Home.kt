package com.example.weatherapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*;
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

//@Composable
//fun HomeUI(viewModel: WeatherViewModel,navController: NavHostController){
//    val weatherDetailsState = viewModel.currentDetails.observeAsState()
//    val messageState = viewModel.message.observeAsState()
//
//    viewModel.fetchWeatherFromLatLonUnitLang(30.0444, 31.2357, "metric", "ar")
//
//    val snackBarHostState = remember { SnackbarHostState() }
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackBarHostState) }
//    ) { innerPadding ->
//        Column(modifier = Modifier.padding(innerPadding)) {
//            Text(text = "Temperature: ${weatherDetailsState.value ?: "Loading..."}")
//
//            messageState.value?.let { message ->
//                Text(text = "Error: $message", color = Color.Red)
//            }
//        }
//    }
//}
@Composable

fun HomeUI(viewModel: WeatherViewModel, navController: NavHostController){
    var city by remember { mutableStateOf("") }
    Column {
        Row {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Search City") },
                modifier = Modifier.padding(8.dp)

            )

        }
    }
}