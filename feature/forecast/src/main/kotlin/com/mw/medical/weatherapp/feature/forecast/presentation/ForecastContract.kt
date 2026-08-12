package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.compose.runtime.Immutable
import com.mw.medical.weatherapp.core.mvi.UiAction
import com.mw.medical.weatherapp.core.mvi.UiState
import com.mw.medical.weatherapp.feature.forecast.presentation.model.CurrentWeatherUiModel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.ForecastUiModel

object ForecastContract {

    @Immutable
    data class State(
        val locationPermission: LocationPermissionStatus,
        val current: SectionState<CurrentWeatherUiModel>,
        val forecast: SectionState<ForecastUiModel>,
    ) : UiState {
        val isPermissionDenied = locationPermission == LocationPermissionStatus.Denied
        val isPermissionPermanentlyDenied = locationPermission == LocationPermissionStatus.PermanentlyDenied

        companion object {
            val Initial = State(
                locationPermission = LocationPermissionStatus.Denied,
                current = SectionState.Loading,
                forecast = SectionState.Loading,
            )
        }
    }

    @Immutable
    sealed interface SectionState<out T> {
        data object Loading : SectionState<Nothing>
        data class Content<out T>(val value: T) : SectionState<T>
        data object Error : SectionState<Nothing>
    }

    sealed interface Action : UiAction {
        data class OnLocationPermissionResult(val status: LocationPermissionStatus) : Action
        data object Retry : Action
    }
}

enum class LocationPermissionStatus { Granted, Denied, PermanentlyDenied }
