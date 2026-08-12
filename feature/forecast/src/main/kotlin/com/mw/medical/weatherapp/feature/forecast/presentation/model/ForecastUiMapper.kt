package com.mw.medical.weatherapp.feature.forecast.presentation.model

import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import kotlin.math.roundToInt

internal fun CurrentWeather.toUiModel(): ForecastUiModel {
    return ForecastUiModel(
        temperature = "${temperature.roundToInt()}°",
        description = condition.description,
        iconCode = condition.iconCode,
    )
}
