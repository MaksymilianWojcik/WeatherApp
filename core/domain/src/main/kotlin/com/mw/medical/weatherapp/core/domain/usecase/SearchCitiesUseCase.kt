package com.mw.medical.weatherapp.core.domain.usecase

import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.City
import com.mw.medical.weatherapp.core.domain.repository.GeocodingRepository
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository,
) {
    suspend operator fun invoke(query: String): Result<List<City>> {
        if (query.isBlank()) {
            return Result.Failure(AppError.NotFound)
        }
        return geocodingRepository.searchCities(query.trim())
    }
}
