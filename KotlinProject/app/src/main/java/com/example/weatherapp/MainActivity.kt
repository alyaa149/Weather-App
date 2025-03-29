package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.ActivityCompat
import com.example.weatherapp.navigation.ScreenRoutes
import com.example.weatherapp.navigation.SetUpNavHost
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.google.android.gms.location.LocationServices
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherapp.Utils.AppContext
import com.example.weatherapp.features.alerts.notificationnsandalerts.AlarmManager
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    public val REQUEST_LOCATION_CODE = 2005

    public val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
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
//            AppNavigation()
            Column(modifier = Modifier.padding(16.dp)) {
                AlarmIndicatorUI(
                    alarmTime = LocalTime.of(7, 30),
                    isActive = true,
                    onToggle = {},
                    onEdit = {},
                    onDismiss = {},
alarmManager = AlarmManager()                )

                Spacer(modifier = Modifier.height(16.dp))

                AlarmIndicatorUI(
                    alarmTime = LocalTime.of(9, 15),
                    isActive = false,
                    onToggle = {},
                    onEdit = {},
                    onDismiss = {},
alarmManager = AlarmManager()                )
            }

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(AppContext.getContext())) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${AppContext.getContext().packageName}"))
            AppContext.getContext().startActivity(intent)
        }

        val alarmManager = AlarmManager()
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

            Spacer(Modifier.width(56.dp))
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
                icon = Icons.Default.Notifications,
                label = "Alerts",
                isSelected = selectedItem.value == Icons.Default.Notifications,
                onClick = {
                    selectedItem.value = Icons.Default.Notifications
                    navController.navigate(ScreenRoutes.AlertsScreen) { popUpTo(0) }
                }
            )
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(ScreenRoutes.MapScreenFromNavBar)
                selectedItem.value = Icons.Default.Search
            },
            contentColor = BabyBlue,
            modifier = Modifier
                .size(65.dp)
                .align(Alignment.TopCenter)
                .shadow(8.dp, shape = CircleShape),


        ) {
            Icon(Icons.Default.Search, contentDescription = "Add")
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
fun AppNavigation() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            CurvedBottomNavigationBar(navController)
        }
    ) { paddingValues ->
        SetUpNavHost(navController, paddingValues)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmIndicatorUI(
    alarmTime: LocalTime,
    isActive: Boolean,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDismiss: () -> Unit,
    alarmManager: AlarmManager
) {
    var isExpanded by remember { mutableStateOf(false) }
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else 100.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 200)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Alarm",
                    modifier = Modifier.size(32.dp),
                    tint = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Time display
                Column {
                    Text(
                        text = "Alarm",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = alarmTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                        style = MaterialTheme.typography.displaySmall,
                        color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Toggle switch
                Switch(
                    checked = isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),


                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                // Expanded controls
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Edit button
                    FilledTonalButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Dismiss button
                    FilledTonalButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dismiss")
                    }
                }

                // Optional: Repeat days indicator
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Repeats: Weekdays",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Preview
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun AlarmIndicatorUIPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AlarmIndicatorUI(
                alarmTime = LocalTime.of(7, 30),
                isActive = true,
                onToggle = {},
                onEdit = {},
                onDismiss = {},
                alarmManager = AlarmManager()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AlarmIndicatorUI(
                alarmTime = LocalTime.of(9, 15),
                isActive = false,
                onToggle = {},
                onEdit = {},
                onDismiss = {},
                alarmManager = AlarmManager()
            )
        }
    }
}