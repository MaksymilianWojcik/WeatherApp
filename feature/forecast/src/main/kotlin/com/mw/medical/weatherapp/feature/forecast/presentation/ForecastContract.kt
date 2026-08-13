package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.compose.runtime.Immutable
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.mvi.UiAction
import com.mw.medical.weatherapp.core.mvi.UiState
import com.mw.medical.weatherapp.feature.forecast.presentation.model.CurrentWeatherUiModel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.ForecastUiModel

internal object ForecastContract {

    @Immutable
    data class State(
        val locationPermission: LocationPermissionStatus,
        val locationName: String?,
        val current: SectionState<CurrentWeatherUiModel>,
        val forecast: SectionState<ForecastUiModel>,
        val isRefreshing: Boolean = false,
        val selectedCoordinates: Coordinates? = null,
    ) : UiState {
        val isPermissionGranted = locationPermission == LocationPermissionStatus.Granted
        val hasSelectedLocation = selectedCoordinates != null
        val showPermissionPrompt = !hasSelectedLocation && locationPermission == LocationPermissionStatus.Denied
        val showSettingsPrompt = !hasSelectedLocation && locationPermission == LocationPermissionStatus.PermanentlyDenied

        companion object {
            val Initial = State(
                locationPermission = LocationPermissionStatus.Denied,
                locationName = null,
                current = SectionState.Loading,
                forecast = SectionState.Loading,
                isRefreshing = false,
                selectedCoordinates = null,
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
        data class LocationSelected(
            val latitude: Double,
            val longitude: Double,
        ) : Action
        data object Retry : Action
        data object RefreshRequested : Action
    }
}

internal enum class LocationPermissionStatus { Granted, Denied, PermanentlyDenied }
