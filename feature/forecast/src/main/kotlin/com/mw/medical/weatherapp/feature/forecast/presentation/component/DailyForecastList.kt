package com.mw.medical.weatherapp.feature.forecast.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.icon.WeatherIcon
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherTheme
import com.mw.medical.weatherapp.feature.forecast.presentation.model.DailyForecastUiModel

@Composable
internal fun DailyForecastList(
    daily: List<DailyForecastUiModel>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column {
            daily.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(color = WeatherTheme.colors.divider)
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
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = item.day,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(40.dp),
        )
        Image(
            painter = painterResource(item.icon.drawableRes),
            contentDescription = null,
            modifier = Modifier
                .size(26.dp)
                .semantics { contentDescription = item.description },
        )
        Text(
            text = item.minTemperature,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = item.maxTemperature,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.width(38.dp),
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
                    icon = WeatherIcon.ClearDay,
                    description = "clear sky",
                ),
                DailyForecastUiModel(
                    day = "Tue",
                    minTemperature = "11°",
                    maxTemperature = "19°",
                    icon = WeatherIcon.ShowerRain,
                    description = "light rain",
                ),
                DailyForecastUiModel(
                    day = "Wed",
                    minTemperature = "9°",
                    maxTemperature = "16°",
                    icon = WeatherIcon.Mist,
                    description = "mist",
                ),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
