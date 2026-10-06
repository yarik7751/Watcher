package com.yarik.watcher.logic.source.remote.weather

import com.yarik.watcher.logic.source.remote.weather.model.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

// https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&hourly=temperature_2m,weather_code,apparent_temperature&current=temperature_2m,weather_code,apparent_temperature&timezone=Europe%2FMoscow
interface WeatherNetworkSource {

    @GET("/v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String,
        @Query("current") current: String,
        @Query("timezone") timezone: String,
    ): WeatherDto
}