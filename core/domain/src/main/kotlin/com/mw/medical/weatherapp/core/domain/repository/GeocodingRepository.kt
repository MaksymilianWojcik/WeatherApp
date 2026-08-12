package com.mw.medical.weatherapp.core.domain.repository

import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.City

interface GeocodingRepository {
    suspend fun searchCities(query: String): Result<List<City>>
}
