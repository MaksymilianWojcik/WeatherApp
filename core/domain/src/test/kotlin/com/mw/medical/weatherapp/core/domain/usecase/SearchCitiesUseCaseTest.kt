package com.mw.medical.weatherapp.core.domain.usecase

import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.City
import com.mw.medical.weatherapp.core.domain.repository.GeocodingRepository
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
    fun `should return cities from repository for a query`() = runTest {
        val cities: List<City> = listOf(mockk())
        coEvery { geocodingRepository.searchCities("london") } returns Result.Success(cities)

        val result = tested("london")

        result shouldBeEqualTo Result.Success(cities)
    }
}
