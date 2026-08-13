package com.mw.medical.weatherapp.feature.search.presentation.model

import com.mw.medical.weatherapp.core.domain.model.City
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class CityUiMapperTest {

    val tested = City::toUiModel

    @Test
    fun `should join state and country into the subtitle`() {
        val result = tested(
            City(
                name = "London",
                state = "England",
                country = "GB",
                coordinates = Coordinates(
                    latitude = 51.5,
                    longitude = -0.12,
                ),
            ),
        )

        result shouldBeEqualTo CityUiModel(
            name = "London",
            subtitle = "England, GB",
            latitude = 51.5,
            longitude = -0.12,
        )
    }

    @Test
    fun `should use only the country when state is missing`() {
        val result = tested(
            City(
                name = "London",
                state = null,
                country = "GB",
                coordinates = Coordinates(
                    latitude = 51.5,
                    longitude = -0.12,
                ),
            ),
        )

        result.subtitle shouldBeEqualTo "GB"
    }
}
