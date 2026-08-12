package com.mw.medical.weatherapp.feature.forecast.presentation

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mw.medical.weatherapp.feature.forecast.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ForecastRoute() {
    val viewModel = hiltViewModel<ForecastViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val permission = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)

    LaunchedEffect(permission.status.isGranted) {
        if (permission.status.isGranted) {
            viewModel.onAction(ForecastContract.Action.Load)
        }
    }

    if (permission.status.isGranted) {
        ForecastScreen(state = state, onAction = viewModel::onAction)
    } else {
        LocationPermissionPrompt(onGrant = { permission.launchPermissionRequest() })
    }
}

@Composable
private fun LocationPermissionPrompt(onGrant: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.forecast_permission_rationale),
            textAlign = TextAlign.Center,
        )
        Button(onClick = onGrant) { Text(text = stringResource(R.string.forecast_permission_grant)) }
    }
}
