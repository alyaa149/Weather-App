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
    object MapScreenFromSettings : ScreenRoutes()
    @Serializable
    object MapScreenFromFavorites : ScreenRoutes()
    @Serializable
    object MapScreenFromNavBar:ScreenRoutes()
    @Serializable
    object SearchScreen : ScreenRoutes()
    @Serializable
    object AlertsScreen : ScreenRoutes()
    @Serializable
    data class FavDetailsScreen(val lon: Double, val lat: Double) : ScreenRoutes()
}
    //@Serializable
    // data class HomeScreen(val userName: String) : ScreenRoutes()
//    @Serializable
//    object FirstScreen : ScreenRoutes()
