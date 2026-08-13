package com.mw.medical.weatherapp.feature.search.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mw.medical.weatherapp.feature.search.presentation.model.CityUiModel

@Composable
internal fun CityList(
    cities: List<CityUiModel>,
    onCityClick: (CityUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(cities) { city ->
            CityRow(
                city = city,
                onClick = { onCityClick(city) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun CityRow(
    city: CityUiModel,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(text = city.name) },
        supportingContent = { Text(text = city.subtitle) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    )
}
