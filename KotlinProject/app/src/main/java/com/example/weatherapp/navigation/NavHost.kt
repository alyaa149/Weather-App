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
import androidx.work.WorkManager
import com.example.weatherapp.features.favorites.view.FavLocUI
import com.example.weatherapp.features.favorites.viewmodel.FavViewModel
import com.example.weatherapp.features.favorites.viewmodel.FavViewModelFactory
import com.example.weatherapp.features.home.View.HomeWeatherScreen
import com.example.weatherapp.features.home.ViewModel.DetailsViewModel
import com.example.weatherapp.features.home.ViewModel.DetailsViewModelFactory
import com.example.weatherapp.features.map.view.MapScreen
import com.example.weatherapp.features.Settings.View.SettingsUI
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.local.WeatherDataBase
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.features.Settings.ViewModel.SettingsViewModel
import com.example.weatherapp.features.Settings.ViewModel.SettingsViewModelFactory
import com.example.weatherapp.features.map.viewmodel.MapViewModel
import com.example.weatherapp.features.map.viewmodel.MapViewModelFactory
import com.example.weatherapp.features.alerts.view.AlertsScreen
import com.example.weatherapp.features.alerts.viewmodel.AlertViewModel
import com.example.weatherapp.features.alerts.viewmodel.AlertViewModelFactory
import com.example.weatherapp.features.favorites.view.FavoritesDetailsScreen
import com.example.weatherapp.features.search.view.SearchScreen
import com.example.weatherapp.features.search.viewmodel.SearchViewModel
import com.example.weatherapp.features.search.viewmodel.SearchViewModelFactory


//
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetUpNavHost(navController: NavHostController, paddingValues: PaddingValues) {
    val context = LocalContext.current
    val detailsViewModel: DetailsViewModel = viewModel(
        factory = remember {
            DetailsViewModelFactory(
                RepoImpl(
                    RemoteDataSourceImpl(RetrofitHelper.service),
                    LocalDataSourceImpl(
                        WeatherDataBase.getInstance(context).getWeatherDao(),
                        WeatherDataBase.getInstance(context).getReminderDao()
                    ),

                )
            )
        }
    )
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.HomeScreen,
        modifier = Modifier.padding(paddingValues)
    ) {

        composable<ScreenRoutes.HomeScreen> {
            HomeWeatherScreen(detailsViewModel)
        }

        composable<ScreenRoutes.FavLocScreen> {
            val favoritesViewModel: FavViewModel = viewModel(
                factory = remember {
                    FavViewModelFactory(
                        RepoImpl(
                            RemoteDataSourceImpl(RetrofitHelper.service),
                            LocalDataSourceImpl(
                                WeatherDataBase.getInstance(context).getWeatherDao(),
                                WeatherDataBase.getInstance(context).getReminderDao()

                            ),
                        )
                    )
                }
            )
            FavLocUI(
                navigateToFavDetails = { lat, lon ->
                    navController.navigate(ScreenRoutes.FavDetailsScreen(lat, lon))
                },
                favoritesViewModel,
                navigateToMap = { navController.navigate(ScreenRoutes.MapScreenFromFavorites) }
            )
        }
        composable<ScreenRoutes.FavDetailsScreen> {
            FavoritesDetailsScreen(
                detailsViewModel,
                it.arguments?.getDouble("lat")!!,
                it.arguments?.getDouble("lon")!!,
            )
        }
        composable<ScreenRoutes.SettingsScreen> {
            val viewModel: SettingsViewModel = viewModel(
                factory = remember {
                    SettingsViewModelFactory()
                })

            SettingsUI(navigateToMap={navController.navigate(ScreenRoutes.MapScreenFromSettings)},viewModel)
        }
        composable<ScreenRoutes.MapScreenFromFavorites> {
            val mapViewModel: MapViewModel = viewModel(
                factory = remember {
                    MapViewModelFactory(
                        RepoImpl(
                            RemoteDataSourceImpl(RetrofitHelper.service),
                            LocalDataSourceImpl(
                                WeatherDataBase.getInstance(context).getWeatherDao(),
                                WeatherDataBase.getInstance(context).getReminderDao()

                            ),

                            )
                    )
                }
            )
            MapScreen(
                "from_favorites",
                back={navController.popBackStack()},
                mapViewModel
            )
        }
        composable<ScreenRoutes.MapScreenFromSettings> {
            val mapViewModel: MapViewModel = viewModel(
                factory = remember {
                    MapViewModelFactory(
                        RepoImpl(
                            RemoteDataSourceImpl(RetrofitHelper.service),
                            LocalDataSourceImpl(
                                WeatherDataBase.getInstance(context).getWeatherDao(),
                                WeatherDataBase.getInstance(context).getReminderDao()

                            ),

                            )
                    )
                }
            )
            MapScreen("from_settings", back={navController.popBackStack()}, mapViewModel)
        }
        composable<ScreenRoutes.SearchScreen> {
            val searchViewModel: SearchViewModel = viewModel(
                factory = remember {
                    SearchViewModelFactory(
                        RepoImpl(
                            RemoteDataSourceImpl(RetrofitHelper.service),
                            LocalDataSourceImpl(
                                WeatherDataBase.getInstance(context).getWeatherDao(),
                                WeatherDataBase.getInstance(context).getReminderDao()
                            ),
                        )
                    )
                })
            SearchScreen(searchViewModel)
        }
        composable<ScreenRoutes.AlertsScreen> {
            val alertsViewModel: AlertViewModel = viewModel(
                factory = remember {
                    AlertViewModelFactory(
                        RepoImpl(
                            RemoteDataSourceImpl(RetrofitHelper.service),
                            LocalDataSourceImpl(
                                WeatherDataBase.getInstance(context).getWeatherDao(),
                                WeatherDataBase.getInstance(context).getReminderDao()
                            ),
                        ),
                        WorkManager.getInstance(context),
                        context
                    )
                })
            AlertsScreen(alertsViewModel)
        }
        composable<ScreenRoutes.MapScreenFromNavBar> {
            val mapViewModel: MapViewModel = viewModel(

                factory = remember {
                    MapViewModelFactory(
                        RepoImpl(
                            RemoteDataSourceImpl(RetrofitHelper.service),
                            LocalDataSourceImpl(
                                WeatherDataBase.getInstance(context).getWeatherDao(),
                                WeatherDataBase.getInstance(context).getReminderDao()
                            ),
                        ),
                        )
                }
            )
            MapScreen("from_nav_bar",back={navController.popBackStack()},mapViewModel)
        }
    }
}

