package com.example.weatherapp.Settings.View

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weatherapp.R
import com.example.weatherapp.Settings.ViewModel.SettingsViewModel
import com.example.weatherapp.ui.theme.BabyBlue
import com.example.weatherapp.ui.theme.Blue

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SettingsUI(viewModel = SettingsViewModel(), navController = NavHostController(context = LocalContext.current))

}

@Composable
fun SettingsUI(
    viewModel: SettingsViewModel,
    navController: NavHostController
) {
    var selectedLocation by remember { mutableStateOf("GPS") }
    var selectedTempUnit by remember { mutableStateOf("Celsius") }
    var selectedWindUnit by remember { mutableStateOf("meter/second") }
    var selectedLanguage by remember { mutableStateOf("English") }


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
            text = "Settings",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SettingSection(title = "Choose Location", picPath = R.drawable.location) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedLocation == "GPS",
                    onClick = { selectedLocation = "GPS" }
                )
                Text("Use GPS"
,            fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = selectedLocation == "Map",
                    onClick = { }
                )
                Text("Choose from Map",            fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,)
            }
        }

        SettingDropdown(title = "Temperature Unit",picPath= R.drawable.tempunit, items = listOf("Kelvin", "Celsius", "Fahrenheit"), selectedItem = selectedTempUnit) {
            selectedTempUnit = it
        }

        SettingDropdown(title = "Wind Speed Unit", picPath = R.drawable.windsettings, items = listOf("meter/second", "mile /hour"), selectedItem = selectedWindUnit) {
            selectedWindUnit = it
        }

        SettingDropdown(title = "Language",picPath = R.drawable.language, items = listOf("English", "Arabic"), selectedItem = selectedLanguage) {
            selectedLanguage = it
        }
    }
}
//common
@Composable
fun SettingSection(title: String,picPath:Int ,content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row (
            modifier = Modifier.padding(top = 20.dp),
        ){
            Image(
                painter = painterResource(id = picPath),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp,            fontFamily = FontFamily.Monospace,
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

    SettingSection(title,picPath) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
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
                    color = Color.White,)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.rotate(rotateZ).background(color = Blue)
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
                                color = Color.White,
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


