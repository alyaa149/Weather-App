package com.example.weatherapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Clouds
import com.example.weatherapp.data.models.Coord
import com.example.weatherapp.data.models.Main
import com.example.weatherapp.data.models.Sys
import com.example.weatherapp.data.models.Weather
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.models.Wind
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {
    private lateinit var database: WeatherDataBase
    private lateinit var dao: WeatherDao

    @Before
    fun setup(){
        database= Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).build()
        dao=database.getWeatherDao()
    }

    @After
    fun tearDown() =database.close()

    @Test
    fun insertWeather_insertFavCity_getFavCitySuccessfully() = runTest {
        // Given
        val weatherResponse = WeatherResponse(
            main = Main(temp = 25.0, temp_min = 20.0, temp_max = 30.0, pressure = "1013", humidity = "60", sea_level = "1013", grnd_level = "998", feels_like = "25.0"),
            weather = listOf(Weather(id = "800", main = "Clear", description = "clear sky", icon = "01d")),
            wind = Wind(speed = 5.5, deg = 180),
            clouds = Clouds(all = 10),
            sys = Sys(country = "US", sunrise = 1629876543, sunset = 1629923456),
            name = "New York",
            dt_txt = "2024-03-30 12:00:00",
            visibility = 10000
        )

        val favCity = City(
            id = 700,
            address = "address1",
            lat = 31.27077,
            lon = 30.007815,
            weatherResponse = weatherResponse
        )

        // When: Insert city
        dao.insertWeather(favCity)

        // Then: Get the inserted city
        val result = dao.getAllFavs().first()
        val insertedCity = result.find { it.id == favCity.id }

        assertNotNull(insertedCity)
        assertThat(insertedCity!!.id, `is`(favCity.id))
    }
    @Test
    fun insertWeather_updateFavCity_cityUpdatedSuccessfully() = runTest {
        // Given (Insert first city)
        val weatherResponse = WeatherResponse(
            main = Main(temp = 25.0, temp_min = 20.0, temp_max = 30.0, pressure = "1013", humidity = "60", sea_level = "1013", grnd_level = "998", feels_like = "25.0"),
            weather = listOf(Weather(id = "800", main = "Clear", description = "clear sky", icon = "01d")),
            wind = Wind(speed = 5.5, deg = 180),
            clouds = Clouds(all = 10),
            sys = Sys(country = "US", sunrise = 1629876543, sunset = 1629923456),
            name = "New York",
            dt_txt = "2024-03-30 12:00:00",
            visibility = 10000
        )

        val favCity = City(
            id = 700,  // Primary key
            address = "address1",
            lat = 31.27077,
            lon = 30.007815,
            weatherResponse = weatherResponse
        )
        dao.insertWeather(favCity)

        // When (Update city at same lat/lon)
        val updatedCity = City(
            id = 700,
            address = "address2",
            lat = 31.27077,
            lon = 30.007815,
            weatherResponse = weatherResponse
        )
        dao.insertWeather(updatedCity)

        // Then (Verify if updated city replaced the old one)
        val result = dao.getAllFavs().first() // Get latest data

        val insertedCity = result.find { it.lat == updatedCity.lat && it.lon == updatedCity.lon }
        assertNotNull(insertedCity)

        // Check if updated data is correct
        assertThat(insertedCity!!.id, `is`(700)) // ID should remain the same
        assertThat(insertedCity.address, `is`("address2")) // Address should be updated
    }



}
