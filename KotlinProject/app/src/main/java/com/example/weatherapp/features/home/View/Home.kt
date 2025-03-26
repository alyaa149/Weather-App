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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue
import com.example.weatherapp.ui.theme.Roze
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeWeatherScreen(viewModel: DetailsViewModel) {
    val uiState = viewModel.currentDetails.collectAsStateWithLifecycle().value
    val hourlyForecast = viewModel.nextHoursDetailsList.collectAsStateWithLifecycle().value
    val futureDays = viewModel.futureDays.collectAsStateWithLifecycle().value
    val currentDate = viewModel.currentDate

    LaunchedEffect(Unit) {
        val lat = sharedPreferencesUtils.getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0
        val lon = sharedPreferencesUtils.getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0

        viewModel.fetchWeatherFromLatLonUnitLang(lat, lon,)
        viewModel.getFutureWeatherForecast(lat, lon)
        viewModel.getFutureDaysWeatherForecast(lat, lon)
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White,Blue)
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
                        Log.e("WeatherError", uiState.message.message.toString())
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

@RequiresApi(Build.VERSION_CODES.O)
fun getTheDayOfTheWeek(dateString: String?): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val outputFormatter = DateTimeFormatter.ofPattern("EEEE")
    val date = LocalDateTime.parse(dateString, inputFormatter).toLocalDate()
    return outputFormatter.format(date)
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
    currentDate: String
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
                .padding(top = 70.dp),
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
                    text = currentDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BabyBlue,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center

                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${weatherData.main?.temp}°${getUnit()}",
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
                            append("feels like ")
                        }
                        withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)) {
                            append("/ ${weatherData.main?.feels_like}°${getUnit()} ")
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
        if (sharedPreferencesUtils.getData(AppStrings().WINDUNITKEY) == AppStrings().MILE_PER_HOURKEY) "km/h" else "m/s"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        val weatherDetails = listOf(
            WeatherDetail(R.drawable.humidity, "${weatherData.main?.humidity ?: 0}%",
                stringResource(
                    R.string.humidity
                )
            ),
            WeatherDetail(R.drawable.wind, "${weatherData.wind?.speed ?: 0} $windunit",
                stringResource(R.string.wind)),
            WeatherDetail(R.drawable.pressure, "${weatherData.main?.pressure ?: 0} hPa",
                stringResource(
                    R.string.pressure
                )
            ),
            WeatherDetail(R.drawable.clouds, "${weatherData.clouds?.all ?: 0}%",
                stringResource(R.string.clouds)),
            WeatherDetail(R.drawable.sunrise, "${weatherData.sys?.sunrise ?: 0}",
                stringResource(R.string.sunrise)),
            WeatherDetail(R.drawable.sunset, "${weatherData.sys?.sunset ?: 0}",
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
fun formatDateTime(dtTxt: String?) :String? {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val outputFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val dateTime = LocalDateTime.parse(dtTxt, inputFormatter)
return  dateTime.format(outputFormatter)

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyItem(model: WeatherResponse) {
    Log.i("response", " mainnnnnn : ${model.weather?.get(0)?.main}")
    Card(
        modifier = Modifier
            .width(90.dp)
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
                text = "${formatDateTime(model.dt_txt)}",
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
                text = "${model.main?.temp}°${getUnit()}",
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
                    append("${item.main?.temp_max}°${getUnit()}  ")
                }
                withStyle(style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)) {
                    append("/ ${item.main?.temp_min}°${getUnit()} ")
                }
            },
            fontFamily = FontFamily.Monospace,
            color = Color.White
        )
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
fun getUnit():String{
    if(sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) == AppStrings().CELSIUSKEY) {
        return "C"
    }
    else if(sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY) == AppStrings().FAHRENHEITKEY){
       return "F"
    }
    else{
        return "K"
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