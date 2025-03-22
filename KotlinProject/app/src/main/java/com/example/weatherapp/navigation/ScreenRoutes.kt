package com.example.weatherapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoutes {
    @Serializable
    object HomeScreen : ScreenRoutes()
    @Serializable
    object FavLocScreen : ScreenRoutes()
    @Serializable
    object SettingsScreen : ScreenRoutes()
    @Serializable
    object MapScreen : ScreenRoutes()
}
    //@Serializable
    // data class HomeScreen(val userName: String) : ScreenRoutes()
//    @Serializable
//    object FirstScreen : ScreenRoutes()
