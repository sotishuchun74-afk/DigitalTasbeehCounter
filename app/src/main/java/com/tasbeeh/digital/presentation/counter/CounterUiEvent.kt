package com.tasbeeh.digital.presentation.counter

import com.tasbeeh.digital.domain.model.CounterThemeType

sealed interface CounterUiEvent {
    data object OnTapIncrement : CounterUiEvent
    data object OnDecrement : CounterUiEvent
    data object RequestResetConfirmation : CounterUiEvent
    data object DismissResetConfirmation : CounterUiEvent
    data object ConfirmReset : CounterUiEvent
    data class UpdateTargetLimit(val newTarget: Int) : CounterUiEvent
    data class SetTheme(val theme: CounterThemeType) : CounterUiEvent
    data object ToggleTimerState : CounterUiEvent
    data object OpenTargetDialog : CounterUiEvent
    data object DismissTargetDialog : CounterUiEvent
}
