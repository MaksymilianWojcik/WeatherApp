package com.mw.medical.weatherapp.feature.forecast.presentation.model

import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.DailyForecast
import com.mw.medical.weatherapp.core.domain.model.Forecast
import com.mw.medical.weatherapp.core.domain.model.HourlyForecast
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

internal fun CurrentWeather.toUiModel(): CurrentWeatherUiModel {
    return CurrentWeatherUiModel(
        temperature = temperature.toTemperatureLabel(),
        description = condition.description,
        icon = condition.iconCode.toWeatherEmoji(),
    )
}

internal fun Forecast.toUiModel(): ForecastUiModel {
    return ForecastUiModel(
        hourly = hourly.map { it.toUiModel() },
        daily = daily.map { it.toUiModel() },
    )
}

internal fun HourlyForecast.toUiModel(): HourlyForecastUiModel {
    return HourlyForecastUiModel(
        time = time.format(hourFormatter),
        temperature = temperature.toTemperatureLabel(),
        icon = condition.iconCode.toWeatherEmoji(),
        description = condition.description,
    )
}

internal fun DailyForecast.toUiModel(): DailyForecastUiModel {
    return DailyForecastUiModel(
        day = date.format(dayFormatter),
        minTemperature = minTemperature.toTemperatureLabel(),
        maxTemperature = maxTemperature.toTemperatureLabel(),
        icon = condition.iconCode.toWeatherEmoji(),
        description = condition.description,
    )
}

private fun Double.toTemperatureLabel() = "${roundToInt()}°"

private fun String.toWeatherEmoji(): String {
    return when (take(2)) {
        "01" -> if (endsWith("n")) "🌙" else "☀️"
        "02" -> "🌤️"
        "03" -> "⛅"
        "04" -> "☁️"
        "09" -> "🌧️"
        "10" -> "🌦️"
        "11" -> "⛈️"
        "13" -> "❄️"
        "50" -> "🌫️"
        else -> "🌡️"
    }
}
