package com.mw.medical.weatherapp.feature.search.presentation

import androidx.lifecycle.viewModelScope
import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.presentation.mvi.MviViewModel
import com.mw.medical.weatherapp.feature.search.domain.usecase.SearchCitiesUseCase
import com.mw.medical.weatherapp.feature.search.presentation.SearchContract.SearchResults
import com.mw.medical.weatherapp.feature.search.presentation.model.CityUiModel
import com.mw.medical.weatherapp.feature.search.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_DEBOUNCE_MILLIS = 300L

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val searchCities: SearchCitiesUseCase,
) : MviViewModel<SearchContract.State, SearchContract.Action, SearchContract.SideEffect>(
    SearchContract.State.Initial,
) {
    private var searchJob: Job? = null

    override fun onAction(action: SearchContract.Action) {
        when (action) {
            is SearchContract.Action.QueryChanged -> onQueryChanged(action.query)
            is SearchContract.Action.CitySelected -> onCitySelected(action.city)
            SearchContract.Action.Retry -> onRetry()
        }
    }

    private fun onQueryChanged(query: String) {
        updateState { copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            updateState { copy(results = SearchResults.Idle) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            performSearch(query)
        }
    }

    private fun onRetry() {
        val query = state.value.query
        if (query.isBlank()) {
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch { performSearch(query) }
    }

    private suspend fun performSearch(query: String) {
        updateState { copy(results = SearchResults.Loading) }
        val results = when (val result = searchCities(query)) {
            is Result.Success -> SearchResults.Content(result.value.map { it.toUiModel() })
            is Result.Failure -> when (result.error) {
                AppError.NotFound -> SearchResults.NoMatches
                AppError.Generic -> SearchResults.Error
            }
        }
        updateState { copy(results = results) }
    }

    private fun onCitySelected(city: CityUiModel) {
        emitSideEffect(
            SearchContract.SideEffect.CityChosen(
                latitude = city.latitude,
                longitude = city.longitude,
            ),
        )
    }
}
