package com.example.weatherapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Clouds
import com.example.weatherapp.data.models.Main
import com.example.weatherapp.data.models.Sys
import com.example.weatherapp.data.models.Weather
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.models.Wind
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceImplTest{
    private lateinit var  database :WeatherDataBase
    private lateinit var dao :WeatherDao
    private lateinit var reminderDao: ReminderDao
    private lateinit var localDataSource: LocalDataSourceImpl
    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).allowMainThreadQueries().build()

        dao =database.getWeatherDao()
        reminderDao=database.getReminderDao()
        localDataSource = LocalDataSourceImpl(dao,reminderDao)
    }
    @After
    fun tearDown(){
        database.close()
    }
    @Test
    fun deleteFavCity() = runTest {
        // Given: Insert a city first
        val city = City(
            id = 3,
            address = "Chicago",
            lat = 41.8781,
            lon = -87.6298,
            weatherResponse = WeatherResponse(
                main = Main(temp = 15.0, temp_min = 10.0, temp_max = 20.0, pressure = "1018", humidity = "80", sea_level = "1018", grnd_level = "1015", feels_like = "15.0"),
                weather = listOf(Weather(id = "803", main = "Cloudy", description = "broken clouds", icon = "04d")),
                wind = Wind(speed = 2.5, deg = 120),
                clouds = Clouds(all = 50),
                sys = Sys(country = "US", sunrise = 1629876543, sunset = 1629923456),
                name = "Chicago",
                dt_txt = "2024-03-30 18:00:00",
                visibility = 9000
            )
        )

        localDataSource.insertWeather(city)

        // When: Delete the city
        localDataSource.deleteWeather(city)

        // Then: Check that the city is no longer in the database
        val result = localDataSource.getAllFavs().first()
        val deletedCity = result.find { it.lat == city.lat && it.lon == city.lon }

        assertNull(deletedCity) // City should be removed
    }
    @Test
    fun getWeatherByLatLon() = runTest {
        // Given: Insert a city first
        val city = City(
            id = 4,
            address = "San Francisco",
            lat = 37.7749,
            lon = -122.4194,
            weatherResponse = WeatherResponse(
                main = Main(temp = 18.0, temp_min = 14.0, temp_max = 22.0, pressure = "1016", humidity = "75", sea_level = "1016", grnd_level = "1014", feels_like = "18.0"),
                weather = listOf(Weather(id = "804", main = "Foggy", description = "dense fog", icon = "50d")),
                wind = Wind(speed = 3.0, deg = 160),
                clouds = Clouds(all = 90),
                sys = Sys(country = "US", sunrise = 1629876543, sunset = 1629923456),
                name = "San Francisco",
                dt_txt = "2024-03-30 06:00:00",
                visibility = 5000
            )
        )

        localDataSource.insertWeather(city)

        // When: Retrieve weather by lat/lon
        val retrievedCity = localDataSource.getWeatherFromLatLonOffline(city.lat, city.lon).first()

        // Then: Verify correct retrieval
        assertNotNull(retrievedCity)
        assertThat(retrievedCity.address, `is`("San Francisco"))
        assertThat(retrievedCity.weatherResponse.weather?.first()?.main, `is`("Foggy")) // Check weather condition
    }




}