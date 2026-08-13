package com.mw.medical.weatherapp.feature.search.presentation.model

import com.mw.medical.weatherapp.core.domain.model.City

internal fun City.toUiModel(): CityUiModel {
    return CityUiModel(
        name = name,
        subtitle = listOfNotNull(state, country).joinToString(separator = ", "),
        latitude = coordinates.latitude,
        longitude = coordinates.longitude,
    )
}
