package com.mw.medical.weatherapp.core.data.repository

import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.data.remote.api.WeatherApi
import com.mw.medical.weatherapp.core.data.remote.dto.CurrentWeatherResponse
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class WeatherRepositoryImplTest {

    val api: WeatherApi = mockk()
    val dispatchers: DispatcherProvider = mockk {
        every { io } returns UnconfinedTestDispatcher()
    }
    val tested = WeatherRepositoryImpl(api, dispatchers)

    @Test
    fun `should return mapped weather on success`() = runTest {
        val response: CurrentWeatherResponse = mockk {
            every { main } returns mockk { every { temp } returns 20.0 }
            every { weather } returns listOf(
                mockk {
                    every { description } returns "clear sky"
                    every { icon } returns "01d"
                },
            )
        }
        coEvery { api.getCurrentWeather(any(), any()) } returns response

        val result = tested.getCurrentWeather(
            Coordinates(
                latitude = 0.0,
                longitude = 0.0,
            ),
        )

        result shouldBeEqualTo Result.Success(
            CurrentWeather(
                temperature = 20.0,
                condition = WeatherCondition(
                    description = "clear sky",
                    iconCode = "01d",
                ),
            ),
        )
    }

    @Test
    fun `should return failure when api throws`() = runTest {
        coEvery { api.getCurrentWeather(any(), any()) } throws IOException()

        val result = tested.getCurrentWeather(
            Coordinates(
                latitude = 0.0,
                longitude = 0.0,
            ),
        )

        result shouldBeEqualTo Result.Failure(AppError.Generic)
    }
}
