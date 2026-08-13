package com.mw.medical.weatherapp.core.domain.model

data class City(
    val name: String,
    val state: String?,
    val country: String,
    val coordinates: Coordinates,
)
