package com.mw.medical.weatherapp.core.data.mapper

import com.mw.medical.weatherapp.core.data.remote.dto.GeocodingCityDto
import com.mw.medical.weatherapp.core.domain.model.City
import com.mw.medical.weatherapp.core.domain.model.Coordinates

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
