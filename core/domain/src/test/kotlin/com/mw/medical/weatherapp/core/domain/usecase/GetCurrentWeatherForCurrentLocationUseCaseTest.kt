package com.mw.medical.weatherapp.core.domain.usecase

import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class GetCurrentWeatherForCurrentLocationUseCaseTest {

    val getDeviceLocation: GetDeviceLocationUseCase = mockk()
    val getCurrentWeather: GetCurrentWeatherUseCase = mockk()
    val tested = GetCurrentWeatherForCurrentLocationUseCase(getDeviceLocation, getCurrentWeather)

    @Test
    fun `should return weather for the device location`() = runTest {
        val coordinates: Coordinates = mockk()
        val weather: CurrentWeather = mockk()
        coEvery { getDeviceLocation() } returns Result.Success(coordinates)
        coEvery { getCurrentWeather(coordinates) } returns Result.Success(weather)

        val result = tested()

        result shouldBeEqualTo Result.Success(weather)
    }

    @Test
    fun `should propagate failure when location is unavailable`() = runTest {
        coEvery { getDeviceLocation() } returns Result.Failure(AppError.Generic)

        val result = tested()

        result shouldBeEqualTo Result.Failure(AppError.Generic)
    }
}
