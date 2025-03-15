package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.WeatherResponse

class RemoteDataSourceImpl  (
 private var weatherService: WeatherService
) : RemoteDataSource{
    private val apiKey = "a48ab7f2ea1db8788b4a980035313863"

    override suspend fun getInfoFromLatLonAndUnitAndLang(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): WeatherResponse {
        return weatherService.getInfoFromLatLonAndUnitAndLang(lat, lon, apiKey, units, lang)

    }

    override suspend fun getCurrentFiveDaysWather() {
        TODO("Not yet implemented")
    }


}