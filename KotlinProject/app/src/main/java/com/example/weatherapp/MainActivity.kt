package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.models.HourlyModel
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.RepoImpl
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.ActivityCompat
import com.example.weatherapp.Home.ViewModel.HomeViewModelFactory
import com.example.weatherapp.data.models.FutureModel
import com.example.weatherapp.navigation.ScreenRoutes
import com.example.weatherapp.navigation.SetUpNavHost
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.weatherapp.Utils.Location.LocationRepository


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
    public val REQUEST_LOCATION_CODE = 2005

    public val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
//private lateinit var fusedLocationClient: FusedLocationProviderClient


    public lateinit var locationState: MutableState<Location>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            locationState = remember { mutableStateOf(Location(LocationManager.GPS_PROVIDER)) }
            AppNavigation(
                viewModel(
                    factory = HomeViewModelFactory(
                        RepoImpl(
                            RemoteDataSourceImpl(RetrofitHelper.service),
                            LocalDataSourceImpl()
                        ),
                        LocalContext.current,
                        LocationRepository(fusedLocationClient)

                    )
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getFreshLocation()
            } else {
                enableLocationServices()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_CODE
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun enableLocationServices() {
        Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun checkPermissions(): Boolean {
        return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                getFreshLocation()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFreshLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                locationState.value = it
            } ?: run {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        }
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
fun AppNavigation(viewModel: Any) {
    val navController = rememberNavController()
    Scaffold(
//        bottomBar = {
//
//            CurvedBottomNavigationBar(navController)
//        }
    ) { paddingValues ->
        SetUpNavHost(viewModel, navController, paddingValues)
    }

}