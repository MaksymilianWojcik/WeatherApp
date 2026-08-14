package com.mw.medical.weatherapp.feature.search.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SearchRoute(
    onCitySelected: (latitude: Double, longitude: Double) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<SearchViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SearchContract.SideEffect.CityChosen -> onCitySelected(
                    effect.latitude,
                    effect.longitude,
                )
            }
        }
    }

    SearchScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}
