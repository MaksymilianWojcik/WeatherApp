package com.mw.medical.weatherapp.feature.forecast.presentation

import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import com.mw.medical.weatherapp.core.domain.usecase.GetCurrentWeatherForCurrentLocationUseCase
import com.mw.medical.weatherapp.feature.forecast.presentation.model.ForecastUiModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.assertSoftly
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ForecastViewModelTest {

    val getCurrentWeatherForCurrentLocation: GetCurrentWeatherForCurrentLocationUseCase = mockk()
    val tested = ForecastViewModel(getCurrentWeatherForCurrentLocation)

    @BeforeEach
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `should load forecast when permission granted`() = runTest {
        val weather = CurrentWeather(
            temperature = 20.4,
            condition = WeatherCondition(
                description = "clear sky",
                iconCode = "01d",
            ),
        )
        coEvery { getCurrentWeatherForCurrentLocation() } returns Result.Success(weather)

        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Granted))

        assertSoftly(tested.state.value) {
            locationPermission shouldBeEqualTo LocationPermissionStatus.Granted
            isLoading shouldBe false
            hasError shouldBe false
            forecast shouldBeEqualTo ForecastUiModel(
                temperature = "20°",
                description = "clear sky",
                iconCode = "01d",
            )
        }
    }

    @Test
    fun `should show error when load fails`() = runTest {
        coEvery { getCurrentWeatherForCurrentLocation() } returns Result.Failure(AppError.Generic)

        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Granted))

        assertSoftly(tested.state.value) {
            hasError shouldBe true
            isLoading shouldBe false
        }
    }

    @Test
    fun `should not reload when permission result repeats`() = runTest {
        coEvery { getCurrentWeatherForCurrentLocation() } returns Result.Success(mockk(relaxed = true))

        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Granted))
        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Granted))

        coVerify(exactly = 1) { getCurrentWeatherForCurrentLocation() }
    }

    @Test
    fun `should not load when permission denied`() = runTest {
        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Denied))

        tested.state.value.locationPermission shouldBeEqualTo LocationPermissionStatus.Denied
        coVerify(exactly = 0) { getCurrentWeatherForCurrentLocation() }
    }
}
