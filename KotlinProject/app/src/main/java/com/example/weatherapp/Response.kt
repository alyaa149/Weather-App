package com.example.weatherapp

sealed class Response<out T> {
    object Loading : Response<Nothing>()
    data class Success<T>(val data: T) : Response<T>()
    data class Failure(val message: Throwable) : Response<Nothing>()
}
