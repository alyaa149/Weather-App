package com.example.weatherapp.features.Settings.View

import android.app.Activity
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.recreate
import androidx.navigation.NavHostController
import com.example.weatherapp.R
import com.example.weatherapp.Utils.AppContext
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.Location.Location
import com.example.weatherapp.Utils.MyAppContext
import com.example.weatherapp.Utils.formatNumberBasedOnLanguage
import com.example.weatherapp.Utils.restartActivity
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.applyLanguage
import com.example.weatherapp.features.Settings.ViewModel.SettingsViewModel
import com.example.weatherapp.ui.theme.Roze
import java.util.Locale

@Composable
fun SettingsUI(
    navigateToMap : () -> Unit,
    viewModel: SettingsViewModel,
) {
    val weatherPreferences = remember { sharedPreferencesUtils }

    var selectedLocation by remember { mutableStateOf(weatherPreferences.getData(AppStrings().LOCATIONKEY) ?: "GPS") }
    var selectedLocationLon by remember { mutableStateOf(weatherPreferences.getData(AppStrings().LONGITUDEKEY) ?: "") }
    var selectedLocationLat by remember { mutableStateOf(weatherPreferences.getData(AppStrings().LATITUDEKEY) ?: "") }


    var selectedTempUnit by remember { mutableStateOf(weatherPreferences.getData(AppStrings().TEMPUNITKEY) ?: "Celsius") }
    var selectedWindUnit by remember { mutableStateOf(weatherPreferences.getData(AppStrings().WINDUNITKEY) ?: "meter/second") }
    var selectedLanguage by remember { mutableStateOf(weatherPreferences.getData(AppStrings().LANGUAGEKEY) ?: "English") }
    Log.d("SettingsUI", "Selected Location: $selectedLocation")
    Log.d("SettingsUI", "Selected Temp Unit: ${sharedPreferencesUtils.getData(AppStrings().TEMPUNITKEY)}")
    Log.d("SettingsUI", "Selected Wind Unit: ${sharedPreferencesUtils.getData(AppStrings().WINDUNITKEY)}")
    Log.d("SettingsUI", "Selected Language: ${sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY)}")

    suspend fun updateLocation() {
        val location = Location().getCurrentLocation()
        location?.let {
            selectedLocationLat = it.latitude.toString()
            selectedLocationLon = it.longitude.toString()

            weatherPreferences.putData(AppStrings().LATITUDEKEY, selectedLocationLat)
            weatherPreferences.putData(AppStrings().LONGITUDEKEY, selectedLocationLon)
            Log.d("SettingsUI", "Updated Location: $selectedLocationLat, $selectedLocationLon")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Blue, BabyBlue, Blue)
                )
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Location Selection
        SettingSection(title = stringResource(R.string.choose_location)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedLocation == "GPS",
                    onClick = {
                        selectedLocation = AppContext.getContext().getString(R.string.gps)
                        weatherPreferences.putData(AppStrings().LOCATIONKEY, "GPS")

                        CoroutineScope(Dispatchers.Main).launch {
                            updateLocation()
                        }
                    }
                )
                Text(stringResource(R.string.use_gps), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = selectedLocation == "Map",
                    onClick = {
                        selectedLocation = "Map"
                        weatherPreferences.putData(AppStrings().LOCATIONKEY, "Map")
                        navigateToMap()
                    }
                )

                Text(stringResource(R.string.choose_from_map), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Text(text = stringResource(R.string.latitude, formatNumberBasedOnLanguage( selectedLocationLat)), color = Color.White, fontSize = 16.sp)
        Text(text = stringResource(R.string.longitude,formatNumberBasedOnLanguage(selectedLocationLon)), color = Color.White, fontSize = 16.sp)

        SettingDropdown(
            title = stringResource(R.string.temperature_unit),
            items = listOf(
                stringResource(R.string.kelvin),
                stringResource(R.string.celsius),
                stringResource(R.string.fahrenheit)
            ),
            itemValues = listOf(
                AppStrings().KELVINKEY,
                AppStrings().CELSIUSKEY,
                AppStrings().FAHRENHEITKEY
            ),
            selectedItem = when (selectedTempUnit) {
                AppStrings().KELVINKEY -> stringResource(R.string.kelvin)
                AppStrings().CELSIUSKEY -> stringResource(R.string.celsius)
                AppStrings().FAHRENHEITKEY -> stringResource(R.string.fahrenheit)
                else -> stringResource(R.string.celsius)
            }
        ) { selectedKey ->
            selectedTempUnit = selectedKey
            weatherPreferences.putData(AppStrings().TEMPUNITKEY, selectedKey)

            // Automatically set corresponding wind unit
            val windUnit = when (selectedKey) {
                AppStrings().FAHRENHEITKEY -> AppStrings().MILE_PER_HOURKEY
                else -> AppStrings().METER_PER_SECONDKEY
            }
            selectedWindUnit = windUnit
            weatherPreferences.putData(AppStrings().WINDUNITKEY, windUnit)
        }

        SettingDropdown(
            title = stringResource(R.string.wind_speed_unit),
            items = listOf(
                stringResource(R.string.meter_second),
                stringResource(R.string.mile_hour)
            ),
            itemValues = listOf(
                AppStrings().METER_PER_SECONDKEY,
                AppStrings().MILE_PER_HOURKEY
            ),
            selectedItem = when (selectedWindUnit) {
                AppStrings().MILE_PER_HOURKEY -> stringResource(R.string.mile_hour)
                else -> stringResource(R.string.meter_second)
            }
        ) { selectedKey ->
            selectedWindUnit = selectedKey
            weatherPreferences.putData(AppStrings().WINDUNITKEY, selectedKey)

            val tempUnit = when (selectedKey) {
                AppStrings().MILE_PER_HOURKEY -> AppStrings().FAHRENHEITKEY
                else -> AppStrings().CELSIUSKEY
            }
            selectedTempUnit = tempUnit
            weatherPreferences.putData(AppStrings().TEMPUNITKEY, tempUnit)
        }
        SettingDropdown(
            title = stringResource(R.string.language),
            items = listOf(
                stringResource(R.string.english),
                stringResource(R.string.arabic),
                stringResource(R.string.defaultt)
            ),
            itemValues = listOf("en", "ar", "default"),
            selectedItem = when (selectedLanguage) {
                "ar" -> stringResource(R.string.arabic)
                "en" -> stringResource(R.string.english)
                else -> stringResource(R.string.defaultt)
            }
        ) { languageCode ->
            Log.i("language", "Selected language: $languageCode")

            if (languageCode == "default") {
                sharedPreferencesUtils.putData("settings_language", "default")
                val defaultLanguage = Locale.getDefault().language
                Log.i("language", "Applying system default: $defaultLanguage")
                sharedPreferencesUtils.putData(AppStrings().LANGUAGEKEY, defaultLanguage)
                applyLanguage(defaultLanguage)
            } else {
                sharedPreferencesUtils.putData("settings_language", "normal")
                sharedPreferencesUtils.putData(AppStrings().LANGUAGEKEY, languageCode)
                Log.i("language", "Applying selected language: $languageCode")
                applyLanguage(languageCode)
            }

            restartActivity() // Restart only after updating language
        }



    }
}

//common
@Composable
fun SettingSection(title: String ,content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row (
            modifier = Modifier.padding(top = 20.dp),
        ){

            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp,fontFamily = FontFamily.Monospace,
                color = Blue,)
        }

        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun SettingDropdown(
    title: String,
    items: List<String>,
    itemValues: List<String>, // Add this parameter for the actual values
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val rotateZ by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    SettingSection(title) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Roze, shape = RoundedCornerShape(8.dp))
                .clickable { expanded = !expanded }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedItem, fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Blue,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier
                        .rotate(rotateZ)
                        .background(color = Blue)
                )
            }

            DropdownMenu(
                expanded = expanded,
                modifier = Modifier.background(Color.Gray.copy(alpha = 0.2f)),
                onDismissRequest = { expanded = false }
            ) {
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Blue,
                            )
                        },
                        onClick = {
                            onItemSelected(itemValues[index])
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
