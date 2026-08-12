package com.mw.medical.weatherapp.core.domain.usecase

import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.model.Forecast
import com.mw.medical.weatherapp.core.domain.repository.WeatherRepository
import javax.inject.Inject

private const val HOURLY_FORECAST_COUNT = 8

class GetForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {
    suspend operator fun invoke(coordinates: Coordinates): Result<Forecast> {
        return when (val result = weatherRepository.getForecast(coordinates)) {
            is Result.Success -> Result.Success(result.value.withHourlyLimit())
            is Result.Failure -> result
        }
    }

    private fun Forecast.withHourlyLimit() = copy(hourly = hourly.take(HOURLY_FORECAST_COUNT))
}
