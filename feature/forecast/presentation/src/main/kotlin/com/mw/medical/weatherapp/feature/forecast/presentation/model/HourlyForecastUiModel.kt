package com.mw.medical.weatherapp.feature.forecast.presentation.model

import com.mw.medical.weatherapp.core.designsystem.icon.WeatherIcon

data class HourlyForecastUiModel(
    val time: String,
    val temperature: String,
    val icon: WeatherIcon,
    val description: String,
)
