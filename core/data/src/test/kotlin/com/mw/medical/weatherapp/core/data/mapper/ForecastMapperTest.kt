package com.mw.medical.weatherapp.core.data.mapper

import com.mw.medical.weatherapp.core.data.remote.dto.ForecastEntryDto
import com.mw.medical.weatherapp.core.data.remote.dto.ForecastResponse
import com.mw.medical.weatherapp.core.domain.model.DailyForecast
import com.mw.medical.weatherapp.core.domain.model.HourlyForecast
import com.mw.medical.weatherapp.core.domain.model.WeatherCondition
import com.mw.medical.weatherapp.core.domain.model.WeatherKind
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.assertSoftly
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class ForecastMapperTest {

    val tested = ForecastResponse::toDomain

    @Test
    fun `should map entries to hourly points`() {
        val response = response(
            entries = listOf(
                entry(
                    epochUtc = LocalDateTime.of(2022, 1, 1, 12, 0),
                    temperature = 10.0,
                    conditionDescription = "clear sky",
                    conditionIcon = "01d",
                ),
            ),
        )

        val result = tested(response)

        result.hourly shouldBeEqualTo listOf(
            HourlyForecast(
                time = LocalDateTime.of(2022, 1, 1, 12, 0),
                temperature = 10.0,
                condition = WeatherCondition(
                    description = "clear sky",
                    kind = WeatherKind.Clear,
                    isNight = false,
                ),
            ),
        )
    }

    @Test
    fun `should aggregate a day into min max and the midday condition`() {
        val response = response(
            entries = listOf(
                entry(
                    epochUtc = LocalDateTime.of(2022, 1, 1, 9, 0),
                    minTemperature = 5.0,
                    maxTemperature = 9.0,
                    conditionDescription = "morning",
                    conditionIcon = "01d",
                ),
                entry(
                    epochUtc = LocalDateTime.of(2022, 1, 1, 12, 0),
                    minTemperature = 8.0,
                    maxTemperature = 14.0,
                    conditionDescription = "noon",
                    conditionIcon = "02d",
                ),
                entry(
                    epochUtc = LocalDateTime.of(2022, 1, 1, 15, 0),
                    minTemperature = 7.0,
                    maxTemperature = 12.0,
                    conditionDescription = "afternoon",
                    conditionIcon = "03d",
                ),
            ),
        )

        val result = tested(response)

        result.daily shouldBeEqualTo listOf(
            DailyForecast(
                date = LocalDate.of(2022, 1, 1),
                minTemperature = 5.0,
                maxTemperature = 14.0,
                condition = WeatherCondition(
                    description = "noon",
                    kind = WeatherKind.FewClouds,
                    isNight = false,
                ),
            ),
        )
    }

    @Test
    fun `should resolve local time using the city timezone offset`() {
        val response = response(
            entries = listOf(entry(epochUtc = LocalDateTime.of(2022, 1, 1, 23, 30))),
            offsetSeconds = 3600,
        )

        val result = tested(response)

        assertSoftly(result) {
            hourly.single().time shouldBeEqualTo LocalDateTime.of(2022, 1, 2, 0, 30)
            daily.single().date shouldBeEqualTo LocalDate.of(2022, 1, 2)
        }
    }

    fun response(
        entries: List<ForecastEntryDto>,
        offsetSeconds: Int = 0,
    ): ForecastResponse = mockk {
        every { list } returns entries
        every { city } returns mockk { every { timezone } returns offsetSeconds }
    }

    fun entry(
        epochUtc: LocalDateTime,
        temperature: Double = 0.0,
        minTemperature: Double = 0.0,
        maxTemperature: Double = 0.0,
        conditionDescription: String = "",
        conditionIcon: String = "",
    ): ForecastEntryDto = mockk {
        every { dt } returns epochUtc.toEpochSecond(ZoneOffset.UTC)
        every { main } returns mockk {
            every { temp } returns temperature
            every { tempMin } returns minTemperature
            every { tempMax } returns maxTemperature
        }
        every { weather } returns listOf(
            mockk {
                every { description } returns conditionDescription
                every { icon } returns conditionIcon
            },
        )
    }
}
