package com.mw.medical.weatherapp.core.data.repository

import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.data.remote.api.GeocodingApi
import com.mw.medical.weatherapp.core.data.remote.dto.GeocodingCityDto
import com.mw.medical.weatherapp.core.domain.model.City
import com.mw.medical.weatherapp.core.domain.model.Coordinates
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
internal class GeocodingRepositoryImplTest {

    val api: GeocodingApi = mockk()
    val dispatchers: DispatcherProvider = mockk {
        every { io } returns UnconfinedTestDispatcher()
    }
    val tested = GeocodingRepositoryImpl(api, dispatchers)

    @Test
    fun `should return mapped cities on success`() = runTest {
        val dto: GeocodingCityDto = mockk {
            every { name } returns "London"
            every { lat } returns 51.5
            every { lon } returns -0.12
            every { country } returns "GB"
            every { state } returns "England"
        }
        coEvery { api.searchCities(any(), any()) } returns listOf(dto)

        val result = tested.searchCities("london")

        result shouldBeEqualTo Result.Success(
            listOf(
                City(
                    name = "London",
                    state = "England",
                    country = "GB",
                    coordinates = Coordinates(
                        latitude = 51.5,
                        longitude = -0.12,
                    ),
                ),
            ),
        )
    }

    @Test
    fun `should return not found when no cities match`() = runTest {
        coEvery { api.searchCities(any(), any()) } returns emptyList()

        val result = tested.searchCities("nowhere")

        result shouldBeEqualTo Result.Failure(AppError.NotFound)
    }

    @Test
    fun `should return failure when api throws`() = runTest {
        coEvery { api.searchCities(any(), any()) } throws IOException()

        val result = tested.searchCities("london")

        result shouldBeEqualTo Result.Failure(AppError.Generic)
    }
}
