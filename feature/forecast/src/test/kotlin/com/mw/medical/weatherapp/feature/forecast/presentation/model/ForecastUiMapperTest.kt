package com.mw.medical.weatherapp.feature.forecast.presentation.model

import com.mw.medical.weatherapp.core.domain.model.CurrentWeather
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import com.mw.medical.weatherapp.core.domain.model.WeatherKind
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class ForecastUiMapperTest {

    @TestFactory
    fun `should map weather kinds to emoji`(): List<DynamicTest> {
        return listOf(
            WeatherKind.Clear to "☀️",
            WeatherKind.FewClouds to "🌤️",
            WeatherKind.ScatteredClouds to "⛅",
            WeatherKind.BrokenClouds to "☁️",
            WeatherKind.ShowerRain to "🌧️",
            WeatherKind.Rain to "🌦️",
            WeatherKind.Thunderstorm to "⛈️",
            WeatherKind.Snow to "❄️",
            WeatherKind.Mist to "🌫️",
            WeatherKind.Unknown to "🌡️",
        ).map { (kind, emoji) ->
            DynamicTest.dynamicTest("$kind maps to $emoji") {
                WeatherCondition(
                    description = "description",
                    kind = kind,
                    isNight = false,
                ).toEmoji() shouldBeEqualTo emoji
            }
        }
    }

    @Test
    fun `should map clear night to the moon emoji`() {
        val result = WeatherCondition(
            description = "description",
            kind = WeatherKind.Clear,
            isNight = true,
        ).toEmoji()

        result shouldBeEqualTo "🌙"
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
