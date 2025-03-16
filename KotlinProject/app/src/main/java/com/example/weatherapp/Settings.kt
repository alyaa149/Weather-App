package com.example.weatherapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SettingsUI(viewModel: WeatherViewModel,navController: NavHostController){
    Text(text = "Settings",fontSize = 30.sp)


}