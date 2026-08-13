package com.mw.medical.weatherapp.core.data.remote

import okhttp3.Interceptor
import okhttp3.Response

internal class ApiKeyInterceptor(
    private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("appid", apiKey)
            .addQueryParameter("units", "metric")
            .build()
        return chain.proceed(original.newBuilder().url(url).build())
    }
}
