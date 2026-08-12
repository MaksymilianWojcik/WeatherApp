package com.mw.medical.weatherapp.feature.forecast.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class ForecastUiModel(
    val temperature: String,
    val description: String,
    val iconCode: String,
)
