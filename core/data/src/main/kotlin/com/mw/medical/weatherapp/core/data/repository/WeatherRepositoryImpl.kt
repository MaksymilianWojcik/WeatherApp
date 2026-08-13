package com.mw.medical.weatherapp.core.data.repository

import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.common.result.resultOf
import com.mw.medical.weatherapp.core.data.mapper.toDomain
import com.mw.medical.weatherapp.core.data.remote.api.WeatherApi
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.Forecast
import com.mw.medical.weatherapp.core.domain.repository.WeatherRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dispatchers: DispatcherProvider,
) : WeatherRepository {
    override suspend fun getCurrentWeather(coordinates: Coordinates): Result<CurrentWeather> {
        return withContext(dispatchers.io) {
            resultOf { api.getCurrentWeather(coordinates.latitude, coordinates.longitude).toDomain() }
        }
    }

    override suspend fun getForecast(coordinates: Coordinates): Result<Forecast> {
        return withContext(dispatchers.io) {
            resultOf { api.getForecast(coordinates.latitude, coordinates.longitude).toDomain() }
        }
    }
}
