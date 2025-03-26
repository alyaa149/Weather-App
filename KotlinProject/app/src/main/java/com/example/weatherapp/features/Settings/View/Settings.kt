package com.example.weatherapp.features.Settings.View

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weatherapp.R
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.Location.Location
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.ui.theme.Roze

@Composable
fun SettingsUI(
    navigateToMap : () -> Unit,
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
        SettingSection(title = "Choose Location") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedLocation == "GPS",
                    onClick = {
                        selectedLocation = "GPS"
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

        Text(text = stringResource(R.string.latitude, selectedLocationLat), color = Color.White, fontSize = 16.sp)
        Text(text = stringResource(R.string.longitude, selectedLocationLon), color = Color.White, fontSize = 16.sp)

        SettingDropdown(
            title = stringResource(R.string.temperature_unit),
            picPath = R.drawable.tempunit,
            items = listOf(
                stringResource(R.string.kelvin),
                stringResource(R.string.celsius),
                "Fahrenheit"
            ),
            selectedItem = selectedTempUnit
        ) {
            selectedTempUnit = it
            weatherPreferences.putData(
                AppStrings().TEMPUNITKEY,
                when (it) {
                    "Celsius" -> {
                        selectedWindUnit = "meter/second"
                        weatherPreferences.putData(AppStrings().WINDUNITKEY, AppStrings().METER_PER_SECONDKEY)
                        AppStrings().CELSIUSKEY}

                    "Fahrenheit" -> {
                        selectedWindUnit = "mile/hour"
                        weatherPreferences.putData(AppStrings().WINDUNITKEY, AppStrings().MILE_PER_HOURKEY)
                        AppStrings().FAHRENHEITKEY
                    }
                    else -> {
                        selectedWindUnit = "meter/second"
                        weatherPreferences.putData(AppStrings().WINDUNITKEY, AppStrings().METER_PER_SECONDKEY)
                        AppStrings().KELVINKEY}
                }
            )
        }

        SettingDropdown(
            title = stringResource(R.string.wind_speed_unit),
            picPath = R.drawable.windsettings,
            items = listOf("meter/second", "mile/hour"),
            selectedItem = selectedWindUnit
        ) {
            if(it == "mile/hour"){
                selectedTempUnit = "Fahrenheit"
                weatherPreferences.putData(AppStrings().TEMPUNITKEY, AppStrings().FAHRENHEITKEY)
            }else{
                selectedTempUnit = "Celsius"
                weatherPreferences.putData(AppStrings().TEMPUNITKEY, AppStrings().CELSIUSKEY)
            }
            selectedWindUnit = it
            weatherPreferences.putData(AppStrings().WINDUNITKEY, if (it == "meter/second") AppStrings().METER_PER_SECONDKEY else AppStrings().MILE_PER_HOURKEY)
        }

        SettingDropdown(
            title = stringResource(R.string.language),
            picPath = R.drawable.language,
            items = listOf("English", "Arabic"),
            selectedItem = selectedLanguage
        ) {
            selectedLanguage = it
            weatherPreferences.putData(AppStrings().LANGUAGEKEY, if (it == "English") AppStrings().ENGLISHKEY else AppStrings().ARABICKEY)
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
    picPath: Int,
    items: List<String>,
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
                Text(text = selectedItem,fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Blue,)
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
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Blue,
                            ) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


