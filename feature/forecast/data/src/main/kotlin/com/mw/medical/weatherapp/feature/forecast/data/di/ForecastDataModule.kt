package com.mw.medical.weatherapp.feature.forecast.data.di

import com.mw.medical.weatherapp.feature.forecast.data.remote.api.WeatherApi
import com.mw.medical.weatherapp.feature.forecast.data.repository.WeatherRepositoryImpl
import com.mw.medical.weatherapp.feature.forecast.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ForecastDataModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    companion object {

        @Provides
        @Singleton
        fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
            return retrofit.create(WeatherApi::class.java)
        }
    }
}
