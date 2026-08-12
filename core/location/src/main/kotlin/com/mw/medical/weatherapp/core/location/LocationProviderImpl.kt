package com.mw.medical.weatherapp.core.location

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.location.LocationProvider
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LocationProviderImpl @Inject constructor(
    private val client: FusedLocationProviderClient,
    private val dispatchers: DispatcherProvider,
) : LocationProvider {

    @SuppressLint("MissingPermission")
    override suspend fun currentCoordinates(): Result<Coordinates> {
        return withContext(dispatchers.io) {
            try {
                val location = client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
                if (location == null) {
                    Result.Failure(AppError.Generic)
                } else {
                    Result.Success(Coordinates(latitude = location.latitude, longitude = location.longitude))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.Failure(AppError.Generic)
            }
        }
    }
}
