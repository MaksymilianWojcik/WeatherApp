package com.mw.medical.weatherapp.core.data.mapper

import com.mw.medical.weatherapp.core.data.remote.dto.CurrentWeatherResponse
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.assertSoftly
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

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
            condition.iconCode shouldBeEqualTo "01d"
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
}
