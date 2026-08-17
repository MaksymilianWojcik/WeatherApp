package com.mw.medical.weatherapp.feature.search.data.mapper

import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.feature.search.data.remote.dto.GeocodingCityDto
import com.mw.medical.weatherapp.feature.search.domain.model.City

internal fun GeocodingCityDto.toDomain(): City {
    return City(
        name = name,
        state = state,
        country = country,
        coordinates = Coordinates(
            latitude = lat,
            longitude = lon,
        ),
    )
}
