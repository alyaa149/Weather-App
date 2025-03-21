package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.RepoImpl
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.ActivityCompat
import com.example.weatherapp.Home.ViewModel.HomeViewModelFactory
import com.example.weatherapp.navigation.ScreenRoutes
import com.example.weatherapp.navigation.SetUpNavHost
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.android.gms.location.LocationServices
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    public val REQUEST_LOCATION_CODE = 2005

    public val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
//private lateinit var fusedLocationClient: FusedLocationProviderClient


    public lateinit var locationState: MutableState<android.location.Location>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            locationState = remember { mutableStateOf(
                Location(
                    LocationManager.GPS_PROVIDER
                )
            ) }
            AppNavigation(
                viewModel(
                    factory = HomeViewModelFactory(
                        RepoImpl(
                            RemoteDataSourceImpl(RetrofitHelper.service),
                            LocalDataSourceImpl()
                        ),
                        LocalContext.current,
                        com.example.weatherapp.Utils.Location.Location(fusedLocationClient)

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
        fusedLocationClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
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
    val selectedItem = remember { mutableStateOf(Icons.Default.Home) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .offset(y = (-30).dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)

        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width * 0.35f, 0f)
                cubicTo(
                    size.width * 0.42f, 0f,
                    size.width * 0.45f, size.height * 0.55f,
                    size.width * 0.5f, size.height * 0.55f
                )
                cubicTo(
                    size.width * 0.55f, size.height * 0.55f,
                    size.width * 0.58f, 0f,
                    size.width * 0.65f, 0f
                )
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, color = Color.White, style = Fill)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = selectedItem.value == Icons.Default.Home,
                onClick = {
                    selectedItem.value = Icons.Default.Home
                    navController.navigate(ScreenRoutes.HomeScreen) { popUpTo(0) }
                }
            )

            BottomNavItem(
                icon = Icons.Default.Favorite,
                label = "Favorites",
                isSelected = selectedItem.value == Icons.Default.Favorite,
                onClick = {
                    selectedItem.value = Icons.Default.Favorite
                    navController.navigate(ScreenRoutes.FavLocScreen) { popUpTo(0) }
                }
            )

            Spacer(Modifier.width(56.dp)) // Space for the Floating Button

            BottomNavItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSelected = selectedItem.value == Icons.Default.Settings,
                onClick = {
                    selectedItem.value = Icons.Default.Settings
                    navController.navigate(ScreenRoutes.SettingsScreen) { popUpTo(0) }
                }
            )

            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                isSelected = selectedItem.value == Icons.Default.Person,
                onClick = {
                    selectedItem.value = Icons.Default.Person
                }
            )
        }

        FloatingActionButton(
            onClick = { /* Handle FAB click */ },
            contentColor = BabyBlue,
            modifier = Modifier
                .size(65.dp)
                .align(Alignment.TopCenter)
                .shadow(8.dp, shape = CircleShape)

        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Add")
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(if (isSelected) 30.dp else 24.dp),
            tint = if (isSelected) Blue else BabyBlue
        )
        Text(
            text = label,
            color = if (isSelected) Blue else BabyBlue,
            fontSize = 12.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(viewModel: Any) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            CurvedBottomNavigationBar(navController)
        }
    ) { paddingValues ->
        SetUpNavHost(navController, paddingValues)
    }
}