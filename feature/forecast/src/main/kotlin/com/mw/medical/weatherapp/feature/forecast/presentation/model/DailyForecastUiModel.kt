package com.mw.medical.weatherapp.feature.forecast.presentation.model

import com.mw.medical.weatherapp.core.designsystem.icon.WeatherIcon

data class DailyForecastUiModel(
    val day: String,
    val minTemperature: String,
    val maxTemperature: String,
    val icon: WeatherIcon,
    val description: String,
)
