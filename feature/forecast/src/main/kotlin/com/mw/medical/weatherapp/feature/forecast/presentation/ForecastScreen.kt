package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.component.GenericError
import com.mw.medical.weatherapp.core.designsystem.component.LoadingIndicator
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme
import com.mw.medical.weatherapp.feature.forecast.R
import com.mw.medical.weatherapp.feature.forecast.presentation.ForecastContract.SectionState
import com.mw.medical.weatherapp.feature.forecast.presentation.component.CurrentWeatherCard
import com.mw.medical.weatherapp.feature.forecast.presentation.component.DailyForecastList
import com.mw.medical.weatherapp.feature.forecast.presentation.component.HourlyForecastRow
import com.mw.medical.weatherapp.feature.forecast.presentation.model.CurrentWeatherUiModel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.DailyForecastUiModel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.ForecastUiModel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.HourlyForecastUiModel

private val ScreenPadding = 24.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ForecastScreen(
    state: ForecastContract.State,
    onAction: (ForecastContract.Action) -> Unit,
    onRequestPermission: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
    ) {
        LocationSearchBar(
            locationName = state.locationName ?: stringResource(R.string.forecast_current_location),
            onClick = onSearchClick,
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                state.showSettingsPrompt -> LocationPermissionPrompt(
                    rationale = stringResource(R.string.forecast_permission_settings_rationale),
                    actionLabel = stringResource(R.string.forecast_permission_open_settings),
                    onClick = onOpenAppSettings,
                )
                state.showPermissionPrompt -> LocationPermissionPrompt(
                    rationale = stringResource(R.string.forecast_permission_rationale),
                    actionLabel = stringResource(R.string.forecast_permission_grant),
                    onClick = onRequestPermission,
                )
                else -> PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onAction(ForecastContract.Action.RefreshRequested) },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ForecastContent(
                        current = state.current,
                        forecast = state.forecast,
                        onRetry = { onAction(ForecastContract.Action.Retry) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationSearchBar(
    locationName: String,
    onClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(ScreenPadding),
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = locationName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.forecast_search_action),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun LocationPermissionPrompt(
    rationale: String,
    actionLabel: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = rationale,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onClick) { Text(text = actionLabel) }
    }
}

@Composable
private fun ForecastContent(
    current: SectionState<CurrentWeatherUiModel>,
    forecast: SectionState<ForecastUiModel>,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        CurrentWeatherSection(
            section = current,
            onRetry = onRetry,
        )
        ForecastSection(
            section = forecast,
            onRetry = onRetry,
        )
    }
}

@Composable
private fun CurrentWeatherSection(
    section: SectionState<CurrentWeatherUiModel>,
    onRetry: () -> Unit,
) {
    when (section) {
        SectionState.Loading -> SectionLoading()
        is SectionState.Content -> CurrentWeatherCard(
            current = section.value,
            modifier = Modifier.padding(horizontal = ScreenPadding),
        )
        SectionState.Error -> GenericError(
            onRetry = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPadding),
        )
    }
}

@Composable
private fun ForecastSection(
    section: SectionState<ForecastUiModel>,
    onRetry: () -> Unit,
) {
    when (section) {
        SectionState.Loading -> SectionLoading()
        is SectionState.Content -> ForecastDetails(section.value)
        SectionState.Error -> GenericError(
            onRetry = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPadding),
        )
    }
}

@Composable
private fun ForecastDetails(forecast: ForecastUiModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        if (forecast.hourly.isNotEmpty()) {
            TitledSection(title = stringResource(R.string.forecast_hourly_title)) {
                HourlyForecastRow(forecast.hourly)
            }
        }
        if (forecast.daily.isNotEmpty()) {
            TitledSection(title = stringResource(R.string.forecast_daily_title)) {
                DailyForecastList(
                    daily = forecast.daily,
                    modifier = Modifier.padding(horizontal = ScreenPadding),
                )
            }
        }
    }
}

@Composable
private fun TitledSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = ScreenPadding),
        )
        content()
    }
}

@Composable
private fun SectionLoading() {
    LoadingIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun ForecastScreenPermissionPreview() {
    WeatherAppTheme {
        ForecastScreen(
            state = ForecastContract.State(
                locationPermission = LocationPermissionStatus.Denied,
                locationName = null,
                current = SectionState.Loading,
                forecast = SectionState.Loading,
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
            onSearchClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForecastScreenPermanentlyDeniedPreview() {
    WeatherAppTheme {
        ForecastScreen(
            state = ForecastContract.State(
                locationPermission = LocationPermissionStatus.PermanentlyDenied,
                locationName = null,
                current = SectionState.Loading,
                forecast = SectionState.Loading,
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
            onSearchClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForecastScreenContentPreview() {
    WeatherAppTheme {
        ForecastScreen(
            state = ForecastContract.State(
                locationPermission = LocationPermissionStatus.Granted,
                locationName = null,
                current = SectionState.Content(
                    CurrentWeatherUiModel(
                        temperature = "20°",
                        description = "clear sky",
                        icon = "☀️",
                    ),
                ),
                forecast = SectionState.Content(
                    ForecastUiModel(
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
                    ),
                ),
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
            onSearchClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForecastScreenLoadingPreview() {
    WeatherAppTheme {
        ForecastScreen(
            state = ForecastContract.State(
                locationPermission = LocationPermissionStatus.Granted,
                locationName = null,
                current = SectionState.Loading,
                forecast = SectionState.Loading,
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
            onSearchClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForecastScreenRefreshingPreview() {
    WeatherAppTheme {
        ForecastScreen(
            state = ForecastContract.State(
                locationPermission = LocationPermissionStatus.Granted,
                locationName = "Szczecin, Poland",
                current = SectionState.Content(
                    CurrentWeatherUiModel(
                        temperature = "20°",
                        description = "clear sky",
                        icon = "☀️",
                    ),
                ),
                forecast = SectionState.Content(
                    ForecastUiModel(
                        hourly = listOf(
                            HourlyForecastUiModel(
                                time = "12:00",
                                temperature = "20°",
                                icon = "☀️",
                                description = "clear sky",
                            ),
                        ),
                        daily = listOf(
                            DailyForecastUiModel(
                                day = "Mon",
                                minTemperature = "12°",
                                maxTemperature = "21°",
                                icon = "☀️",
                                description = "clear sky",
                            ),
                        ),
                    ),
                ),
                isRefreshing = true,
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
            onSearchClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForecastScreenPartialErrorPreview() {
    WeatherAppTheme {
        ForecastScreen(
            state = ForecastContract.State(
                locationPermission = LocationPermissionStatus.Granted,
                locationName = null,
                current = SectionState.Content(
                    CurrentWeatherUiModel(
                        temperature = "20°",
                        description = "clear sky",
                        icon = "☀️",
                    ),
                ),
                forecast = SectionState.Error,
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
            onSearchClick = {},
        )
    }
}
