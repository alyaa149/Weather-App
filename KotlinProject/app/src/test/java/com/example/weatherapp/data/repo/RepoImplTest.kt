package com.example.weatherapp.data.repo

import com.example.weatherapp.FakeLocalDataSourceImpl
import com.example.weatherapp.FakeRemoteDataSource
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.models.Clouds
import com.example.weatherapp.data.models.Main
import com.example.weatherapp.data.models.Reminder
import com.example.weatherapp.data.models.Sys
import com.example.weatherapp.data.models.Weather
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.models.Wind
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime


class RepoImplTest{

    private lateinit var fakeRemoteDataSource: RemoteDataSource
    private lateinit var fakeLocalDataSource: LocalDataSource
    private lateinit var repo: Repo

    @Before
    fun setup(){
        fakeRemoteDataSource = FakeRemoteDataSource()
        fakeLocalDataSource = FakeLocalDataSourceImpl()
        repo = RepoImpl(fakeRemoteDataSource,fakeLocalDataSource)

    }
    @Test
    fun updateWeatherHomeAndRetrieve_Success_validDataWeatherDataRetrieved() = runTest {
        // Given: A repository with fake data sources and valid weather data

        val weatherResponse = WeatherResponse(
            main = Main(temp = 22.0, temp_min = 20.0, temp_max = 24.0, pressure = "1012", humidity = "65", sea_level = "1012", grnd_level = "1008", feels_like = "22.0"),
            weather = listOf(Weather(id = "801", main = "Cloudy", description = "partly cloudy", icon = "02d")),
            wind = Wind(speed = 4.5, deg = 150),
            clouds = Clouds(all = 30),
            sys = Sys(country = "US", sunrise = 1629876543, sunset = 1629923456),
            name = "Los Angeles",
            dt_txt = "2024-03-30 14:00:00",
            visibility = 12000
        )

        val forecastResponse = WeatherForecastResponse("200", 0, 0, emptyList(), City(0, "Unknown", 0.0, 0.0, weatherResponse))
        val lat = 34.0522
        val lon = -118.2437

        // When: Update weather home
        repo.updateWeatherHome(lat, lon, weatherResponse, forecastResponse)

        // Then: Retrieve and verify
        val retrievedWeather = repo.getAllDetailsWeatherFromLatLonHome(lat, lon).first()

        assertNotNull(retrievedWeather)
        assertThat(retrievedWeather!!.weatherResponse?.main?.temp, `is`(22.0))
        assertThat(retrievedWeather.weatherResponse?.weather?.first()?.main, `is`("Cloudy"))
    }

    @Test
    fun deleteReminder_reminderDeletedSuccessfully() = runTest {
        // Given: Insert and delete a reminder
        val reminder = Reminder(id = 3,time = LocalDateTime.now(), type = "NOTIFICATION")
        repo.insertReminder(reminder)

        // When: Delete the reminder
        repo.deleteReminder(reminder.id)

        // Then: Verify it's deleted
        val reminders = repo.getAllReminders().first()
        assertThat(reminders.isEmpty(), `is`(true))
    }

}