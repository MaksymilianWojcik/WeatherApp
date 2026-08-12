package com.mw.medical.weatherapp.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CurrentWeatherResponse(
    val name: String,
    val sys: SysDto,
    val main: MainDto,
    val weather: List<WeatherDto>,
)

@Serializable
internal data class SysDto(
    val country: String,
)

@Serializable
internal data class MainDto(
    val temp: Double,
)

@Serializable
internal data class WeatherDto(
    val description: String,
    val icon: String,
)
