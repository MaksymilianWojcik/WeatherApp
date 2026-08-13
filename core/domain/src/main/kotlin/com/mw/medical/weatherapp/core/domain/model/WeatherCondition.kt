package com.mw.medical.weatherapp.core.domain.model

data class WeatherCondition(
    val description: String,
    val kind: WeatherKind,
    val isNight: Boolean,
) {
    companion object {
        val EMPTY = WeatherCondition(
            description = "",
            kind = WeatherKind.Unknown,
            isNight = false,
        )
    }
}
