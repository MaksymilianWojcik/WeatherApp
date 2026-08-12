package com.mw.medical.weatherapp.core.domain.model

data class WeatherCondition(
    val description: String,
    val iconCode: String,
) {
    companion object {
        val EMPTY = WeatherCondition(
            description = "",
            iconCode = "",
        )
    }
}
