package com.mw.medical.weatherapp.core.data.remote.api

import com.mw.medical.weatherapp.core.data.remote.dto.CurrentWeatherResponse
import com.mw.medical.weatherapp.core.data.remote.dto.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): ForecastResponse
}
