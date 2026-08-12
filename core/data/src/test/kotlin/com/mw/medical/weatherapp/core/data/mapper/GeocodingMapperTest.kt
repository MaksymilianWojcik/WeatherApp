package com.mw.medical.weatherapp.core.data.mapper

import com.mw.medical.weatherapp.core.data.remote.dto.GeocodingCityDto
import com.mw.medical.weatherapp.core.domain.model.City
import com.mw.medical.weatherapp.core.domain.model.Coordinates
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
        }

        val result = tested(dto)

        result shouldBeEqualTo City(
            name = "London",
            country = "GB",
            coordinates = Coordinates(
                latitude = 51.5,
                longitude = -0.12,
            ),
        )
    }
}
