package com.mw.medical.weatherapp.feature.forecast.presentation.model

import com.mw.medical.weatherapp.core.designsystem.icon.WeatherIcon
import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import com.mw.medical.weatherapp.core.domain.model.WeatherKind
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class ForecastUiMapperTest {

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
