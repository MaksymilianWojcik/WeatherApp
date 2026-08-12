package com.mw.medical.weatherapp.feature.search.presentation

import androidx.compose.runtime.Immutable
import com.mw.medical.weatherapp.core.mvi.UiAction
import com.mw.medical.weatherapp.core.mvi.UiState
import com.mw.medical.weatherapp.feature.search.presentation.model.CityUiModel

object SearchContract {

    @Immutable
    data class State(
        val query: String,
        val results: SearchResults,
    ) : UiState {
        companion object {
            val Initial = State(
                query = "",
                results = SearchResults.Idle,
            )
        }
    }

    @Immutable
    sealed interface SearchResults {
        data object Idle : SearchResults
        data object Loading : SearchResults
        data class Content(val cities: List<CityUiModel>) : SearchResults
        data object NoMatches : SearchResults
        data object Error : SearchResults
    }

    sealed interface Action : UiAction {
        data class QueryChanged(val query: String) : Action
        data class CitySelected(val city: CityUiModel) : Action
        data object Retry : Action
    }

    sealed interface SideEffect : com.mw.medical.weatherapp.core.mvi.SideEffect {
        data class CityChosen(
            val latitude: Double,
            val longitude: Double,
        ) : SideEffect
    }
}
