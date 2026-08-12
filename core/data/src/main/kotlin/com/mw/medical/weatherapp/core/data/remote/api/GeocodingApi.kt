package com.mw.medical.weatherapp.core.data.remote.api

import com.mw.medical.weatherapp.core.data.remote.dto.GeocodingCityDto
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GeocodingApi {
    @GET("geo/1.0/direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int,
    ): List<GeocodingCityDto>
}
