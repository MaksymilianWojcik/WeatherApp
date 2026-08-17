package com.mw.medical.weatherapp.feature.forecast.domain.model

import java.time.LocalDate

data class DailyForecast(
    val date: LocalDate,
    val minTemperature: Double,
    val maxTemperature: Double,
    val condition: WeatherCondition,
)
