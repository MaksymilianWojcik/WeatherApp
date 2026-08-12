package com.mw.medical.weatherapp.core.domain.usecase

import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Forecast
import javax.inject.Inject

class GetForecastForCurrentLocationUseCase @Inject constructor(
    private val getDeviceLocation: GetDeviceLocationUseCase,
    private val getForecast: GetForecastUseCase,
) {
    suspend operator fun invoke(): Result<Forecast> {
        return when (val location = getDeviceLocation()) {
            is Result.Success -> getForecast(location.value)
            is Result.Failure -> location
        }
    }
}
