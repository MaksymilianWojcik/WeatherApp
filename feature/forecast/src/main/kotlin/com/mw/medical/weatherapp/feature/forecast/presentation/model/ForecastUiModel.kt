package com.mw.medical.weatherapp.feature.forecast.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class ForecastUiModel(
    val hourly: List<HourlyForecastUiModel>,
    val daily: List<DailyForecastUiModel>,
)
