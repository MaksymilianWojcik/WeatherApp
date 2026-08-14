package com.mw.medical.weatherapp.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.R
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme

@Composable
fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedButton(onClick = onRetry) {
                Text(text = stringResource(R.string.ds_try_again))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorCardPreview() {
    WeatherAppTheme {
        ErrorCard(
            message = "Couldn't load current weather.",
            onRetry = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorCardDarkPreview() {
    WeatherAppTheme {
        ErrorCard(
            message = "Couldn't load current weather.",
            onRetry = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
