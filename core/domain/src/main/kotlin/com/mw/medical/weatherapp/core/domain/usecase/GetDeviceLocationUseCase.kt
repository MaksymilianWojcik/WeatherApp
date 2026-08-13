package com.mw.medical.weatherapp.core.domain.usecase

import com.mw.medical.weatherapp.core.domain.location.LocationProvider
import javax.inject.Inject

class GetDeviceLocationUseCase @Inject constructor(
    private val locationProvider: LocationProvider,
) {
    suspend operator fun invoke() = locationProvider.currentCoordinates()
}
