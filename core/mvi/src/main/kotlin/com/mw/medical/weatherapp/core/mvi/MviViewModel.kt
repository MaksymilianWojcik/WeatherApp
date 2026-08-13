package com.mw.medical.weatherapp.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MviViewModel<S : UiState, A : UiAction, E : SideEffect>(
    initialState: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _sideEffect = Channel<E>(Channel.BUFFERED)
    val sideEffect: Flow<E> = _sideEffect.receiveAsFlow()

    abstract fun onAction(action: A)

    protected fun updateState(reducer: S.() -> S) = _state.update(reducer)

    protected fun emitSideEffect(effect: E) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}
