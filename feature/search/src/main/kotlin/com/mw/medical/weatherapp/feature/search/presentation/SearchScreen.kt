package com.mw.medical.weatherapp.feature.search.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.component.GenericError
import com.mw.medical.weatherapp.core.designsystem.component.LoadingIndicator
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme
import com.mw.medical.weatherapp.feature.search.R
import com.mw.medical.weatherapp.feature.search.presentation.component.CityList
import com.mw.medical.weatherapp.feature.search.presentation.model.CityUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    state: SearchContract.State,
    onAction: (SearchContract.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.search_title)) })
        },
    ) { padding ->
        val focusManager = LocalFocusManager.current
        val clearQueryDescription = stringResource(R.string.search_clear_query)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { onAction(SearchContract.Action.QueryChanged(it)) },
                label = { Text(text = stringResource(R.string.search_hint)) },
                singleLine = true,
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { onAction(SearchContract.Action.QueryChanged("")) }) {
                            Text(
                                text = "✕",
                                modifier = Modifier.semantics {
                                    contentDescription = clearQueryDescription
                                },
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
            SearchResultsContent(
                results = state.results,
                onCityClick = { onAction(SearchContract.Action.CitySelected(it)) },
                onRetry = { onAction(SearchContract.Action.Retry) },
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    results: SearchContract.SearchResults,
    onCityClick: (CityUiModel) -> Unit,
    onRetry: () -> Unit,
) {
    when (results) {
        SearchContract.SearchResults.Idle -> CenteredMessage(stringResource(R.string.search_prompt))
        SearchContract.SearchResults.Loading -> LoadingIndicator(Modifier.fillMaxSize())
        is SearchContract.SearchResults.Content -> CityList(
            cities = results.cities,
            onCityClick = onCityClick,
            modifier = Modifier.fillMaxSize(),
        )
        SearchContract.SearchResults.NoMatches -> CenteredMessage(stringResource(R.string.search_no_matches))
        SearchContract.SearchResults.Error -> GenericError(
            onRetry = onRetry,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun CenteredMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenIdlePreview() {
    WeatherAppTheme {
        SearchScreen(
            state = SearchContract.State(
                query = "",
                results = SearchContract.SearchResults.Idle,
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenContentPreview() {
    WeatherAppTheme {
        SearchScreen(
            state = SearchContract.State(
                query = "Lond",
                results = SearchContract.SearchResults.Content(
                    listOf(
                        CityUiModel(
                            name = "London",
                            country = "GB",
                            latitude = 51.5,
                            longitude = -0.12,
                        ),
                        CityUiModel(
                            name = "London",
                            country = "US",
                            latitude = 39.9,
                            longitude = -83.4,
                        ),
                    ),
                ),
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenNoMatchesPreview() {
    WeatherAppTheme {
        SearchScreen(
            state = SearchContract.State(
                query = "zzzzz",
                results = SearchContract.SearchResults.NoMatches,
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenErrorPreview() {
    WeatherAppTheme {
        SearchScreen(
            state = SearchContract.State(
                query = "london",
                results = SearchContract.SearchResults.Error,
            ),
            onAction = {},
        )
    }
}
