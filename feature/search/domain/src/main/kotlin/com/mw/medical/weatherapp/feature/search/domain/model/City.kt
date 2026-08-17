package com.mw.medical.weatherapp.feature.search.domain.model

import com.mw.medical.weatherapp.core.domain.model.Coordinates

data class City(
    val name: String,
    val state: String?,
    val country: String,
    val coordinates: Coordinates,
)
