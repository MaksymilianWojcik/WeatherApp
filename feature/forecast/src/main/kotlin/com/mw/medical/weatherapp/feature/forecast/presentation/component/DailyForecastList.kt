package com.mw.medical.weatherapp.feature.forecast.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme
import com.mw.medical.weatherapp.feature.forecast.R
import com.mw.medical.weatherapp.feature.forecast.presentation.model.DailyForecastUiModel

@Composable
internal fun DailyForecastList(
    daily: List<DailyForecastUiModel>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            daily.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                DailyForecastRow(item)
            }
        }
    }
}

@Composable
private fun DailyForecastRow(item: DailyForecastUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 14.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.day,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = item.icon,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { contentDescription = item.description },
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(
                R.string.forecast_temperature_range,
                item.minTemperature,
                item.maxTemperature,
            ),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyForecastListPreview() {
    WeatherAppTheme {
        DailyForecastList(
            daily = listOf(
                DailyForecastUiModel(
                    day = "Mon",
                    minTemperature = "12°",
                    maxTemperature = "21°",
                    icon = "☀️",
                    description = "clear sky",
                ),
                DailyForecastUiModel(
                    day = "Tue",
                    minTemperature = "11°",
                    maxTemperature = "19°",
                    icon = "🌦️",
                    description = "light rain",
                ),
            ),
            modifier = Modifier.padding(24.dp),
        )
    }
}
