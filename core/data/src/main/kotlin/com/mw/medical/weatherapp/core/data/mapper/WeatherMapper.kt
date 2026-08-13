package com.mw.medical.weatherapp.core.data.mapper

import com.mw.medical.weatherapp.core.data.remote.dto.CurrentWeatherResponse
import com.mw.medical.weatherapp.core.data.remote.dto.WeatherDto
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import com.mw.medical.weatherapp.core.domain.model.WeatherKind

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
        kind = dto.icon.toWeatherKind(),
        isNight = dto.icon.endsWith("n"),
    )
}

private fun String.toWeatherKind(): WeatherKind {
    return when (take(2)) {
        "01" -> WeatherKind.Clear
        "02" -> WeatherKind.FewClouds
        "03" -> WeatherKind.ScatteredClouds
        "04" -> WeatherKind.BrokenClouds
        "09" -> WeatherKind.ShowerRain
        "10" -> WeatherKind.Rain
        "11" -> WeatherKind.Thunderstorm
        "13" -> WeatherKind.Snow
        "50" -> WeatherKind.Mist
        else -> WeatherKind.Unknown
    }
}
