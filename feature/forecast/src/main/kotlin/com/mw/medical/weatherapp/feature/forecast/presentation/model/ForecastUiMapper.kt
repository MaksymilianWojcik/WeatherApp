package com.mw.medical.weatherapp.feature.forecast.presentation.model

import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.DailyForecast
import com.mw.medical.weatherapp.core.domain.model.Forecast
import com.mw.medical.weatherapp.core.domain.model.HourlyForecast
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import com.mw.medical.weatherapp.core.domain.model.WeatherKind
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")

internal fun CurrentWeather.toUiModel(): CurrentWeatherUiModel {
    return CurrentWeatherUiModel(
        temperature = temperature.toTemperatureLabel(),
        description = condition.description,
        icon = condition.toEmoji(),
    )
}

internal fun CurrentWeather.toLocationLabel(): String {
    return "$cityName, ${country.toCountryDisplayName()}"
}

private fun String.toCountryDisplayName(): String {
    return Locale("", this).displayCountry
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
        icon = condition.toEmoji(),
        description = condition.description,
    )
}

internal fun DailyForecast.toUiModel(): DailyForecastUiModel {
    return DailyForecastUiModel(
        day = date.format(dayFormatter()),
        minTemperature = minTemperature.toTemperatureLabel(),
        maxTemperature = maxTemperature.toTemperatureLabel(),
        icon = condition.toEmoji(),
        description = condition.description,
    )
}

private fun dayFormatter() = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

private fun Double.toTemperatureLabel() = "${roundToInt()}°"

internal fun WeatherCondition.toEmoji(): String {
    return when (kind) {
        WeatherKind.Clear -> if (isNight) "🌙" else "☀️"
        WeatherKind.FewClouds -> "🌤️"
        WeatherKind.ScatteredClouds -> "⛅"
        WeatherKind.BrokenClouds -> "☁️"
        WeatherKind.ShowerRain -> "🌧️"
        WeatherKind.Rain -> "🌦️"
        WeatherKind.Thunderstorm -> "⛈️"
        WeatherKind.Snow -> "❄️"
        WeatherKind.Mist -> "🌫️"
        WeatherKind.Unknown -> "🌡️"
    }
}
