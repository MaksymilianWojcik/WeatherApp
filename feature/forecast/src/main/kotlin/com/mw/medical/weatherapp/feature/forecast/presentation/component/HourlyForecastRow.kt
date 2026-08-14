package com.mw.medical.weatherapp.feature.forecast.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.icon.WeatherIcon
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme
import com.mw.medical.weatherapp.feature.forecast.presentation.model.HourlyForecastUiModel

@Composable
internal fun HourlyForecastRow(
    hourly: List<HourlyForecastUiModel>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(hourly) { item ->
            HourlyForecastItem(item)
        }
    }
}

@Composable
private fun HourlyForecastItem(item: HourlyForecastUiModel) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 64.dp)
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = item.time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Image(
                painter = painterResource(item.icon.drawableRes),
                contentDescription = item.description,
                modifier = Modifier.size(30.dp),
            )
            Text(
                text = item.temperature,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
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
                    icon = WeatherIcon.ClearDay,
                    description = "clear sky",
                ),
                HourlyForecastUiModel(
                    time = "13:00",
                    temperature = "21°",
                    icon = WeatherIcon.FewClouds,
                    description = "few clouds",
                ),
                HourlyForecastUiModel(
                    time = "14:00",
                    temperature = "19°",
                    icon = WeatherIcon.Rain,
                    description = "light rain",
                ),
                HourlyForecastUiModel(
                    time = "15:00",
                    temperature = "17°",
                    icon = WeatherIcon.Thunderstorm,
                    description = "thunderstorm",
                ),
            ),
            modifier = Modifier.padding(vertical = 16.dp),
        )
    }
}
