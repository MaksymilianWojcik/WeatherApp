package com.mw.medical.weatherapp.feature.forecast.presentation.model

import com.mw.medical.weatherapp.core.designsystem.icon.WeatherIcon

data class CurrentWeatherUiModel(
    val temperature: String,
    val description: String,
    val icon: WeatherIcon,
)
