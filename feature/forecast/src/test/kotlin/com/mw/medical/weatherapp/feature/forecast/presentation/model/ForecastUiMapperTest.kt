package com.mw.medical.weatherapp.feature.forecast.presentation.model

import com.mw.medical.weatherapp.core.designsystem.icon.WeatherIcon
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.DailyForecast
import com.mw.medical.weatherapp.core.domain.model.HourlyForecast
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import com.mw.medical.weatherapp.core.domain.model.WeatherKind
import com.mw.medical.weatherapp.core.testing.DefaultLocaleExtension
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldStartWith
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale

internal class ForecastUiMapperTest {

    @RegisterExtension
    val defaultLocale = DefaultLocaleExtension()

    @TestFactory
    fun `should map weather kinds to icons`(): List<DynamicTest> {
        return listOf(
            WeatherKind.Clear to WeatherIcon.ClearDay,
            WeatherKind.FewClouds to WeatherIcon.FewClouds,
            WeatherKind.ScatteredClouds to WeatherIcon.ScatteredClouds,
            WeatherKind.BrokenClouds to WeatherIcon.BrokenClouds,
            WeatherKind.ShowerRain to WeatherIcon.ShowerRain,
            WeatherKind.Rain to WeatherIcon.Rain,
            WeatherKind.Thunderstorm to WeatherIcon.Thunderstorm,
            WeatherKind.Snow to WeatherIcon.Snow,
            WeatherKind.Mist to WeatherIcon.Mist,
            WeatherKind.Unknown to WeatherIcon.Unknown,
        ).map { (kind, icon) ->
            DynamicTest.dynamicTest("$kind maps to $icon") {
                WeatherCondition(
                    description = "description",
                    kind = kind,
                    isNight = false,
                ).toIcon() shouldBeEqualTo icon
            }
        }
    }

    @Test
    fun `should map clear night to the night icon`() {
        val result = WeatherCondition(
            description = "description",
            kind = WeatherKind.Clear,
            isNight = true,
        ).toIcon()

        result shouldBeEqualTo WeatherIcon.ClearNight
    }

    @Test
    fun `should format the hour for a twenty four hour locale`() {
        val result = HourlyForecast(
            time = LocalDateTime.of(2022, 1, 1, 18, 30),
            temperature = 0.0,
            condition = WeatherCondition.EMPTY,
        ).toUiModel()

        result.time shouldBeEqualTo "18:30"
    }

    @Test
    fun `should format the hour for a twelve hour locale`() {
        Locale.setDefault(Locale.US)

        val result = HourlyForecast(
            time = LocalDateTime.of(2022, 1, 1, 18, 30),
            temperature = 0.0,
            condition = WeatherCondition.EMPTY,
        ).toUiModel()

        result.time shouldStartWith "6:30"
    }

    @Test
    fun `should format the day as a short weekday`() {
        val result = DailyForecast(
            date = LocalDate.of(2022, 1, 3),
            minTemperature = 0.0,
            maxTemperature = 0.0,
            condition = WeatherCondition.EMPTY,
        ).toUiModel()

        result.day shouldBeEqualTo "Mon"
    }

    @Test
    fun `should round the temperature label`() {
        val result = CurrentWeather(
            temperature = 20.6,
            condition = WeatherCondition.EMPTY,
            cityName = "name",
            country = "PL",
        ).toUiModel()

        result.temperature shouldBeEqualTo "21°"
    }
}
