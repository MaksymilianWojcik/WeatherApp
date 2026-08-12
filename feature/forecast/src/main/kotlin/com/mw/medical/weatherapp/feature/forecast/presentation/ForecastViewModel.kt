package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.lifecycle.viewModelScope
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.usecase.GetCurrentWeatherForCurrentLocationUseCase
import com.mw.medical.weatherapp.core.mvi.MviViewModel
import com.mw.medical.weatherapp.core.mvi.SideEffect
import com.mw.medical.weatherapp.feature.forecast.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ForecastViewModel @Inject constructor(
    private val getCurrentWeatherForCurrentLocation: GetCurrentWeatherForCurrentLocationUseCase,
) : MviViewModel<ForecastContract.State, ForecastContract.Action, SideEffect>(
    ForecastContract.State.Initial,
) {
    private var loadJob: Job? = null

    override fun onAction(action: ForecastContract.Action) {
        when (action) {
            is ForecastContract.Action.OnLocationPermissionResult -> onLocationPermissionResult(action.status)
            ForecastContract.Action.Retry -> load()
        }
    }

    private fun onLocationPermissionResult(status: LocationPermissionStatus) {
        val permissionChanged = state.value.locationPermission != status
        updateState { copy(locationPermission = status) }
        if (status == LocationPermissionStatus.Granted && permissionChanged) {
            load()
        }
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            updateState {
                copy(
                    isLoading = true,
                    hasError = false,
                )
            }
            when (val result = getCurrentWeatherForCurrentLocation()) {
                is Result.Success -> updateState {
                    copy(
                        isLoading = false,
                        hasError = false,
                        forecast = result.value.toUiModel(),
                    )
                }
                is Result.Failure -> updateState {
                    copy(
                        isLoading = false,
                        hasError = true,
                    )
                }
            }
        }
    }
}
