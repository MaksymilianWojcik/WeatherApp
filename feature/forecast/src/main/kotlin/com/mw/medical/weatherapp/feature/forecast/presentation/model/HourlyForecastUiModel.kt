package com.mw.medical.weatherapp.feature.forecast.presentation.model

data class HourlyForecastUiModel(
    val time: String,
    val temperature: String,
    val icon: String,
    val description: String,
)
