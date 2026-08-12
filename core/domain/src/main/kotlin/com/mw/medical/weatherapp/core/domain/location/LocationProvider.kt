package com.mw.medical.weatherapp.core.domain.location

import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates

interface LocationProvider {
    suspend fun currentCoordinates(): Result<Coordinates>
}
