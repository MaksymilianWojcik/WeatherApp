package com.mw.medical.weatherapp.core.data.di

import com.mw.medical.weatherapp.core.data.repository.GeocodingRepositoryImpl
import com.mw.medical.weatherapp.core.data.repository.WeatherRepositoryImpl
import com.mw.medical.weatherapp.core.domain.repository.GeocodingRepository
import com.mw.medical.weatherapp.core.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindGeocodingRepository(impl: GeocodingRepositoryImpl): GeocodingRepository
}
