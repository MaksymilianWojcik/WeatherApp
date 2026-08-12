package com.mw.medical.weatherapp.feature.search.presentation.model

import com.mw.medical.weatherapp.core.domain.model.City

internal fun City.toUiModel(): CityUiModel {
    return CityUiModel(
        name = name,
        country = country,
        latitude = coordinates.latitude,
        longitude = coordinates.longitude,
    )
}
