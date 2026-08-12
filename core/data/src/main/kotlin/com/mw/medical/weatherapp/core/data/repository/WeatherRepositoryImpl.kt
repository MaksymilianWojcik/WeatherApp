package com.mw.medical.weatherapp.core.data.repository

import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.data.mapper.toDomain
import com.mw.medical.weatherapp.core.data.remote.api.WeatherApi
import com.mw.medical.weatherapp.core.data.remote.apiCall
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.repository.WeatherRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dispatchers: DispatcherProvider,
) : WeatherRepository {
    override suspend fun getCurrentWeather(coordinates: Coordinates): Result<CurrentWeather> {
        return withContext(dispatchers.io) {
            apiCall { api.getCurrentWeather(coordinates.latitude, coordinates.longitude).toDomain() }
        }
    }
}
