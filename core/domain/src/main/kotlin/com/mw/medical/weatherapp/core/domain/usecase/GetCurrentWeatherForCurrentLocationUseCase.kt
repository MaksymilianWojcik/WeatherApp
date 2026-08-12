package com.mw.medical.weatherapp.core.domain.usecase

import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import javax.inject.Inject

class GetCurrentWeatherForCurrentLocationUseCase @Inject constructor(
    private val getDeviceLocation: GetDeviceLocationUseCase,
    private val getCurrentWeather: GetCurrentWeatherUseCase,
) {
    suspend operator fun invoke(): Result<CurrentWeather> {
        return when (val location = getDeviceLocation()) {
            is Result.Success -> getCurrentWeather(location.value)
            is Result.Failure -> location
        }
    }
}
