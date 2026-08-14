package com.mw.medical.weatherapp.core.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks
import com.mw.medical.weatherapp.core.common.dispatcher.DispatcherProvider
import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.logging.Logger
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class LocationProviderImplTest {

    val client: FusedLocationProviderClient = mockk()
    val dispatchers: DispatcherProvider = mockk {
        every { io } returns UnconfinedTestDispatcher()
    }
    val logger: Logger = mockk(relaxed = true)
    val tested = LocationProviderImpl(client, dispatchers, logger)

    @Test
    fun `should return coordinates on success`() = runTest {
        val location: Location = mockk {
            every { latitude } returns 51.5
            every { longitude } returns -0.12
        }
        every { client.getCurrentLocation(any<Int>(), any()) } returns Tasks.forResult(location)

        val result = tested.currentCoordinates()

        result shouldBeEqualTo Result.Success(
            Coordinates(
                latitude = 51.5,
                longitude = -0.12,
            ),
        )
    }

    @Test
    fun `should return failure when location lookup throws`() = runTest {
        every { client.getCurrentLocation(any<Int>(), any()) } throws SecurityException()

        val result = tested.currentCoordinates()

        result shouldBeEqualTo Result.Failure(AppError.Generic)
    }
}
