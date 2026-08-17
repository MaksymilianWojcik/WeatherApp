package com.mw.medical.weatherapp.feature.search.domain.usecase

import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.feature.search.domain.model.City
import com.mw.medical.weatherapp.feature.search.domain.repository.GeocodingRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class SearchCitiesUseCaseTest {

    val geocodingRepository: GeocodingRepository = mockk()
    val tested = SearchCitiesUseCase(geocodingRepository)

    @Test
    fun `should return not found for a blank query`() = runTest {
        val result = tested("   ")

        result shouldBeEqualTo Result.Failure(AppError.NotFound)
    }

    @Test
    fun `should keep cities that differ by state or country`() = runTest {
        val cities = listOf(
            City(
                name = "London",
                state = "England",
                country = "GB",
                coordinates = Coordinates(
                    latitude = 51.5,
                    longitude = -0.12,
                ),
            ),
            City(
                name = "London",
                state = "Ontario",
                country = "CA",
                coordinates = Coordinates(
                    latitude = 42.9,
                    longitude = -81.2,
                ),
            ),
        )
        coEvery { geocodingRepository.searchCities("london") } returns Result.Success(cities)

        val result = tested("london")

        result shouldBeEqualTo Result.Success(cities)
    }

    @Test
    fun `should drop cities that share name, state and country`() = runTest {
        coEvery { geocodingRepository.searchCities("szczecin") } returns Result.Success(
            listOf(
                City(
                    name = "Szczecin",
                    state = "West Pomeranian Voivodeship",
                    country = "PL",
                    coordinates = Coordinates(
                        latitude = 53.4,
                        longitude = 14.5,
                    ),
                ),
                City(
                    name = "Szczecin",
                    state = "West Pomeranian Voivodeship",
                    country = "PL",
                    coordinates = Coordinates(
                        latitude = 53.3,
                        longitude = 14.6,
                    ),
                ),
            ),
        )

        val result = tested("szczecin")

        result shouldBeEqualTo Result.Success(
            listOf(
                City(
                    name = "Szczecin",
                    state = "West Pomeranian Voivodeship",
                    country = "PL",
                    coordinates = Coordinates(
                        latitude = 53.4,
                        longitude = 14.5,
                    ),
                ),
            ),
        )
    }

    @Test
    fun `should return failure from repository`() = runTest {
        coEvery { geocodingRepository.searchCities("london") } returns Result.Failure(AppError.Generic)

        val result = tested("london")

        result shouldBeEqualTo Result.Failure(AppError.Generic)
    }
}
