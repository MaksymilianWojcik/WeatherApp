package com.mw.medical.weatherapp.feature.forecast.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.icon.WeatherIcon
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherTheme
import com.mw.medical.weatherapp.feature.forecast.presentation.model.CurrentWeatherUiModel

@Composable
internal fun CurrentWeatherCard(
    current: CurrentWeatherUiModel,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.extraLarge
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = shape,
                spotColor = WeatherTheme.colors.heroStart,
            )
            .clip(shape)
            .background(WeatherTheme.heroGradient)
            .padding(
                horizontal = 24.dp,
                vertical = 22.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = current.temperature,
                style = MaterialTheme.typography.displayLarge,
                color = WeatherTheme.colors.onHero,
            )
            Text(
                text = current.description,
                style = MaterialTheme.typography.bodyLarge,
                color = WeatherTheme.colors.onHero.copy(alpha = 0.92f),
            )
        }
        Image(
            painter = painterResource(current.icon.drawableRes),
            contentDescription = null,
            colorFilter = ColorFilter.tint(WeatherTheme.colors.onHero.copy(alpha = 0.94f)),
            modifier = Modifier.size(88.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentWeatherCardPreview() {
    WeatherAppTheme {
        CurrentWeatherCard(
            current = CurrentWeatherUiModel(
                temperature = "20°",
                description = "clear sky",
                icon = WeatherIcon.ClearDay,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentWeatherCardDarkPreview() {
    WeatherAppTheme(darkTheme = true) {
        CurrentWeatherCard(
            current = CurrentWeatherUiModel(
                temperature = "20°",
                description = "few clouds",
                icon = WeatherIcon.FewClouds,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
