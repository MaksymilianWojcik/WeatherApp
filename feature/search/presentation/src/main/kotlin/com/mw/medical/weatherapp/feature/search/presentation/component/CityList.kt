package com.mw.medical.weatherapp.feature.search.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme
import com.mw.medical.weatherapp.feature.search.presentation.model.CityUiModel

@Composable
internal fun CityList(
    cities: List<CityUiModel>,
    onCityClick: (CityUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        items(cities) { city ->
            CityRow(
                city = city,
                onClick = { onCityClick(city) },
            )
        }
    }
}

@Composable
private fun CityRow(
    city: CityUiModel,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 72.dp)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(
            text = city.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = city.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CityListPreview() {
    WeatherAppTheme {
        CityList(
            cities = listOf(
                CityUiModel(
                    name = "Manchester",
                    subtitle = "England, GB",
                    latitude = 53.5,
                    longitude = -2.2,
                ),
                CityUiModel(
                    name = "Mannheim",
                    subtitle = "Baden-Württemberg, DE",
                    latitude = 49.5,
                    longitude = 8.5,
                ),
            ),
            onCityClick = {},
        )
    }
}
