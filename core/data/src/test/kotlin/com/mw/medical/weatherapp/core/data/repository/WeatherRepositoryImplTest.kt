package com.mw.medical.weatherapp.core.data.repository

import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.data.remote.api.WeatherApi
import com.mw.medical.weatherapp.core.data.remote.dto.CurrentWeatherResponse
import com.mw.medical.weatherapp.core.data.remote.dto.ForecastResponse
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.DailyForecast
import com.mw.medical.weatherapp.core.domain.model.Forecast
import com.mw.medical.weatherapp.core.domain.model.HourlyForecast
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

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
            every { name } returns "Szczecin"
            every { sys } returns mockk { every { country } returns "PL" }
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
                cityName = "Szczecin",
                country = "PL",
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

    @Test
    fun `should return mapped forecast on success`() = runTest {
        val response: ForecastResponse = mockk {
            every { list } returns listOf(
                mockk {
                    every { dt } returns LocalDateTime.of(2022, 1, 1, 12, 0).toEpochSecond(ZoneOffset.UTC)
                    every { main } returns mockk {
                        every { temp } returns 10.0
                        every { tempMin } returns 8.0
                        every { tempMax } returns 12.0
                    }
                    every { weather } returns listOf(
                        mockk {
                            every { description } returns "clear sky"
                            every { icon } returns "01d"
                        },
                    )
                },
            )
            every { city } returns mockk { every { timezone } returns 0 }
        }
        coEvery { api.getForecast(any(), any()) } returns response

        val result = tested.getForecast(
            Coordinates(
                latitude = 0.0,
                longitude = 0.0,
            ),
        )

        result shouldBeEqualTo Result.Success(
            Forecast(
                hourly = listOf(
                    HourlyForecast(
                        time = LocalDateTime.of(2022, 1, 1, 12, 0),
                        temperature = 10.0,
                        condition = WeatherCondition(
                            description = "clear sky",
                            iconCode = "01d",
                        ),
                    ),
                ),
                daily = listOf(
                    DailyForecast(
                        date = LocalDate.of(2022, 1, 1),
                        minTemperature = 8.0,
                        maxTemperature = 12.0,
                        condition = WeatherCondition(
                            description = "clear sky",
                            iconCode = "01d",
                        ),
                    ),
                ),
            ),
        )
    }

    @Test
    fun `should return failure when forecast api throws`() = runTest {
        coEvery { api.getForecast(any(), any()) } throws IOException()

        val result = tested.getForecast(
            Coordinates(
                latitude = 0.0,
                longitude = 0.0,
            ),
        )

        result shouldBeEqualTo Result.Failure(AppError.Generic)
    }
}
