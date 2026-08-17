package com.mw.medical.weatherapp.feature.forecast.presentation

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.component.ErrorCard
import com.mw.medical.weatherapp.core.designsystem.component.Skeleton
import com.mw.medical.weatherapp.core.designsystem.component.rememberSkeletonAlpha
import com.mw.medical.weatherapp.core.designsystem.icon.WeatherAppIcons
import com.mw.medical.weatherapp.core.designsystem.icon.WeatherIcon
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherTheme
import com.mw.medical.weatherapp.feature.forecast.presentation.ForecastContract.SectionState
import com.mw.medical.weatherapp.feature.forecast.presentation.R
import com.mw.medical.weatherapp.feature.forecast.presentation.component.CurrentWeatherCard
import com.mw.medical.weatherapp.feature.forecast.presentation.component.DailyForecastList
import com.mw.medical.weatherapp.feature.forecast.presentation.component.HourlyForecastRow
import com.mw.medical.weatherapp.feature.forecast.presentation.model.CurrentWeatherUiModel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.DailyForecastUiModel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.ForecastUiModel
import com.mw.medical.weatherapp.feature.forecast.presentation.model.HourlyForecastUiModel

private val ScreenPadding = 16.dp

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
        LocationBar(
            locationName = state.locationName ?: stringResource(R.string.forecast_current_location),
            onClick = onSearchClick,
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                state.showSettingsPrompt -> LocationPrompt(
                    icon = WeatherAppIcons.LocationOff,
                    rationale = stringResource(R.string.forecast_permission_settings_rationale),
                    actionLabel = stringResource(R.string.forecast_permission_open_settings),
                    onClick = onOpenAppSettings,
                )
                state.showPermissionPrompt -> LocationPrompt(
                    icon = WeatherAppIcons.LocationPin,
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
private fun LocationBar(
    locationName: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 56.dp)
            .padding(
                start = ScreenPadding,
                end = 8.dp,
                top = 4.dp,
                bottom = 4.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(WeatherAppIcons.LocationPin),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = locationName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.forecast_search_action),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}

@Composable
private fun LocationPrompt(
    @DrawableRes icon: Int,
    rationale: String,
    actionLabel: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = ScreenPadding,
                vertical = 12.dp,
            ),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = rationale,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Button(onClick = onClick) {
                Text(text = actionLabel)
            }
        }
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
            .padding(bottom = 24.dp),
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
    val modifier = Modifier.padding(
        start = ScreenPadding,
        end = ScreenPadding,
        top = 4.dp,
        bottom = 8.dp,
    )
    when (section) {
        SectionState.Loading -> Skeleton(
            modifier = modifier
                .fillMaxWidth()
                .height(172.dp),
            shape = MaterialTheme.shapes.extraLarge,
        )
        is SectionState.Content -> CurrentWeatherCard(
            current = section.value,
            modifier = modifier,
        )
        SectionState.Error -> ErrorCard(
            message = stringResource(R.string.forecast_current_error),
            onRetry = onRetry,
            modifier = modifier,
        )
    }
}

@Composable
private fun ForecastSection(
    section: SectionState<ForecastUiModel>,
    onRetry: () -> Unit,
) {
    when (section) {
        SectionState.Loading -> ForecastSkeleton()
        is SectionState.Content -> ForecastDetails(section.value)
        SectionState.Error -> ErrorCard(
            message = stringResource(R.string.forecast_sections_error),
            onRetry = onRetry,
            modifier = Modifier.padding(
                horizontal = ScreenPadding,
                vertical = 12.dp,
            ),
        )
    }
}

@Composable
private fun ForecastDetails(forecast: ForecastUiModel) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        if (forecast.hourly.isNotEmpty()) {
            SectionLabel(text = stringResource(R.string.forecast_hourly_title))
            HourlyForecastRow(forecast.hourly)
        }
        if (forecast.daily.isNotEmpty()) {
            SectionLabel(
                text = stringResource(R.string.forecast_daily_title),
                modifier = Modifier.padding(top = 28.dp),
            )
            DailyForecastList(
                daily = forecast.daily,
                modifier = Modifier.padding(horizontal = ScreenPadding),
            )
        }
    }
}

@Composable
private fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        style = WeatherTheme.sectionLabelStyle,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(
            start = ScreenPadding,
            end = ScreenPadding,
            bottom = 12.dp,
        ),
    )
}

@Composable
private fun ForecastSkeleton() {
    val alpha = rememberSkeletonAlpha()
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Skeleton(
            alpha = alpha,
            modifier = Modifier
                .padding(
                    start = ScreenPadding,
                    end = ScreenPadding,
                    bottom = 12.dp,
                )
                .size(
                    width = 110.dp,
                    height = 14.dp,
                ),
        )
        Row(
            modifier = Modifier.padding(horizontal = ScreenPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(6) {
                Skeleton(
                    modifier = Modifier.size(
                        width = 64.dp,
                        height = 108.dp,
                    ),
                    shape = MaterialTheme.shapes.large,
                    alpha = alpha,
                )
            }
        }
        Skeleton(
            modifier = Modifier
                .padding(
                    start = ScreenPadding,
                    end = ScreenPadding,
                    top = 28.dp,
                    bottom = 12.dp,
                )
                .size(
                    width = 88.dp,
                    height = 14.dp,
                ),
            alpha = alpha,
        )
        Column(
            modifier = Modifier.padding(horizontal = ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            repeat(5) {
                Skeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    alpha = alpha,
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ForecastScreenContentPreview() {
    WeatherAppTheme {
        ForecastScreen(
            state = ForecastContract.State(
                locationPermission = LocationPermissionStatus.Granted,
                locationName = "Szczecin, Poland",
                current = SectionState.Content(
                    CurrentWeatherUiModel(
                        temperature = "20°",
                        description = "clear sky",
                        icon = WeatherIcon.ClearDay,
                    ),
                ),
                forecast = SectionState.Content(
                    ForecastUiModel(
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
                                icon = WeatherIcon.ShowerRain,
                                description = "shower rain",
                            ),
                            HourlyForecastUiModel(
                                time = "15:00",
                                temperature = "17°",
                                icon = WeatherIcon.Thunderstorm,
                                description = "thunderstorm",
                            ),
                        ),
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ForecastScreenContentDarkPreview() {
    WeatherAppTheme {
        ForecastScreen(
            state = ForecastContract.State(
                locationPermission = LocationPermissionStatus.Granted,
                locationName = "Szczecin, Poland",
                current = SectionState.Content(
                    CurrentWeatherUiModel(
                        temperature = "16°",
                        description = "clear sky",
                        icon = WeatherIcon.ClearNight,
                    ),
                ),
                forecast = SectionState.Content(
                    ForecastUiModel(
                        hourly = listOf(
                            HourlyForecastUiModel(
                                time = "21:00",
                                temperature = "16°",
                                icon = WeatherIcon.ClearNight,
                                description = "clear sky",
                            ),
                            HourlyForecastUiModel(
                                time = "22:00",
                                temperature = "15°",
                                icon = WeatherIcon.BrokenClouds,
                                description = "broken clouds",
                            ),
                            HourlyForecastUiModel(
                                time = "23:00",
                                temperature = "14°",
                                icon = WeatherIcon.Snow,
                                description = "snow",
                            ),
                        ),
                        daily = listOf(
                            DailyForecastUiModel(
                                day = "Mon",
                                minTemperature = "12°",
                                maxTemperature = "21°",
                                icon = WeatherIcon.ScatteredClouds,
                                description = "scattered clouds",
                            ),
                            DailyForecastUiModel(
                                day = "Tue",
                                minTemperature = "11°",
                                maxTemperature = "19°",
                                icon = WeatherIcon.Rain,
                                description = "rain",
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
private fun ForecastScreenErrorPreview() {
    WeatherAppTheme {
        ForecastScreen(
            state = ForecastContract.State(
                locationPermission = LocationPermissionStatus.Granted,
                locationName = "Szczecin, Poland",
                current = SectionState.Error,
                forecast = SectionState.Error,
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
