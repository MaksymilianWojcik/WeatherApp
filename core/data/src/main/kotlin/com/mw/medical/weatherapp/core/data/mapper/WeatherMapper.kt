package com.mw.medical.weatherapp.core.data.mapper

import com.mw.medical.weatherapp.core.data.remote.dto.CurrentWeatherResponse
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition

internal fun CurrentWeatherResponse.toDomain(): CurrentWeather {
    return CurrentWeather(
        temperature = main.temp,
        condition = weather.firstOrNull()
            ?.let {
                WeatherCondition(
                    description = it.description,
                    iconCode = it.icon,
                )
            }
            ?: WeatherCondition.EMPTY, // might switch to error later
    )
}
