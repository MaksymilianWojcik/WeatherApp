package com.mw.medical.weatherapp.core.domain.usecase

import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {
    suspend operator fun invoke(coordinates: Coordinates) =
        weatherRepository.getCurrentWeather(coordinates)
}
