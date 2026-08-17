package com.mw.medical.weatherapp.core.presentation.mvi

import app.cash.turbine.test
import com.mw.medical.weatherapp.core.testing.MainDispatcherExtension
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

data class CounterState(val count: Int) : UiState

sealed interface CounterAction : UiAction {
    data object Increment : CounterAction
    data object Emit : CounterAction
}

sealed interface CounterSideEffect : SideEffect {
    data object Pinged : CounterSideEffect
}

class CounterViewModel : MviViewModel<CounterState, CounterAction, CounterSideEffect>(CounterState(0)) {
    override fun onAction(action: CounterAction) = when (action) {
        CounterAction.Increment -> updateState { copy(count = count + 1) }
        CounterAction.Emit -> emitSideEffect(CounterSideEffect.Pinged)
    }
}

class MviViewModelTest {

    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()
    val tested = CounterViewModel()

    @Test
    fun `should reduce state on action`() {
        tested.state.value shouldBeEqualTo CounterState(0)

        tested.onAction(CounterAction.Increment)

        tested.state.value shouldBeEqualTo CounterState(1)
    }

    @Test
    fun `should emit side effect once`() = runTest {
        tested.sideEffect.test {
            tested.onAction(CounterAction.Emit)

            awaitItem() shouldBeEqualTo CounterSideEffect.Pinged
            cancelAndConsumeRemainingEvents()
        }
    }
}
