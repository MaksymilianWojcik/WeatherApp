package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.lifecycle.viewModelScope
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.usecase.GetDeviceLocationUseCase
import com.mw.medical.weatherapp.core.presentation.mvi.MviViewModel
import com.mw.medical.weatherapp.feature.forecast.domain.model.CurrentWeather
import com.mw.medical.weatherapp.feature.forecast.domain.model.Forecast
import com.mw.medical.weatherapp.feature.forecast.domain.usecase.GetCurrentWeatherUseCase
import com.mw.medical.weatherapp.feature.forecast.domain.usecase.GetForecastUseCase
import com.mw.medical.weatherapp.feature.forecast.presentation.ForecastContract.SectionState
import com.mw.medical.weatherapp.feature.forecast.presentation.model.toLocationLabel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
internal class ForecastViewModel @Inject constructor(
    private val getDeviceLocation: GetDeviceLocationUseCase,
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    private val getForecast: GetForecastUseCase,
) : MviViewModel<ForecastContract.State, ForecastContract.Action, Nothing>(
    ForecastContract.State.Initial,
) {
    private var loadJob: Job? = null

    override fun onAction(action: ForecastContract.Action) {
        when (action) {
            is ForecastContract.Action.OnLocationPermissionResult -> onLocationPermissionResult(action.status)
            is ForecastContract.Action.LocationSelected -> onLocationSelected(action)
            ForecastContract.Action.Retry -> load()
            ForecastContract.Action.RefreshRequested -> refresh()
        }
    }

    private fun onLocationPermissionResult(status: LocationPermissionStatus) {
        if (state.value.locationPermission == status) {
            return
        }
        updateState { copy(locationPermission = status) }
        if (state.value.isPermissionGranted && !state.value.hasSelectedLocation) {
            load()
        }
    }

    private fun onLocationSelected(action: ForecastContract.Action.LocationSelected) {
        val coordinates = Coordinates(
            latitude = action.latitude,
            longitude = action.longitude,
        )
        if (coordinates == state.value.selectedCoordinates) {
            return
        }
        updateState { copy(selectedCoordinates = coordinates) }
        load()
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
            loadCurrentTarget()
        }
    }

    private fun refresh() {
        if (state.value.isRefreshing) {
            return
        }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            updateState { copy(isRefreshing = true) }
            try {
                loadCurrentTarget()
            } finally {
                updateState { copy(isRefreshing = false) }
            }
        }
    }

    private suspend fun loadCurrentTarget() {
        val coordinates = state.value.selectedCoordinates
        if (coordinates != null) {
            loadForCoordinates(coordinates)
        } else {
            when (val location = getDeviceLocation()) {
                is Result.Success -> loadForCoordinates(location.value)
                is Result.Failure -> updateState {
                    copy(
                        current = SectionState.Error,
                        forecast = SectionState.Error,
                    )
                }
            }
        }
    }

    private suspend fun loadForCoordinates(coordinates: Coordinates) {
        supervisorScope {
            launch {
                val result = getCurrentWeather(coordinates)
                updateState {
                    copy(
                        current = result.toSection { it.toUiModel() },
                        locationName = when (result) {
                            is Result.Success -> result.value.toLocationLabel()
                            is Result.Failure -> locationName
                        },
                    )
                }
            }
            launch {
                val result = getForecast(coordinates)
                updateState { copy(forecast = result.toSection { it.toUiModel() }) }
            }
        }
    }
}

private fun <T, R> Result<T>.toSection(transform: (T) -> R): SectionState<R> {
    return when (this) {
        is Result.Success -> SectionState.Content(transform(value))
        is Result.Failure -> SectionState.Error
    }
}
