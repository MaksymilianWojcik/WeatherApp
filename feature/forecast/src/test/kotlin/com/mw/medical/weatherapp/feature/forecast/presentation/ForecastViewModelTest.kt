package com.mw.medical.weatherapp.feature.forecast.presentation

import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.DailyForecast
import com.mw.medical.weatherapp.core.domain.model.Forecast
import com.mw.medical.weatherapp.core.domain.model.HourlyForecast
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import com.mw.medical.weatherapp.core.domain.usecase.GetCurrentWeatherForCurrentLocationUseCase
import com.mw.medical.weatherapp.core.domain.usecase.GetCurrentWeatherUseCase
import com.mw.medical.weatherapp.core.domain.usecase.GetForecastForCurrentLocationUseCase
import com.mw.medical.weatherapp.core.domain.usecase.GetForecastUseCase
import com.mw.medical.weatherapp.feature.forecast.presentation.ForecastContract.SectionState
import com.mw.medical.weatherapp.feature.forecast.presentation.model.CurrentWeatherUiModel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.ForecastUiModel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.HourlyForecastUiModel
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
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
internal class ForecastViewModelTest {

    val getCurrentWeatherForCurrentLocation: GetCurrentWeatherForCurrentLocationUseCase = mockk()
    val getForecastForCurrentLocation: GetForecastForCurrentLocationUseCase = mockk()
    val getCurrentWeather: GetCurrentWeatherUseCase = mockk()
    val getForecast: GetForecastUseCase = mockk()
    val tested = ForecastViewModel(
        getCurrentWeatherForCurrentLocation,
        getForecastForCurrentLocation,
        getCurrentWeather,
        getForecast,
    )

    @BeforeEach
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `should load current and forecast when permission granted`() = runTest {
        val currentWeather = CurrentWeather(
            temperature = 20.4,
            condition = WeatherCondition(
                description = "clear sky",
                iconCode = "01d",
            ),
            cityName = "Szczecin",
            country = "PL",
        )
        val forecast = Forecast(
            hourly = listOf(
                HourlyForecast(
                    time = LocalDateTime.of(2022, 1, 1, 12, 0),
                    temperature = 20.0,
                    condition = WeatherCondition(
                        description = "clear sky",
                        iconCode = "01d",
                    ),
                ),
            ),
            daily = listOf(
                DailyForecast(
                    date = LocalDate.of(2022, 1, 1),
                    minTemperature = 12.0,
                    maxTemperature = 21.0,
                    condition = WeatherCondition(
                        description = "clear sky",
                        iconCode = "01d",
                    ),
                ),
            ),
        )
        coEvery { getCurrentWeatherForCurrentLocation() } returns Result.Success(currentWeather)
        coEvery { getForecastForCurrentLocation() } returns Result.Success(forecast)

        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Granted))

        tested.state.value.current shouldBeEqualTo SectionState.Content(
            CurrentWeatherUiModel(
                temperature = "20°",
                description = "clear sky",
                icon = "☀️",
            ),
        )
        val forecastContent = tested.state.value.forecast
            .shouldBeInstanceOf<SectionState.Content<ForecastUiModel>>()
        forecastContent.value.hourly shouldBeEqualTo listOf(
            HourlyForecastUiModel(
                time = "12:00",
                temperature = "20°",
                icon = "☀️",
                description = "clear sky",
            ),
        )
        assertSoftly(forecastContent.value.daily.single()) {
            minTemperature shouldBeEqualTo "12°"
            maxTemperature shouldBeEqualTo "21°"
        }
        tested.state.value.locationName shouldBeEqualTo "Szczecin, ${Locale("", "PL").displayCountry}"
    }

    @Test
    fun `should load the selected city and label the location`() = runTest {
        val currentWeather = CurrentWeather(
            temperature = 15.0,
            condition = WeatherCondition(
                description = "broken clouds",
                iconCode = "04d",
            ),
            cityName = "London",
            country = "GB",
        )
        coEvery { getCurrentWeather(any()) } returns Result.Success(currentWeather)
        coEvery { getForecast(any()) } returns Result.Success(mockk(relaxed = true))

        tested.onAction(
            ForecastContract.Action.LocationSelected(
                latitude = 51.5,
                longitude = -0.12,
            ),
        )

        assertSoftly(tested.state.value) {
            locationName shouldBeEqualTo "London, ${Locale("", "GB").displayCountry}"
            current.shouldBeInstanceOf<SectionState.Content<CurrentWeatherUiModel>>()
            forecast.shouldBeInstanceOf<SectionState.Content<ForecastUiModel>>()
        }
        coVerify(exactly = 0) { getCurrentWeatherForCurrentLocation() }
    }

    @Test
    fun `should keep forecast when current weather fails`() = runTest {
        coEvery { getCurrentWeatherForCurrentLocation() } returns Result.Failure(AppError.Generic)
        coEvery { getForecastForCurrentLocation() } returns Result.Success(mockk(relaxed = true))

        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Granted))

        tested.state.value.current shouldBeEqualTo SectionState.Error
        tested.state.value.forecast.shouldBeInstanceOf<SectionState.Content<ForecastUiModel>>()
    }

    @Test
    fun `should keep current weather when forecast fails`() = runTest {
        coEvery { getCurrentWeatherForCurrentLocation() } returns Result.Success(mockk(relaxed = true))
        coEvery { getForecastForCurrentLocation() } returns Result.Failure(AppError.Generic)

        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Granted))

        tested.state.value.forecast shouldBeEqualTo SectionState.Error
        tested.state.value.current.shouldBeInstanceOf<SectionState.Content<CurrentWeatherUiModel>>()
    }

    @Test
    fun `should not reload when permission result repeats`() = runTest {
        coEvery { getCurrentWeatherForCurrentLocation() } returns Result.Success(mockk(relaxed = true))
        coEvery { getForecastForCurrentLocation() } returns Result.Success(mockk(relaxed = true))

        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Granted))
        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Granted))

        coVerify(exactly = 1) { getCurrentWeatherForCurrentLocation() }
        coVerify(exactly = 1) { getForecastForCurrentLocation() }
    }

    @Test
    fun `should not load when permission denied`() = runTest {
        tested.onAction(ForecastContract.Action.OnLocationPermissionResult(LocationPermissionStatus.Denied))

        tested.state.value.locationPermission shouldBeEqualTo LocationPermissionStatus.Denied
        coVerify(exactly = 0) { getCurrentWeatherForCurrentLocation() }
        coVerify(exactly = 0) { getForecastForCurrentLocation() }
    }
}
