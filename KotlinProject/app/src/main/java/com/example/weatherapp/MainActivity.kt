package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
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
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           // val weatherViewModel: WeatherViewModel = viewModel()
//            TestScreen(
//            viewModel(
//                factory = WeatherViewModelFactory(
//                    RepoImpl(
//                        RemoteDataSourceImpl(RetrofitHelper.service),
//                        LocalDataSourceImpl()
//                    )
//
//                )
//            )
//            )
           // weatherViewModel.fetchWeather("London")
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
fun TestScreen(viewModel: WeatherViewModel) {
    val weatherDetailsState = viewModel.currentDetails.observeAsState()
    val messageState = viewModel.message.observeAsState()

    viewModel.fetchWeatherFromLatLonUnitLang(30.0444, 31.2357, "metric", "ar")

    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "Temperature: ${weatherDetailsState.value ?: "Loading..."}")

            messageState.value?.let { message ->
                Text(text = "Error: $message", color = Color.Red)
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavController) {
    val selected = remember { mutableStateOf(Icons.Default.Home) }

    BottomAppBar(
        containerColor = Color.Cyan,
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    selected.value = Icons.Default.Home
                    navController.navigate(ScreenRoutes.HomeScreen) {
                        popUpTo(0)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(26.dp),
                        tint = if (selected.value == Icons.Default.Home) Color.White else Color.Gray
                    )
                    Text(
                        text = "Home",
                        color = if (selected.value == Icons.Default.Home) Color.White else Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(
                onClick = {
                    selected.value = Icons.Default.Favorite
                    navController.navigate(ScreenRoutes.FavLocScreen) {
                        popUpTo(0)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorites",
                        modifier = Modifier.size(26.dp),
                        tint = if (selected.value == Icons.Default.Favorite) Color.White else Color.Gray
                    )
                    Text(
                        text = "Favorites",
                        color = if (selected.value == Icons.Default.Favorite) Color.White else Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(
                onClick = {
                    selected.value = Icons.Default.Settings
                    navController.navigate(ScreenRoutes.SettingsScreen) {
                        popUpTo(0)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(26.dp),
                        tint = if (selected.value == Icons.Default.Settings) Color.White else Color.Gray
                    )
                    Text(
                        text = "Settings",
                        color = if (selected.value == Icons.Default.Settings) Color.White else Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


@Composable
fun AppNavigation(viewModel: WeatherViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        SetUpNavHost(viewModel,navController,paddingValues)
    }
}

