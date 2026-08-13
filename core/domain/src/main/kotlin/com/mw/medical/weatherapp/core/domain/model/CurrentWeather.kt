package com.mw.medical.weatherapp.core.domain.model

data class CurrentWeather(
    val temperature: Double,
    val condition: WeatherCondition,
    val cityName: String,
    val country: String,
)
