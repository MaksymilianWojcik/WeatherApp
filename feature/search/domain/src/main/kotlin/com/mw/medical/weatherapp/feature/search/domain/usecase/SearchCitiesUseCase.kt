package com.mw.medical.weatherapp.feature.search.domain.usecase

import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.feature.search.domain.model.City
import com.mw.medical.weatherapp.feature.search.domain.repository.GeocodingRepository
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository,
) {
    suspend operator fun invoke(query: String): Result<List<City>> {
        if (query.isBlank()) {
            return Result.Failure(AppError.NotFound)
        }
        return when (val result = geocodingRepository.searchCities(query.trim())) {
            is Result.Success -> Result.Success(result.value.distinctByIdentity())
            is Result.Failure -> result
        }
    }

    private fun List<City>.distinctByIdentity() =
        distinctBy { Triple(it.name, it.state, it.country) }
}
