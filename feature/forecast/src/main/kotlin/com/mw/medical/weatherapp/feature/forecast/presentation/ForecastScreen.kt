package com.mw.medical.weatherapp.feature.forecast.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.mw.medical.weatherapp.feature.forecast.presentation.model.ForecastUiModel

@Composable
internal fun ForecastScreen(
    state: ForecastContract.State,
    onAction: (ForecastContract.Action) -> Unit,
    onRequestPermission: () -> Unit,
    onOpenAppSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isPermissionPermanentlyDenied -> LocationPermissionPrompt(
                rationale = stringResource(R.string.forecast_permission_settings_rationale),
                actionLabel = stringResource(R.string.forecast_permission_open_settings),
                onClick = onOpenAppSettings,
            )
            state.isPermissionDenied -> LocationPermissionPrompt(
                rationale = stringResource(R.string.forecast_permission_rationale),
                actionLabel = stringResource(R.string.forecast_permission_grant),
                onClick = onRequestPermission,
            )
            state.isLoading -> LoadingIndicator()
            state.hasError -> GenericError(onRetry = { onAction(ForecastContract.Action.Retry) })
            state.forecast != null -> ForecastContent(state.forecast)
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
private fun ForecastContent(forecast: ForecastUiModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = forecast.temperature,
            style = MaterialTheme.typography.displayLarge,
        )
        Text(
            text = forecast.description,
            style = MaterialTheme.typography.bodyLarge,
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
                isLoading = false,
                forecast = null,
                hasError = false,
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
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
                isLoading = false,
                forecast = null,
                hasError = false,
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
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
                isLoading = false,
                forecast = ForecastUiModel(
                    temperature = "20°",
                    description = "clear sky",
                    iconCode = "01d",
                ),
                hasError = false,
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
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
                isLoading = true,
                forecast = null,
                hasError = false,
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
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
                isLoading = false,
                forecast = null,
                hasError = true,
            ),
            onAction = {},
            onRequestPermission = {},
            onOpenAppSettings = {},
        )
    }
}
