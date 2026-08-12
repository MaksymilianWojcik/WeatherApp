package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.compose.runtime.Immutable
import com.mw.medical.weatherapp.core.mvi.UiAction
import com.mw.medical.weatherapp.core.mvi.UiState
import com.mw.medical.weatherapp.feature.forecast.presentation.model.ForecastUiModel

object ForecastContract {

    @Immutable
    data class State(
        val locationPermission: LocationPermissionStatus,
        val isLoading: Boolean,
        val forecast: ForecastUiModel?,
        val hasError: Boolean,
    ) : UiState {
        val isPermissionDenied = locationPermission == LocationPermissionStatus.Denied
        val isPermissionPermanentlyDenied = locationPermission == LocationPermissionStatus.PermanentlyDenied

        companion object {
            val Initial = State(
                locationPermission = LocationPermissionStatus.Denied,
                isLoading = false,
                forecast = null,
                hasError = false,
            )
        }
    }

    sealed interface Action : UiAction {
        data class OnLocationPermissionResult(val status: LocationPermissionStatus) : Action
        data object Retry : Action
    }
}

enum class LocationPermissionStatus { Granted, Denied, PermanentlyDenied }
