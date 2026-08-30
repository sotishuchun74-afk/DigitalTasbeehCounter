package com.tasbeeh.digital.presentation.counter

import com.tasbeeh.digital.domain.model.CounterThemeType

data class CounterUiState(
    val counterId: String = "",
    val counterName: String = "Tasbeeh",
    val currentCount: Int = 0,
    val targetCount: Int = 33,
    val roundsCount: Int = 0,
    val progress: Float = 0f,
    val selectedTheme: CounterThemeType = CounterThemeType.MINIMAL_RING,
    val elapsedSessionSeconds: Long = 0L,
    val isSessionTimerRunning: Boolean = false,
    
    val showSessionTimer: Boolean = true,
    val showCounterName: Boolean = true,
    val showRoundTracker: Boolean = true,
    val showLapLimits: Boolean = true,
    val showPercentageBadge: Boolean = true,
    
    val isSoundEnabled: Boolean = true,
    val isHapticEnabled: Boolean = true,
    val hapticIntensity: Float = 0.6f,
    val isHardwareVolumeKeysEnabled: Boolean = true,
    
    val showResetConfirmationDialog: Boolean = false,
    val showTargetInputDialog: Boolean = false
)
