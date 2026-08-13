package com.mw.medical.weatherapp.di

import com.mw.medical.weatherapp.core.common.dispatcher.DefaultDispatcherProvider
import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }
}
