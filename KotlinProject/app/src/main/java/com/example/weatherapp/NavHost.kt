package com.example.weatherapp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
//
@Composable
fun setUpNavHost(navController: NavHostController,paddingValues: PaddingValues) {
   // val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.HomeScreen,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable<ScreenRoutes.HomeScreen> {
            HomeUI(navController)
        }
        composable<ScreenRoutes.FavLocScreen> {
            FavLocUI(navController)
        }
        composable<ScreenRoutes.SettingsScreen> {
            SettingsUI(navController)
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

