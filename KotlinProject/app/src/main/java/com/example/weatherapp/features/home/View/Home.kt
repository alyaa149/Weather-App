package com.example.weatherapp.features.home.View

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.features.home.ViewModel.DetailsViewModel
import com.example.weatherapp.R
import com.example.weatherapp.Response
import com.example.weatherapp.Utils.AppContext
import com.example.weatherapp.Utils.NetworkUtils
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.fetchCurrentTime
import com.example.weatherapp.Utils.fetchformattedDateTime
import com.example.weatherapp.Utils.formatDateTime
import com.example.weatherapp.Utils.formatNumberBasedOnLanguage
import com.example.weatherapp.Utils.getDrawableResourceId
import com.example.weatherapp.Utils.getTheDayOfTheWeek
import com.example.weatherapp.Utils.getUnit
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue
import com.example.weatherapp.ui.theme.Roze
import com.google.android.gms.common.util.SharedPreferencesUtils
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeWeatherScreen(
    viewModel: DetailsViewModel,
) {
    val uiState = viewModel.currentDetails.collectAsStateWithLifecycle().value
    val hourlyForecast = viewModel.nextHoursDetails.collectAsStateWithLifecycle().value
    val futureDays = viewModel.futureDays.collectAsStateWithLifecycle().value
    val currentDateTime = fetchformattedDateTime()
    val currentDate = getTheDayOfTheWeek(currentDateTime)

    val snackbarHostState = remember { SnackbarHostState() }
    if (!NetworkUtils.isNetworkAvailable()) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(AppContext.getContext().getString(R.string.no_internet_connection))
        }

    }

    val scope = rememberCoroutineScope()
    var showError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val lat = sharedPreferencesUtils.getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0
            val lon = sharedPreferencesUtils.getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0
            viewModel.loadWeatherData(lat, lon)
        } catch (e: Exception) {
            showError = e.message ?: AppContext.getContext().getString(R.string.failed_to_load_weather_data)
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
                            val lat = sharedPreferencesUtils.getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0
                            val lon = sharedPreferencesUtils.getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0
                            viewModel.loadWeatherData(lat, lon)
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Blue)
                    )
                )
                .padding(padding)
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
                            WeatherCard(weatherData, currentDate ?: "00:00",currentDateTime)
                            WeatherDetails(weatherData)
                        }
                        is Response.Failure -> {
                            // Error is handled with Snackbar
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
                            // Error is handled with Snackbar
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
                            // Error is handled with Snackbar
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun SectionTitle(title: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}


@Composable
fun WeatherCard(
    weatherData: WeatherResponse,
    currentDate: String,
    currentDateTime: String
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(top = 50.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(8.dp),
            colors = CardDefaults.cardColors( Color(0xFFFAE6F9))

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp, start = 20.dp, end = 20.dp, bottom = 5.dp)

                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentDate +" "+ formatNumberBasedOnLanguage(currentDateTime),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BabyBlue,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center

                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${formatNumberBasedOnLanguage(weatherData.main?.temp.toString())}°${getUnit()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue,
                    fontFamily = FontFamily.Monospace

                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = weatherData.weather?.get(0)?.description ?: "description" ,
                    fontSize = 20.sp,
                    color = Blue,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold

                )
                Text(
                    text = weatherData.name ?: "name",
                    fontSize = 16.sp,
                    color = BabyBlue,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,


                    )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.feels_like))
                        }
                        withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)) {
                            append(": ${formatNumberBasedOnLanguage(weatherData.main?.feels_like.toString())}°${getUnit()} ")
                        }
                    },
                    fontFamily = FontFamily.Monospace,
                    color = BabyBlue
                )
            }
        }

        Image(
            painter= painterResource(getDrawableResourceId(weatherData.weather?.get(0)?.main)),
            contentDescription = "Weather Icon",
            contentScale = ContentScale.Fit
,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 20.dp)
                        //   .clip(CircleShape)
                .border(4.dp, Roze, CircleShape)
                        .shadow(8.dp, CircleShape)
        )
    }
}


data class WeatherDetail(val icon: Int, val value: String, val label: String)

@Composable
fun WeatherDetails(weatherData: WeatherResponse) {
    val windunit :String =
        if (sharedPreferencesUtils.getData(AppStrings().WINDUNITKEY) == AppStrings().MILE_PER_HOURKEY) stringResource(
            R.string.km_h
        ) else stringResource(R.string.m_s)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        val weatherDetails = listOf(
            WeatherDetail(R.drawable.humidity, "${formatNumberBasedOnLanguage(weatherData.main?.humidity ?: 0 .toString())}%",
                stringResource(
                    R.string.humidity
                )
            ),
            WeatherDetail(R.drawable.wind, "${formatNumberBasedOnLanguage((weatherData.wind?.speed ?: 0.toString()).toString())} $windunit",
                stringResource(R.string.wind)),
            WeatherDetail(R.drawable.pressure,
                stringResource(R.string.hpa, formatNumberBasedOnLanguage(weatherData.main?.pressure.toString())),
                stringResource(
                    R.string.pressure
                )
            ),
            WeatherDetail(R.drawable.clouds, "${formatNumberBasedOnLanguage((weatherData.clouds?.all ?: 0 .toString()).toString())}%",
                stringResource(R.string.clouds)),
            WeatherDetail(R.drawable.sunrise,
                formatNumberBasedOnLanguage(weatherData.sys?.sunrise.toString() ),
                stringResource(R.string.sunrise)),
            WeatherDetail(R.drawable.sunset,
                formatNumberBasedOnLanguage(weatherData.sys?.sunset.toString()),
                stringResource(R.string.sunset))
        )

        weatherDetails.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { detail ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        WeatherDetailItem(icon = detail.icon, value = detail.value, label = detail.label)
                    }
                }
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
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,

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
        horizontalArrangement = Arrangement.spacedBy(8.dp),

    ) {
        items(forecastResponse) { item ->
            HourlyItem(item)
        }

    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyItem(model: WeatherResponse) {
    Log.i("response", " mainnnnnn : ${model.weather?.get(0)?.main}")
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(150.dp)
            .padding(4.dp)
            .background(
                color = Roze,
                shape = RoundedCornerShape(20.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 9.dp)

    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Center elements horizontally
        ) {
            Text(
                text = formatNumberBasedOnLanguage(formatDateTime(model.dt_txt) ?: "00:00"),
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Blue
            )

            Image(
                painter = painterResource(id = getDrawableResourceId(model.weather?.get(0)?.main)),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "${formatNumberBasedOnLanguage(model.main?.temp.toString())}°${getUnit()}",
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Blue
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FutureDaysForecast(forecastData: List<WeatherResponse>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 60.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(forecastData) { item ->
            FutureDyItemCard(item)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FutureDyItemCard(item: WeatherResponse) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = getTheDayOfTheWeek(item.dt_txt),
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Image(
            painter = painterResource(getDrawableResourceId(item.weather?.get(0)?.main)),
            contentDescription = null,
            modifier = Modifier.size(45.dp),
            contentScale = ContentScale.Fit

        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
                    append("${formatNumberBasedOnLanguage(item.main?.temp_max.toString())}°${getUnit()}  ")
                }
                withStyle(style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)) {
                    append("/ ${formatNumberBasedOnLanguage(item.main?.temp_min.toString())}°${getUnit()} ")
                }
            },
            fontFamily = FontFamily.Monospace,
            color = Color.White
        )
    }


    }


@Composable
fun LoadingIndicator() {
    Box (
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ){
        CircularProgressIndicator()
    }
}