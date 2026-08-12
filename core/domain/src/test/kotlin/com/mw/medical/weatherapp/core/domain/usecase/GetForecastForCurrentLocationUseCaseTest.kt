package com.mw.medical.weatherapp.core.domain.usecase

import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.model.Forecast
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class GetForecastForCurrentLocationUseCaseTest {

    val getDeviceLocation: GetDeviceLocationUseCase = mockk()
    val getForecast: GetForecastUseCase = mockk()
    val tested = GetForecastForCurrentLocationUseCase(getDeviceLocation, getForecast)

    @Test
    fun `should return forecast for the device location`() = runTest {
        val coordinates: Coordinates = mockk()
        val forecast: Forecast = mockk()
        coEvery { getDeviceLocation() } returns Result.Success(coordinates)
        coEvery { getForecast(coordinates) } returns Result.Success(forecast)

        val result = tested()

        result shouldBeEqualTo Result.Success(forecast)
    }

    @Test
    fun `should propagate failure when location is unavailable`() = runTest {
        coEvery { getDeviceLocation() } returns Result.Failure(AppError.Generic)

        val result = tested()

        result shouldBeEqualTo Result.Failure(AppError.Generic)
    }
}
