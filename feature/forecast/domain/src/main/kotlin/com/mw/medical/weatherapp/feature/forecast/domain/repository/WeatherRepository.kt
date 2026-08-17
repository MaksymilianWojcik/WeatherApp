package com.mw.medical.weatherapp.feature.forecast.domain.repository

import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.feature.forecast.domain.model.CurrentWeather
import com.mw.medical.weatherapp.feature.forecast.domain.model.Forecast

interface WeatherRepository {
    suspend fun getCurrentWeather(coordinates: Coordinates): Result<CurrentWeather>
    suspend fun getForecast(coordinates: Coordinates): Result<Forecast>
}
