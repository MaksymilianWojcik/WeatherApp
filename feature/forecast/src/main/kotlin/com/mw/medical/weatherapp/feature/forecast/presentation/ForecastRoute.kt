package com.mw.medical.weatherapp.feature.forecast.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ForecastRoute() {
    val viewModel = hiltViewModel<ForecastViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permission = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)
    var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }

    val permissionStatus = when {
        permission.status.isGranted -> LocationPermissionStatus.Granted
        hasRequestedPermission && !permission.status.shouldShowRationale -> LocationPermissionStatus.PermanentlyDenied
        else -> LocationPermissionStatus.Denied
    }

    LaunchedEffect(permissionStatus) {
        viewModel.onAction(ForecastContract.Action.OnLocationPermissionResult(permissionStatus))
    }

    ForecastScreen(
        state = state,
        onAction = viewModel::onAction,
        onRequestPermission = {
            hasRequestedPermission = true
            permission.launchPermissionRequest()
        },
        onOpenAppSettings = { context.openApplicationSettings() },
    )
}

private fun Context.openApplicationSettings() {
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null),
        ),
    )
}
