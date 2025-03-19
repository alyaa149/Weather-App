package com.example.weatherapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.Favorites.View.FavLocUI
import com.example.weatherapp.Home.View.HomeWeatherScreen
import com.example.weatherapp.Home.ViewModel.HomeViewModel
import com.example.weatherapp.Settings.View.SettingsUI
import com.example.weatherapp.WeatherViewModel

//
@Composable
fun SetUpNavHost(viewModel: Any, navController: NavHostController, paddingValues: PaddingValues) {

    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.HomeScreen,
        modifier = Modifier.padding(paddingValues)
    ) {

        composable<ScreenRoutes.HomeScreen> {
            HomeWeatherScreen(viewModel as HomeViewModel,navController)
        }
        composable<ScreenRoutes.FavLocScreen> {
            FavLocUI(viewModel as WeatherViewModel,navController)
        }
        composable<ScreenRoutes.SettingsScreen> {
            SettingsUI(viewModel as WeatherViewModel,navController)
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

