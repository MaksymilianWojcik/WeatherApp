package com.mw.medical.weatherapp.feature.search.data.mapper

import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.feature.search.data.remote.dto.GeocodingCityDto
import com.mw.medical.weatherapp.feature.search.domain.model.City
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class GeocodingMapperTest {

    val tested = GeocodingCityDto::toDomain

    @Test
    fun `should map dto to city`() {
        val dto: GeocodingCityDto = mockk {
            every { name } returns "London"
            every { lat } returns 51.5
            every { lon } returns -0.12
            every { country } returns "GB"
            every { state } returns "England"
        }

        val result = tested(dto)

        result shouldBeEqualTo City(
            name = "London",
            state = "England",
            country = "GB",
            coordinates = Coordinates(
                latitude = 51.5,
                longitude = -0.12,
            ),
        )
    }
}
