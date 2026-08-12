package com.mw.medical.weatherapp.feature.forecast.presentation.model

data class DailyForecastUiModel(
    val day: String,
    val minTemperature: String,
    val maxTemperature: String,
    val icon: String,
    val description: String,
)
