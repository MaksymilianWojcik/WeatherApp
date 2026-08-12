package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.compose.runtime.Immutable
import com.mw.medical.weatherapp.core.mvi.UiAction
import com.mw.medical.weatherapp.core.mvi.UiState
import com.mw.medical.weatherapp.feature.forecast.presentation.model.ForecastUiModel

object ForecastContract {

    @Immutable
    data class State(
        val isLoading: Boolean,
        val forecast: ForecastUiModel?,
        val hasError: Boolean,
    ) : UiState {
        companion object {
            val Initial = State(isLoading = false, forecast = null, hasError = false)
        }
    }

    sealed interface Action : UiAction {
        data object Load : Action
        data object Retry : Action
    }
}
