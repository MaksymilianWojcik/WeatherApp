package com.mw.medical.weatherapp.core.data.mapper

import com.mw.medical.weatherapp.core.data.remote.dto.CurrentWeatherResponse
import com.mw.medical.weatherapp.core.data.remote.dto.WeatherDto
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import com.mw.medical.weatherapp.core.domain.model.WeatherKind
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.assertSoftly
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class WeatherMapperTest {

    val tested = CurrentWeatherResponse::toDomain

    @Test
    fun `should map response to current weather`() {
        val response: CurrentWeatherResponse = mockk {
            every { name } returns "Szczecin"
            every { sys } returns mockk { every { country } returns "PL" }
            every { main } returns mockk { every { temp } returns 20.0 }
            every { weather } returns listOf(
                mockk {
                    every { description } returns "clear sky"
                    every { icon } returns "01d"
                },
            )
        }

        val result = tested(response)

        assertSoftly(result) {
            temperature shouldBeEqualTo 20.0
            condition.description shouldBeEqualTo "clear sky"
            condition.kind shouldBeEqualTo WeatherKind.Clear
            condition.isNight shouldBe false
            cityName shouldBeEqualTo "Szczecin"
            country shouldBeEqualTo "PL"
        }
    }

    @Test
    fun `should map missing condition to empty`() {
        val response: CurrentWeatherResponse = mockk {
            every { name } returns "Szczecin"
            every { sys } returns mockk { every { country } returns "PL" }
            every { main } returns mockk { every { temp } returns 20.0 }
            every { weather } returns emptyList()
        }

        val result = tested(response)

        result.condition shouldBeEqualTo WeatherCondition.EMPTY
    }

    @Test
    fun `should map a night icon code`() {
        val result = listOf(weatherDto(icon = "01n")).toCondition()

        result.isNight shouldBe true
    }

    @TestFactory
    fun `should map icon codes to weather kinds`(): List<DynamicTest> {
        return listOf(
            "01d" to WeatherKind.Clear,
            "02d" to WeatherKind.FewClouds,
            "03d" to WeatherKind.ScatteredClouds,
            "04d" to WeatherKind.BrokenClouds,
            "09d" to WeatherKind.ShowerRain,
            "10d" to WeatherKind.Rain,
            "11d" to WeatherKind.Thunderstorm,
            "13d" to WeatherKind.Snow,
            "50d" to WeatherKind.Mist,
            "99x" to WeatherKind.Unknown,
        ).map { (icon, kind) ->
            DynamicTest.dynamicTest("$icon maps to $kind") {
                listOf(weatherDto(icon = icon)).toCondition().kind shouldBeEqualTo kind
            }
        }
    }

    fun weatherDto(icon: String): WeatherDto = mockk {
        every { description } returns "description"
        every { this@mockk.icon } returns icon
    }
}
