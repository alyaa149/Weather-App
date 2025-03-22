package com.example.weatherapp.data.remote

import com.example.weatherapp.Response
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.sharedprefrences.WeatherSharedPrefrences
import com.example.weatherapp.data.models.WeatherForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl  (
 private var weatherService: WeatherService
) : RemoteDataSource{
    private val apiKey = "a48ab7f2ea1db8788b4a980035313863"
    override suspend fun getInfoFromLatLonAndUnitAndLang(
    ): Flow<WeatherResponse> {
       val lat:Double = WeatherSharedPrefrences().getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0
        val lon:Double = WeatherSharedPrefrences().getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0
        val units = WeatherSharedPrefrences().getData(AppStrings().TEMPUNITKEY) ?: "metric"
        val lang = WeatherSharedPrefrences().getData(AppStrings().LANGUAGEKEY) ?: "en"

        return flow {
            val response = weatherService.getInfoFromLatLonAndUnitAndLang(lat, lon, apiKey, units, lang)
            emit(response)
        }.catch { e->
            Response.Failure(e)
        }
    }

    override suspend fun get5DaysWeatherForecast(

    ): Flow<WeatherForecastResponse> {
        val lat:Double = WeatherSharedPrefrences().getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0
        val lon:Double = WeatherSharedPrefrences().getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0
        val units = WeatherSharedPrefrences().getData(AppStrings().TEMPUNITKEY) ?: "metric"
        val lang = WeatherSharedPrefrences().getData(AppStrings().LANGUAGEKEY) ?: "en"
        return flow {
            val response = weatherService.get5DaysWeatherForecast(lat, lon, apiKey, units, lang)
            emit(response)
        }.catch { e->
            Response.Failure(e)
        }
    }
    override suspend fun getHourlyWeatherForecast(

    ): Flow<WeatherForecastResponse> {
        val lat:Double = WeatherSharedPrefrences().getData(AppStrings().LATITUDEKEY)?.toDouble() ?: 0.0
        val lon:Double = WeatherSharedPrefrences().getData(AppStrings().LONGITUDEKEY)?.toDouble() ?: 0.0
        return flow {
            val response = weatherService.getHourlyWeatherForecast(lat, lon, apiKey)
            emit(response)
        }.catch { e ->
            Response.Failure(e)
        }
    }


}


