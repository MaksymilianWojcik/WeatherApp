package com.mw.medical.weatherapp.core.domain.repository

import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather

interface WeatherRepository {
    suspend fun getCurrentWeather(coordinates: Coordinates): Result<CurrentWeather>
}
