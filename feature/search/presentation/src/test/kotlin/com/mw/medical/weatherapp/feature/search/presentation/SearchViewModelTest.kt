package com.mw.medical.weatherapp.feature.search.presentation

import app.cash.turbine.test
import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.testing.MainDispatcherExtension
import com.mw.medical.weatherapp.feature.search.domain.model.City
import com.mw.medical.weatherapp.feature.search.domain.usecase.SearchCitiesUseCase
import com.mw.medical.weatherapp.feature.search.presentation.SearchContract.SearchResults
import com.mw.medical.weatherapp.feature.search.presentation.model.CityUiModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchViewModelTest {

    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()
    val searchCities: SearchCitiesUseCase = mockk()
    val tested = SearchViewModel(searchCities)

    @Test
    fun `should show results for a matching query`() = runTest(mainDispatcher.dispatcher) {
        coEvery { searchCities("london") } returns Result.Success(
            listOf(
                City(
                    name = "London",
                    state = "England",
                    country = "GB",
                    coordinates = Coordinates(
                        latitude = 51.5,
                        longitude = -0.12,
                    ),
                ),
            ),
        )

        tested.onAction(SearchContract.Action.QueryChanged("london"))
        advanceUntilIdle()

        tested.state.value.results shouldBeEqualTo SearchResults.Content(
            listOf(
                CityUiModel(
                    name = "London",
                    subtitle = "England, GB",
                    latitude = 51.5,
                    longitude = -0.12,
                ),
            ),
        )
    }

    @Test
    fun `should show no matches when nothing is found`() = runTest(mainDispatcher.dispatcher) {
        coEvery { searchCities("nowhere") } returns Result.Failure(AppError.NotFound)

        tested.onAction(SearchContract.Action.QueryChanged("nowhere"))
        advanceUntilIdle()

        tested.state.value.results shouldBeEqualTo SearchResults.NoMatches
    }

    @Test
    fun `should show error when search fails`() = runTest(mainDispatcher.dispatcher) {
        coEvery { searchCities("london") } returns Result.Failure(AppError.Generic)

        tested.onAction(SearchContract.Action.QueryChanged("london"))
        advanceUntilIdle()

        tested.state.value.results shouldBeEqualTo SearchResults.Error
    }

    @Test
    fun `should search only the latest query within the debounce window`() = runTest(mainDispatcher.dispatcher) {
        coEvery { searchCities("london") } returns Result.Success(listOf(mockk(relaxed = true)))

        tested.onAction(SearchContract.Action.QueryChanged("lond"))
        tested.onAction(SearchContract.Action.QueryChanged("london"))
        advanceUntilIdle()

        coVerify(exactly = 0) { searchCities("lond") }
        coVerify(exactly = 1) { searchCities("london") }
    }

    @Test
    fun `should search again immediately on retry after a failure`() = runTest(mainDispatcher.dispatcher) {
        coEvery { searchCities("london") } returns Result.Failure(AppError.Generic)
        tested.onAction(SearchContract.Action.QueryChanged("london"))
        advanceUntilIdle()

        coEvery { searchCities("london") } returns Result.Success(listOf(mockk(relaxed = true)))
        tested.onAction(SearchContract.Action.Retry)
        advanceUntilIdle()

        tested.state.value.results.shouldBeInstanceOf<SearchResults.Content>()
        coVerify(exactly = 2) { searchCities("london") }
    }

    @Test
    fun `should not search on retry with a blank query`() = runTest(mainDispatcher.dispatcher) {
        tested.onAction(SearchContract.Action.Retry)
        advanceUntilIdle()

        coVerify(exactly = 0) { searchCities(any()) }
    }

    @Test
    fun `should stay idle and not search for a blank query`() = runTest(mainDispatcher.dispatcher) {
        tested.onAction(SearchContract.Action.QueryChanged("   "))
        advanceUntilIdle()

        tested.state.value.results shouldBeEqualTo SearchResults.Idle
        coVerify(exactly = 0) { searchCities(any()) }
    }

    @Test
    fun `should emit city chosen effect when a city is selected`() = runTest(mainDispatcher.dispatcher) {
        tested.sideEffect.test {
            tested.onAction(
                SearchContract.Action.CitySelected(
                    CityUiModel(
                        name = "London",
                        subtitle = "England, GB",
                        latitude = 51.5,
                        longitude = -0.12,
                    ),
                ),
            )

            awaitItem() shouldBeEqualTo SearchContract.SideEffect.CityChosen(
                latitude = 51.5,
                longitude = -0.12,
            )
            cancelAndConsumeRemainingEvents()
        }
    }
}
