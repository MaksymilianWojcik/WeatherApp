package com.mw.medical.weatherapp.feature.search.data.di

import com.mw.medical.weatherapp.feature.search.data.remote.api.GeocodingApi
import com.mw.medical.weatherapp.feature.search.data.repository.GeocodingRepositoryImpl
import com.mw.medical.weatherapp.feature.search.domain.repository.GeocodingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SearchDataModule {

    @Binds
    @Singleton
    abstract fun bindGeocodingRepository(impl: GeocodingRepositoryImpl): GeocodingRepository

    companion object {

        @Provides
        @Singleton
        fun provideGeocodingApi(retrofit: Retrofit): GeocodingApi {
            return retrofit.create(GeocodingApi::class.java)
        }
    }
}
