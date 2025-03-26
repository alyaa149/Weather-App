package com.example.weatherapp.features.search.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.Utils.Location.Location
import com.example.weatherapp.data.models.City
import com.example.weatherapp.features.search.viewmodel.SearchViewModel
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue
import com.example.weatherapp.ui.theme.Roze

@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    var searchQuery by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color.White, Blue)))
            .padding(top = 50.dp, start = 16.dp, end = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                  //  viewModel.onSearchQueryChanged(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search cities...", color = Blue) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = Blue)
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Results
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            items(filteredCities) { city ->
//                CityItem(city)
//            }
        }
    }
}

@Composable
fun CityItem(city: City) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = {
           // val latLon = Location().getLatLonFromCity(city.name)
          //  Log.d("Selected City", "Lat: ${latLon?.first}, Lon: ${latLon?.second}")
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
           // Text(text = city.name, fontSize = 18.sp, color = Blue, fontFamily = FontFamily.Monospace)
          //  Text(text = "${city.country}, ${city.state ?: ""}", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

