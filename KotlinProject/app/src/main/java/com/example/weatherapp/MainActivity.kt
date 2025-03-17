package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.models.HourlyModel
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.RepoImpl
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.data.models.FutureModel
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue

import kotlinx.coroutines.launch



val items = listOf(
    HourlyModel("9 pm", 25, "cloudy"),
    HourlyModel("10 pm", 25, "sunny"),
    HourlyModel("11 pm", 25, "wind"),
    HourlyModel("10 pm", 25, "raniy"),
    HourlyModel("10 pm", 25, "stom"),
)
val items2 = listOf(
FutureModel("Sat", "cloudy", "Mostly Cloudy", 25, 18)
    ,FutureModel("Sun", "cloudy", "Mostly Cloudy", 25, 18)
    ,FutureModel("Mon", "cloudy", "Mostly Cloudy", 25, 18)
    ,FutureModel("Tue", "cloudy", "Mostly Cloudy", 25, 18)
    ,FutureModel("Wen", "cloudy", "Mostly Cloudy", 25, 18)
    ,FutureModel("Thu", "cloudy", "Mostly Cloudy", 25, 18)

)


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           AppNavigation(
               viewModel(
                   factory = WeatherViewModelFactory(
                       RepoImpl(
                           RemoteDataSourceImpl(RetrofitHelper.service),
                           LocalDataSourceImpl()
                       )

                   )
               )
           )
        }
    }
}
@Composable
fun NewWeatherScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Blue,BabyBlue)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherCard()
            Text(
                text = "Today's Forecast",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HourlyForecast()

            WeatherDetails()



            Text(
                text = "Next 5 Days",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            FutureForecast()
        }
    }
}
@Composable
fun WeatherCard() {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Mostly Cloudy",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = R.drawable.cloudy_sunny),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mon June 17 | 10:00 AM",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "25°C",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "H: 27°  L: 18°",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }


@Composable
fun WeatherDetails() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WeatherDetailItem(icon = R.drawable.rain, value = "80%", label = "Rain")
        WeatherDetailItem(icon = R.drawable.humidity, value = "60%", label = "Humidity")
        WeatherDetailItem(icon = R.drawable.wind, value = "15 km/h", label = "Wind")
      //  WeatherDetailItem(icon = R.drawable, value = "1000 hPa", label = "Pressure")
       // WeatherDetailItem(icon = R.drawable.visibility, value = "10 km", label = "Visibility")
    }
}

@Composable
fun WeatherDetailItem(icon: Int, value: String, label: String) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(
            text = label,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}
@Composable
fun HourlyForecast() {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            FutureModelViewHolder(item)
        }
    }
}

@Composable
fun FutureForecast() {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items2) { item ->
            FutureItemCard(item)
        }
    }
}

@Composable
fun FutureItemCard(item: FutureModel) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = item.day, fontSize = 14.sp, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = painterResource(id = getDrawableResourceId(item.picPath)),
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.status, fontSize = 14.sp, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${item.highTemp}°C / ${item.lowTemp}°C", fontSize = 14.sp, color = Color.White)
        }

}

@Composable
fun FutureModelViewHolder(model: HourlyModel) {
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
        Text(text = model.hour, textAlign = TextAlign.Center, fontSize = 16.sp, color = Color.White)
        Image(
            painter = painterResource(
                id = getDrawableResourceId(model.picPath)
            ),
            contentDescription = null,
            modifier = Modifier.size(45.dp).padding(8.dp),
            contentScale = ContentScale.Crop
        )
        Text(text = "${model.temp}", textAlign = TextAlign.Center, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun getDrawableResourceId(picPath: String): Int {
    return when (picPath) {
        "cloudy" -> R.drawable.cloudy
        "sunny" -> R.drawable.sunny
        "wind" -> R.drawable.wind
        "rainy" -> R.drawable.rainy
        "storm" -> R.drawable.storm
        else -> R.drawable.cloudy
    }
}



@Composable
fun CurvedBottomNavigationBar(navController: NavController) {
    val selected = remember { mutableStateOf(Icons.Default.Home) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Adjust height as needed
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // This creates the curved background
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width * 0.35f, 0f)
                cubicTo(
                    size.width * 0.42f, 0f,
                    size.width * 0.45f, size.height * 0.6f,
                    size.width * 0.5f, size.height * 0.6f
                )
                cubicTo(
                    size.width * 0.55f, size.height * 0.6f,
                    size.width * 0.58f, 0f,
                    size.width * 0.65f, 0f
                )
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, color = Color.Cyan)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = selected.value == Icons.Default.Home,
                onClick = {
                    selected.value = Icons.Default.Home
                    navController.navigate(ScreenRoutes.HomeScreen) { popUpTo(0) }
                }
            )
            BottomNavItem(
                icon = Icons.Default.Favorite,
                label = "Favorites",
                isSelected = selected.value == Icons.Default.Favorite,
                onClick = {
                    selected.value = Icons.Default.Favorite
                    navController.navigate(ScreenRoutes.FavLocScreen) { popUpTo(0) }
                }
            )
            BottomNavItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSelected = selected.value == Icons.Default.Settings,
                onClick = {
                    selected.value = Icons.Default.Settings
                    navController.navigate(ScreenRoutes.SettingsScreen) { popUpTo(0) }
                }
            )
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(if (isSelected) 32.dp else 26.dp),
            tint = if (isSelected) Color.White else Color.Gray
        )
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 12.sp
        )
    }
}
@Composable
fun AppNavigation(viewModel: WeatherViewModel) {
    val navController = rememberNavController()
    Scaffold(
//        bottomBar = {
//
//            CurvedBottomNavigationBar(navController)
//        }
    ) { paddingValues ->
        SetUpNavHost(viewModel,navController,paddingValues)
    }
}