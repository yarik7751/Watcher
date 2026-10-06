package com.yarik.watcher.logic.source.remote.weather.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class WeatherDto(
    @SerialName("current")
    val current: CurrentWeather,
    @SerialName("current_units")
    val currentUnits: CurrentUnits,
    @SerialName("elevation")
    val elevation: Double,
    @SerialName("generationtime_ms")
    val generationtimeMs: Double,
    @SerialName("hourly")
    val hourly: Hourly,
    @SerialName("hourly_units")
    val hourlyUnits: HourlyUnits,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    @SerialName("utc_offset_seconds")
    val utcOffsetSeconds: Int
) {
    @Serializable
    class CurrentWeather(
        @SerialName("apparent_temperature")
        val apparentTemperature: Double,
        @SerialName("interval")
        val interval: Int,
        @SerialName("temperature_2m")
        val temperature2m: Double,
        @SerialName("time")
        val time: String,
        @SerialName("weather_code")
        val weatherCode: Int
    )

    @Serializable
    class CurrentUnits(
        @SerialName("apparent_temperature")
        val apparentTemperature: String,
        @SerialName("interval")
        val interval: String,
        @SerialName("temperature_2m")
        val temperature2m: String,
        @SerialName("time")
        val time: String,
        @SerialName("weather_code")
        val weatherCode: String
    )

    @Serializable
    class HourlyUnits(
        @SerialName("apparent_temperature")
        val apparentTemperature: String,
        @SerialName("temperature_2m")
        val temperature2m: String,
        @SerialName("time")
        val time: String,
        @SerialName("weather_code")
        val weatherCode: String
    )

    @Serializable
    class Hourly(
        @SerialName("apparent_temperature")
        val apparentTemperature: List<Double>,
        @SerialName("temperature_2m")
        val temperature2m: List<Double>,
        @SerialName("time")
        val time: List<String>,
        @SerialName("weather_code")
        val weatherCode: List<Int>
    )
}