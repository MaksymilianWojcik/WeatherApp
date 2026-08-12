package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.lifecycle.viewModelScope
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.Forecast
import com.mw.medical.weatherapp.core.domain.usecase.GetCurrentWeatherForCurrentLocationUseCase
import com.mw.medical.weatherapp.core.domain.usecase.GetCurrentWeatherUseCase
import com.mw.medical.weatherapp.core.domain.usecase.GetForecastForCurrentLocationUseCase
import com.mw.medical.weatherapp.core.domain.usecase.GetForecastUseCase
import com.mw.medical.weatherapp.core.mvi.MviViewModel
import com.mw.medical.weatherapp.core.mvi.SideEffect
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
    private val getCurrentWeatherForCurrentLocation: GetCurrentWeatherForCurrentLocationUseCase,
    private val getForecastForCurrentLocation: GetForecastForCurrentLocationUseCase,
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    private val getForecast: GetForecastUseCase,
) : MviViewModel<ForecastContract.State, ForecastContract.Action, SideEffect>(
    ForecastContract.State.Initial,
) {
    private var loadJob: Job? = null
    private var selectedCoordinates: Coordinates? = null

    override fun onAction(action: ForecastContract.Action) {
        when (action) {
            is ForecastContract.Action.OnLocationPermissionResult -> onLocationPermissionResult(action.status)
            is ForecastContract.Action.LocationSelected -> onLocationSelected(action)
            ForecastContract.Action.Retry -> reload()
        }
    }

    private fun onLocationPermissionResult(status: LocationPermissionStatus) {
        val permissionChanged = state.value.locationPermission != status
        updateState { copy(locationPermission = status) }
        val shouldLoadDeviceLocation = status == LocationPermissionStatus.Granted &&
            permissionChanged &&
            selectedCoordinates == null
        if (shouldLoadDeviceLocation) {
            loadDeviceLocation()
        }
    }

    private fun onLocationSelected(action: ForecastContract.Action.LocationSelected) {
        val coordinates = Coordinates(
            latitude = action.latitude,
            longitude = action.longitude,
        )
        selectedCoordinates = coordinates
        loadCoordinates(coordinates)
    }

    private fun reload() {
        val coordinates = selectedCoordinates
        if (coordinates == null) {
            loadDeviceLocation()
        } else {
            loadCoordinates(coordinates)
        }
    }

    private fun loadDeviceLocation() {
        load(
            loadCurrent = { getCurrentWeatherForCurrentLocation() },
            loadForecast = { getForecastForCurrentLocation() },
        )
    }

    private fun loadCoordinates(coordinates: Coordinates) {
        load(
            loadCurrent = { getCurrentWeather(coordinates) },
            loadForecast = { getForecast(coordinates) },
        )
    }

    private fun load(
        loadCurrent: suspend () -> Result<CurrentWeather>,
        loadForecast: suspend () -> Result<Forecast>,
    ) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            updateState {
                copy(
                    current = SectionState.Loading,
                    forecast = SectionState.Loading,
                )
            }
            supervisorScope {
                launch {
                    val result = loadCurrent()
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
                    val section = loadForecast().toSection { it.toUiModel() }
                    updateState { copy(forecast = section) }
                }
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
