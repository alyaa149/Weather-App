package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.features.alerts.view.WeatherAlertScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.gson.Gson

class WeatherAlert : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val weatherJson = intent.getStringExtra("weather_data")
        val weather = Gson().fromJson(weatherJson, WeatherResponse::class.java)

        setContent {
            WeatherAlertScreen(weather) {
                finish() // Close activity when dismiss is clicked
            }
        }
    }
}

