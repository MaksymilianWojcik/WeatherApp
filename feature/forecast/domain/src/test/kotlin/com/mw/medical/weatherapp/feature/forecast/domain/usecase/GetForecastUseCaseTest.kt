package com.mw.medical.weatherapp.feature.forecast.domain.usecase

import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.feature.forecast.domain.model.Forecast
import com.mw.medical.weatherapp.feature.forecast.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test

class GetForecastUseCaseTest {

    val weatherRepository: WeatherRepository = mockk()
    val tested = GetForecastUseCase(weatherRepository)

    @Test
    fun `should limit hourly forecast to eight entries`() = runTest {
        val forecast = Forecast(
            hourly = List(9) { mockk() },
            daily = emptyList(),
        )
        coEvery { weatherRepository.getForecast(any()) } returns Result.Success(forecast)

        val result = tested(mockk())

        result.shouldBeInstanceOf<Result.Success<Forecast>>().value.hourly.size shouldBeEqualTo 8
    }

    @Test
    fun `should propagate failure from repository`() = runTest {
        coEvery { weatherRepository.getForecast(any()) } returns Result.Failure(AppError.Generic)

        val result = tested(mockk())

        result shouldBeEqualTo Result.Failure(AppError.Generic)
    }
}
