package com.example.weatherapp.Settings.View

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weatherapp.Settings.ViewModel.SettingsViewModel

@Composable
fun SettingsUI(viewModel: SettingsViewModel, navController: NavHostController){
    Text(text = "Settings",fontSize = 30.sp)


}