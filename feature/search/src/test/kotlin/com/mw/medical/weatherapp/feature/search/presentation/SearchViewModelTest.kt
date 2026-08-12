package com.mw.medical.weatherapp.feature.search.presentation

import app.cash.turbine.test
import com.mw.medical.weatherapp.core.common.error.AppError
import com.mw.medical.weatherapp.core.common.result.Result
import com.mw.medical.weatherapp.core.domain.model.City
import com.mw.medical.weatherapp.core.domain.model.Coordinates
import com.mw.medical.weatherapp.core.domain.usecase.SearchCitiesUseCase
import com.mw.medical.weatherapp.feature.search.presentation.SearchContract.SearchResults
import com.mw.medical.weatherapp.feature.search.presentation.model.CityUiModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchViewModelTest {

    val mainDispatcher = UnconfinedTestDispatcher()
    val searchCities: SearchCitiesUseCase = mockk()
    val tested = SearchViewModel(searchCities)

    @BeforeEach
    fun setUp() = Dispatchers.setMain(mainDispatcher)

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `should show results for a matching query`() = runTest(mainDispatcher) {
        coEvery { searchCities("london") } returns Result.Success(
            listOf(
                City(
                    name = "London",
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
                    country = "GB",
                    latitude = 51.5,
                    longitude = -0.12,
                ),
            ),
        )
    }

    @Test
    fun `should show no matches when nothing is found`() = runTest(mainDispatcher) {
        coEvery { searchCities("nowhere") } returns Result.Failure(AppError.NotFound)

        tested.onAction(SearchContract.Action.QueryChanged("nowhere"))
        advanceUntilIdle()

        tested.state.value.results shouldBeEqualTo SearchResults.NoMatches
    }

    @Test
    fun `should show error when search fails`() = runTest(mainDispatcher) {
        coEvery { searchCities("london") } returns Result.Failure(AppError.Generic)

        tested.onAction(SearchContract.Action.QueryChanged("london"))
        advanceUntilIdle()

        tested.state.value.results shouldBeEqualTo SearchResults.Error
    }

    @Test
    fun `should stay idle and not search for a blank query`() = runTest(mainDispatcher) {
        tested.onAction(SearchContract.Action.QueryChanged("   "))
        advanceUntilIdle()

        tested.state.value.results shouldBeEqualTo SearchResults.Idle
        coVerify(exactly = 0) { searchCities(any()) }
    }

    @Test
    fun `should emit city chosen effect when a city is selected`() = runTest(mainDispatcher) {
        tested.sideEffect.test {
            tested.onAction(
                SearchContract.Action.CitySelected(
                    CityUiModel(
                        name = "London",
                        country = "GB",
                        latitude = 51.5,
                        longitude = -0.12,
                    ),
                ),
            )

            awaitItem() shouldBeEqualTo SearchContract.SideEffect.CityChosen(
                latitude = 51.5,
                longitude = -0.12,
                name = "London",
            )
            cancelAndConsumeRemainingEvents()
        }
    }
}
