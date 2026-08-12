package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.lifecycle.viewModelScope
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.usecase.GetCurrentWeatherForCurrentLocationUseCase
import com.mw.medical.weatherapp.core.domain.usecase.GetForecastForCurrentLocationUseCase
import com.mw.medical.weatherapp.core.mvi.MviViewModel
import com.mw.medical.weatherapp.core.mvi.SideEffect
import com.mw.medical.weatherapp.feature.forecast.presentation.ForecastContract.SectionState
import com.mw.medical.weatherapp.feature.forecast.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
internal class ForecastViewModel @Inject constructor(
    private val getCurrentWeatherForCurrentLocation: GetCurrentWeatherForCurrentLocationUseCase,
    private val getForecastForCurrentLocation: GetForecastForCurrentLocationUseCase,
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
                    current = SectionState.Loading,
                    forecast = SectionState.Loading,
                )
            }
            supervisorScope {
                launch { loadCurrentWeather() }
                launch { loadForecast() }
            }
        }
    }

    private suspend fun loadCurrentWeather() {
        val section = getCurrentWeatherForCurrentLocation().toSection { it.toUiModel() }
        updateState { copy(current = section) }
    }

    private suspend fun loadForecast() {
        val section = getForecastForCurrentLocation().toSection { it.toUiModel() }
        updateState { copy(forecast = section) }
    }
}

private fun <T, R> Result<T>.toSection(transform: (T) -> R): SectionState<R> {
    return when (this) {
        is Result.Success -> SectionState.Content(transform(value))
        is Result.Failure -> SectionState.Error
    }
}
