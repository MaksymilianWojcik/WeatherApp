package com.mw.medical.weatherapp.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class GeocodingCityDto(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null,
)
