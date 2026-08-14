package com.mw.medical.weatherapp.feature.search.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.component.ErrorCard
import com.mw.medical.weatherapp.core.designsystem.component.Skeleton
import com.mw.medical.weatherapp.core.designsystem.component.rememberSkeletonAlpha
import com.mw.medical.weatherapp.core.designsystem.icon.WeatherAppIcons
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme
import com.mw.medical.weatherapp.feature.search.R
import com.mw.medical.weatherapp.feature.search.presentation.component.CityList
import com.mw.medical.weatherapp.feature.search.presentation.model.CityUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    state: SearchContract.State,
    onAction: (SearchContract.Action) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.search_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(WeatherAppIcons.ArrowBack),
                            contentDescription = stringResource(R.string.search_back),
                        )
                    }
                },
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.ime),
    ) { padding ->
        val focusManager = LocalFocusManager.current
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
                shape = MaterialTheme.shapes.medium,
                trailingIcon = {
                    if (state.showClearQuery) {
                        IconButton(onClick = { onAction(SearchContract.Action.QueryChanged("")) }) {
                            Icon(
                                painter = painterResource(WeatherAppIcons.Close),
                                contentDescription = stringResource(R.string.search_clear_query),
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 8.dp,
                    ),
            )
            SearchResultsContent(
                results = state.results,
                onCityClick = { onAction(SearchContract.Action.CitySelected(it)) },
                onRetry = { onAction(SearchContract.Action.Retry) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    results: SearchContract.SearchResults,
    onCityClick: (CityUiModel) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (results) {
        SearchContract.SearchResults.Idle -> IdlePrompt(modifier)
        SearchContract.SearchResults.Loading -> ResultsSkeleton(modifier)
        is SearchContract.SearchResults.Content -> CityList(
            cities = results.cities,
            onCityClick = onCityClick,
            modifier = modifier,
        )
        SearchContract.SearchResults.NoMatches -> NoMatchesMessage(modifier)
        SearchContract.SearchResults.Error -> ErrorCard(
            message = stringResource(R.string.search_error),
            onRetry = onRetry,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
        )
    }
}

@Composable
private fun IdlePrompt(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 48.dp,
                vertical = 24.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(WeatherAppIcons.Search),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .size(32.dp),
        )
        Text(
            text = stringResource(R.string.search_prompt),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun NoMatchesMessage(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 48.dp,
                vertical = 24.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.search_no_matches),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.search_no_matches_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun ResultsSkeleton(
    modifier: Modifier = Modifier,
) {
    val alpha = rememberSkeletonAlpha()
    Column(
        modifier = modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(5) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Skeleton(
                    modifier = Modifier
                        .fillMaxWidth(0.56f)
                        .height(16.dp),
                    alpha = alpha,
                )
                Skeleton(
                    modifier = Modifier
                        .fillMaxWidth(0.34f)
                        .height(12.dp),
                    alpha = alpha,
                )
            }
        }
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
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenContentPreview() {
    WeatherAppTheme {
        SearchScreen(
            state = SearchContract.State(
                query = "man",
                results = SearchContract.SearchResults.Content(
                    listOf(
                        CityUiModel(
                            name = "Manchester",
                            subtitle = "England, GB",
                            latitude = 53.5,
                            longitude = -2.2,
                        ),
                        CityUiModel(
                            name = "Mannheim",
                            subtitle = "Baden-Württemberg, DE",
                            latitude = 49.5,
                            longitude = 8.5,
                        ),
                        CityUiModel(
                            name = "Manaus",
                            subtitle = "Amazonas, BR",
                            latitude = -3.1,
                            longitude = -60.0,
                        ),
                    ),
                ),
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchScreenContentDarkPreview() {
    WeatherAppTheme {
        SearchScreen(
            state = SearchContract.State(
                query = "man",
                results = SearchContract.SearchResults.Content(
                    listOf(
                        CityUiModel(
                            name = "Manchester",
                            subtitle = "England, GB",
                            latitude = 53.5,
                            longitude = -2.2,
                        ),
                        CityUiModel(
                            name = "Manama",
                            subtitle = "Capital, BH",
                            latitude = 26.2,
                            longitude = 50.6,
                        ),
                    ),
                ),
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenLoadingPreview() {
    WeatherAppTheme {
        SearchScreen(
            state = SearchContract.State(
                query = "man",
                results = SearchContract.SearchResults.Loading,
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenNoMatchesPreview() {
    WeatherAppTheme {
        SearchScreen(
            state = SearchContract.State(
                query = "Manchestr",
                results = SearchContract.SearchResults.NoMatches,
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenErrorPreview() {
    WeatherAppTheme {
        SearchScreen(
            state = SearchContract.State(
                query = "man",
                results = SearchContract.SearchResults.Error,
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}
