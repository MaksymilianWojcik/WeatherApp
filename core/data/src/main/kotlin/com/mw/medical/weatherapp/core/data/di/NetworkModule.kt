package com.mw.medical.weatherapp.core.data.di

import com.mw.medical.weatherapp.core.data.BuildConfig
import com.mw.medical.weatherapp.core.data.remote.ApiKeyInterceptor
import com.mw.medical.weatherapp.core.data.remote.api.GeocodingApi
import com.mw.medical.weatherapp.core.data.remote.api.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val BASE_URL = "https://api.openweathermap.org/"

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json { ignoreUnknownKeys = true }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(BuildConfig.OPEN_WEATHER_API_KEY))
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingApi(retrofit: Retrofit): GeocodingApi {
        return retrofit.create(GeocodingApi::class.java)
    }
}
