package com.mw.medical.weatherapp.feature.forecast.domain.model

import java.time.LocalDateTime

data class HourlyForecast(
    val time: LocalDateTime,
    val temperature: Double,
    val condition: WeatherCondition,
)
