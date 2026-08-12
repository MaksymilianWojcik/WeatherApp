package com.mw.medical.weatherapp.core.data.mapper

import com.mw.medical.weatherapp.core.data.remote.dto.CurrentWeatherResponse
import com.mw.medical.weatherapp.core.data.remote.dto.WeatherDto
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition

internal fun CurrentWeatherResponse.toDomain(): CurrentWeather {
    return CurrentWeather(
        temperature = main.temp,
        condition = weather.toCondition(),
        cityName = name,
        country = sys.country,
    )
}

internal fun List<WeatherDto>.toCondition(): WeatherCondition {
    val dto = firstOrNull() ?: return WeatherCondition.EMPTY
    return WeatherCondition(
        description = dto.description,
        iconCode = dto.icon,
    )
}
