package com.mw.medical.weatherapp.core.domain.model

data class Forecast(
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
)
