package com.mw.medical.weatherapp.core.location

import android.Manifest
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.common.result.resultOf
import com.mw.medical.weatherapp.core.domain.location.LocationProvider
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LocationProviderImpl @Inject constructor(
    private val client: FusedLocationProviderClient,
    private val dispatchers: DispatcherProvider,
) : LocationProvider {

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun currentCoordinates(): Result<Coordinates> {
        return withContext(dispatchers.io) {
            val location = resultOf {
                client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
            }
            when (location) {
                is Result.Success -> location.value.toCoordinates()
                is Result.Failure -> location
            }
        }
    }

    private fun Location?.toCoordinates(): Result<Coordinates> {
        return if (this == null) {
            Result.Failure(AppError.Generic)
        } else {
            Result.Success(
                Coordinates(
                    latitude = latitude,
                    longitude = longitude,
                ),
            )
        }
    }
}
