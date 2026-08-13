package com.mw.medical.weatherapp.core.location.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mw.medical.weatherapp.core.domain.location.LocationProvider
import com.mw.medical.weatherapp.core.location.LocationProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationProvider(impl: LocationProviderImpl): LocationProvider

    companion object {
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }
    }
}
