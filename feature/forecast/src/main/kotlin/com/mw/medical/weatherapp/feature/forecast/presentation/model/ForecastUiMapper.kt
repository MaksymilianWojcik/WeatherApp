package com.mw.medical.weatherapp.feature.forecast.presentation.model

import com.mw.medical.weatherapp.core.designsystem.icon.WeatherIcon
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
        icon = condition.toIcon(),
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
        icon = condition.toIcon(),
        description = condition.description,
    )
}

internal fun DailyForecast.toUiModel(): DailyForecastUiModel {
    return DailyForecastUiModel(
        day = date.format(dayFormatter()),
        minTemperature = minTemperature.toTemperatureLabel(),
        maxTemperature = maxTemperature.toTemperatureLabel(),
        icon = condition.toIcon(),
        description = condition.description,
    )
}

private fun dayFormatter() = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

private fun Double.toTemperatureLabel() = "${roundToInt()}°"

internal fun WeatherCondition.toIcon(): WeatherIcon {
    return when (kind) {
        WeatherKind.Clear -> if (isNight) WeatherIcon.ClearNight else WeatherIcon.ClearDay
        WeatherKind.FewClouds -> WeatherIcon.FewClouds
        WeatherKind.ScatteredClouds -> WeatherIcon.ScatteredClouds
        WeatherKind.BrokenClouds -> WeatherIcon.BrokenClouds
        WeatherKind.ShowerRain -> WeatherIcon.ShowerRain
        WeatherKind.Rain -> WeatherIcon.Rain
        WeatherKind.Thunderstorm -> WeatherIcon.Thunderstorm
        WeatherKind.Snow -> WeatherIcon.Snow
        WeatherKind.Mist -> WeatherIcon.Mist
        WeatherKind.Unknown -> WeatherIcon.Unknown
    }
}
