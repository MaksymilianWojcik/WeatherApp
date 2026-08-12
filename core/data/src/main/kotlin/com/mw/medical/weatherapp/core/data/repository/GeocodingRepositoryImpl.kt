package com.mw.medical.weatherapp.core.data.repository

import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.data.mapper.toDomain
import com.mw.medical.weatherapp.core.data.remote.api.GeocodingApi
import com.mw.medical.weatherapp.core.data.remote.apiCall
import com.mw.medical.weatherapp.core.domain.model.City
import com.mw.medical.weatherapp.core.domain.repository.GeocodingRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val SEARCH_RESULT_LIMIT = 5

internal class GeocodingRepositoryImpl @Inject constructor(
    private val api: GeocodingApi,
    private val dispatchers: DispatcherProvider,
) : GeocodingRepository {
    override suspend fun searchCities(query: String): Result<List<City>> {
        return withContext(dispatchers.io) {
            apiCall {
                api.searchCities(query, SEARCH_RESULT_LIMIT).map { it.toDomain() }
            }.notFoundIfEmpty()
        }
    }

    private fun Result<List<City>>.notFoundIfEmpty(): Result<List<City>> {
        return when (this) {
            is Result.Success -> if (value.isEmpty()) {
                Result.Failure(AppError.NotFound)
            } else {
                this
            }
            is Result.Failure -> this
        }
    }
}
