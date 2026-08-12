package com.mw.medical.weatherapp.feature.forecast.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme
import com.mw.medical.weatherapp.feature.forecast.presentation.model.HourlyForecastUiModel

@Composable
internal fun HourlyForecastRow(
    hourly: List<HourlyForecastUiModel>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        items(hourly) { item ->
            HourlyForecastItem(item)
        }
    }
}

@Composable
private fun HourlyForecastItem(item: HourlyForecastUiModel) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = item.time,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = item.icon,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { contentDescription = item.description },
            )
            Text(
                text = item.temperature,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HourlyForecastRowPreview() {
    WeatherAppTheme {
        HourlyForecastRow(
            hourly = listOf(
                HourlyForecastUiModel(
                    time = "12:00",
                    temperature = "20°",
                    icon = "☀️",
                    description = "clear sky",
                ),
                HourlyForecastUiModel(
                    time = "15:00",
                    temperature = "19°",
                    icon = "🌤️",
                    description = "few clouds",
                ),
                HourlyForecastUiModel(
                    time = "18:00",
                    temperature = "17°",
                    icon = "⛅",
                    description = "scattered clouds",
                ),
            ),
        )
    }
}
