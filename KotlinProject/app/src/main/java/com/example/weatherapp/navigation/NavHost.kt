package com.example.weatherapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.Favorites.View.FavLocUI
import com.example.weatherapp.Favorites.ViewModel.FavViewModel
import com.example.weatherapp.Favorites.ViewModel.FavViewModelFactory
import com.example.weatherapp.Home.View.HomeWeatherScreen
import com.example.weatherapp.Home.ViewModel.HomeViewModel
import com.example.weatherapp.Home.ViewModel.HomeViewModelFactory
import com.example.weatherapp.Settings.View.SettingsUI
import com.example.weatherapp.Settings.ViewModel.SettingsViewModel
import com.example.weatherapp.Utils.Location.Location
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.RepoImpl
import com.google.android.gms.location.LocationServices


//
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(navController: NavHostController, paddingValues: PaddingValues) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.HomeScreen,
        modifier = Modifier.padding(paddingValues)
    ) {

        composable<ScreenRoutes.HomeScreen> {
            val homeViewModel: HomeViewModel = viewModel(
                factory = remember {
                    HomeViewModelFactory(
                        RepoImpl(
                            RemoteDataSourceImpl(RetrofitHelper.service),
                            LocalDataSourceImpl()
                        ),
                        context,
                        Location(fusedLocationClient)
                    )
                }
            )
            HomeWeatherScreen(homeViewModel, navController)
        }

        composable<ScreenRoutes.FavLocScreen> {
            val favoritesViewModel: FavViewModel = viewModel(
                factory = remember {
                    FavViewModelFactory(
                        RepoImpl(
                            RemoteDataSourceImpl(RetrofitHelper.service),
                            LocalDataSourceImpl()
                        )
                    )
                }
            )
            FavLocUI(favoritesViewModel, navController)
        }

        composable<ScreenRoutes.SettingsScreen> {
            val settingsViewModel: SettingsViewModel = viewModel()
            SettingsUI(settingsViewModel, navController,fusedLocationClient)
        }
    }
}


//    composable<ScreenRoutes.LoginScreen> {
//        val login = it.toRoute<ScreenRoutes.LoginScreen>()
//        LoginUi(navigateToHome = { email ->
//            if (email.isNotBlank()) {
//                navController.navigate(ScreenRoutes.HomeScreen(email))
//            }
//        }, userName = login.userName, password = login.password)
//    }
//
//    composable<ScreenRoutes.HomeScreen> {
//        val profile = it.toRoute<ScreenRoutes.HomeScreen>()
//        HomeScreenUI(navController, profile.userName)
//    }

