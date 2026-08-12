package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.lifecycle.viewModelScope
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.usecase.GetCurrentWeatherForCurrentLocationUseCase
import com.mw.medical.weatherapp.core.mvi.MviViewModel
import com.mw.medical.weatherapp.core.mvi.SideEffect
import com.mw.medical.weatherapp.feature.forecast.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ForecastViewModel @Inject constructor(
    private val getCurrentWeatherForCurrentLocation: GetCurrentWeatherForCurrentLocationUseCase,
) : MviViewModel<ForecastContract.State, ForecastContract.Action, SideEffect>(
    ForecastContract.State.Initial,
) {
    override fun onAction(action: ForecastContract.Action) {
        when (action) {
            ForecastContract.Action.Load, ForecastContract.Action.Retry -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, hasError = false) }
            when (val result = getCurrentWeatherForCurrentLocation()) {
                is Result.Success -> updateState {
                    copy(isLoading = false, hasError = false, forecast = result.value.toUiModel())
                }
                is Result.Failure -> updateState { copy(isLoading = false, hasError = true) }
            }
        }
    }
}
