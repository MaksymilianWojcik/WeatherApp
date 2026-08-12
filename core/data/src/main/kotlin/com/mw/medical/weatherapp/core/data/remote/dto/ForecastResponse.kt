package com.mw.medical.weatherapp.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ForecastResponse(
    val list: List<ForecastEntryDto>,
    val city: ForecastCityDto,
)

@Serializable
internal data class ForecastEntryDto(
    val dt: Long,
    val main: ForecastMainDto,
    val weather: List<WeatherDto>,
)

@Serializable
internal data class ForecastMainDto(
    val temp: Double,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
)

@Serializable
internal data class ForecastCityDto(
    val timezone: Int,
)
