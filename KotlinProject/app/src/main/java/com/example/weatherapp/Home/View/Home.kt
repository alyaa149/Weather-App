package com.example.weatherapp.Home.View

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*;
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.weatherapp.Home.ViewModel.HomeViewModel
import com.example.weatherapp.R
import com.example.weatherapp.Response
import com.example.weatherapp.data.models.FutureModel
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeWeatherScreen(viewModel: HomeViewModel, navController: NavHostController) {
    val uiState = viewModel.currentDetails.collectAsStateWithLifecycle().value
    val forecastState = viewModel.nextHoursDetailsList.collectAsStateWithLifecycle().value
    val currentDate = viewModel.currentDate

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Blue, BabyBlue)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (uiState) {
                is Response.Loading -> LoadingIndicator()
                is Response.Success -> {
                    val weatherData = uiState.data
                    WeatherCard(weatherData,currentDate)
                    WeatherDetails(weatherData)

                //    HourlyForecast(com.example.weatherapp.items)

                }
                is Response.Failure -> {
                    Log.e("WeatherError", uiState.message.message.toString())
                }
            }

            when (forecastState) {
                is Response.Loading -> LoadingIndicator()
                is Response.Success -> {
                    val forecastData = forecastState.data
                    Text(
                        text = "Next Hours",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)

                    )
                    Log.i("response", "${forecastState.data}")

                   //FutureForecast(forecastData)
                    HourlyForecast(forecastData)
                }
                is Response.Failure -> {
                    Log.e("WeatherError", forecastState.message.message.toString())
                }
            }
        }
    }
}

@Composable
fun WeatherCard(weatherData: WeatherResponse, currentDate: String) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${weatherData.weather?.get(0)?.description} in ${weatherData.name}",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,

            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = getDrawableResourceId(weatherData.weather?.get(0)?.main)),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = currentDate,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,

            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${weatherData.main?.temp}°",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "H: ${weatherData.main?.temp_max}°  L: ${weatherData.main?.temp_min}°",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,

            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

data class WeatherDetail(val icon: Int, val value: String, val label: String)

@Composable
fun WeatherDetails(weatherData: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        val weatherDetails = listOf(
            WeatherDetail(R.drawable.humidity, "${weatherData.main?.humidity ?: 0}%", "Humidity"),
            WeatherDetail(R.drawable.wind, "${weatherData.wind?.speed ?: 0} km/h", "Wind"),
            WeatherDetail(R.drawable.pressure, "${weatherData.main?.pressure ?: 0} hPa", "Pressure"),
            WeatherDetail(R.drawable.clouds, "${weatherData.clouds?.all ?: 0}%", "Clouds"),
            WeatherDetail(R.drawable.sunrise, "${weatherData.sys?.sunrise ?: 0}", "Sunrise"),
            WeatherDetail(R.drawable.sunset, "${weatherData.sys?.sunset ?: 0}", "Sunset")
        )

        // Group items into pairs (2 per row)
        weatherDetails.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween // Better spacing
            ) {
                rowItems.forEach { detail ->
                    Box(
                        modifier = Modifier.weight(1f), // Ensures equal width
                        contentAlignment = Alignment.Center
                    ) {
                        WeatherDetailItem(icon = detail.icon, value = detail.value, label = detail.label)
                    }
                }
                // If odd number of items, add an empty space for alignment
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun WeatherDetailItem(icon: Int, value: String, label: String) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = value,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(
            text = label,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,

            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecast(forecastResponse: List<WeatherResponse>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(forecastResponse) { item ->
            HourlyItem(item)
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(dtTxt: String?) :String? {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val outputFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val dateTime = LocalDateTime.parse(dtTxt, inputFormatter)
return  dateTime.format(outputFormatter);

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyItem(model: WeatherResponse) {
    Log.i("response"," mainnnnnn : ${model.weather?.get(0)?.main}")

    Column(
        modifier = Modifier
            .width(90.dp)
            .wrapContentHeight()
            .padding(4.dp)
            .background(
                color = Blue.copy(alpha = 0.8f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Text(text = "${formatDateTime(model.dt_txt)}",fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                textAlign = TextAlign.Center, fontSize = 16.sp, color = Color.White)

        Image(
            painter = painterResource(
                id = getDrawableResourceId(model.weather?.get(0)?.main)
            ),
            contentDescription = null,
            modifier = Modifier.size(45.dp).padding(8.dp),
            contentScale = ContentScale.Crop
        )
        //weatherData.main?.temp
        Text(text = "${model.main?.temp}°",fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            textAlign = TextAlign.Center, fontSize = 16.sp, color = Color.White)
        }

}

//@Composable
//fun FutureForecast(forecastData: WeatherForecastResponse) {
//    LazyRow(
//        modifier = Modifier.fillMaxWidth(),
//        contentPadding = PaddingValues(horizontal = 16.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(items2) { item ->
//            FutureItemCard(item)
//        }
//    }
//}

@Composable
fun FutureItemCard(item: FutureModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = item.day,
            fontSize = 14.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
//        Image(
//            painter = painterResource(R)),
//            contentDescription = null,
//            modifier = Modifier.size(45.dp)
//        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = item.status, fontSize = 14.sp,fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${item.highTemp}°C / ${item.lowTemp}°C", fontSize = 14.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            color = Color.White)
    }

}



@Composable
fun getDrawableResourceId(picPath: String?): Int {
    return when (picPath) {
        "Clear" -> R.drawable.sunny
        "Clouds" -> R.drawable.cloudy
        "Rain" -> R.drawable.rainy
        "Snow" -> R.drawable.snowy
        "storm" -> R.drawable.storm
        else -> R.drawable.back
    }
}


@Composable
fun LoadingIndicator() {
    Box (
        modifier = Modifier.fillMaxSize().wrapContentSize()
    ){
        CircularProgressIndicator()
    }
}