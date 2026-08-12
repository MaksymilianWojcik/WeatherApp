package com.mw.medical.weatherapp.core.data.di

import com.mw.medical.weatherapp.core.common.dispatcher.DefaultDispatcherProvider
import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import com.mw.medical.weatherapp.core.data.repository.WeatherRepositoryImpl
import com.mw.medical.weatherapp.core.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    companion object {
        @Provides
        @Singleton
        fun provideDispatcherProvider(): DispatcherProvider {
            return DefaultDispatcherProvider()
        }
    }
}
