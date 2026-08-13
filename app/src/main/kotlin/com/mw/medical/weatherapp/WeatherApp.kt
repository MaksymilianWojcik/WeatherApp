package com.mw.medical.weatherapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mw.medical.weatherapp.feature.forecast.presentation.ForecastRoute
import com.mw.medical.weatherapp.feature.search.presentation.SearchRoute

private const val ROUTE_FORECAST = "forecast"
private const val ROUTE_SEARCH = "search"
private const val KEY_SELECTED_LATITUDE = "selected_latitude"
private const val KEY_SELECTED_LONGITUDE = "selected_longitude"

@Composable
fun WeatherApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ROUTE_FORECAST,
    ) {
        composable(ROUTE_FORECAST) { entry ->
            val savedStateHandle = entry.savedStateHandle
            val latitude by savedStateHandle
                .getStateFlow<Double?>(KEY_SELECTED_LATITUDE, null)
                .collectAsState()
            val longitude by savedStateHandle
                .getStateFlow<Double?>(KEY_SELECTED_LONGITUDE, null)
                .collectAsState()
            ForecastRoute(
                onSearchClick = { navController.navigate(ROUTE_SEARCH) },
                selectedLatitude = latitude,
                selectedLongitude = longitude,
            )
        }
        composable(ROUTE_SEARCH) {
            SearchRoute(
                onCitySelected = { latitude, longitude ->
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set(KEY_SELECTED_LATITUDE, latitude)
                        set(KEY_SELECTED_LONGITUDE, longitude)
                    }
                    navController.popBackStack()
                },
            )
        }
    }
}
