package com.mw.medical.weatherapp.feature.forecast.domain.model

data class CurrentWeather(
    val temperature: Double,
    val condition: WeatherCondition,
    val cityName: String,
    val country: String,
)
