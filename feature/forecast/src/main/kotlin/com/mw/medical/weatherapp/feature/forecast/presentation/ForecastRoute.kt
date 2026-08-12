package com.mw.medical.weatherapp.feature.forecast.presentation

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ForecastRoute() {
    val viewModel = hiltViewModel<ForecastViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val permission = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)

    LaunchedEffect(permission.status.isGranted) {
        viewModel.onAction(ForecastContract.Action.OnLocationPermissionResult(permission.status.isGranted))
    }

    ForecastScreen(
        state = state,
        onAction = viewModel::onAction,
        onRequestPermission = { permission.launchPermissionRequest() },
    )
}
