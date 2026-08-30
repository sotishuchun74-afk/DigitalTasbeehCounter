package com.tasbeeh.digital.presentation.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasbeeh.digital.core.hardware.audio.SoundEngine
import com.tasbeeh.digital.core.hardware.haptics.HapticEngine
import com.tasbeeh.digital.core.hardware.keyevent.VolumeKeyEventType
import com.tasbeeh.digital.core.hardware.keyevent.VolumeKeyHandler
import com.tasbeeh.digital.data.local.database.SyncStatus
import com.tasbeeh.digital.data.local.database.dao.CounterDao
import com.tasbeeh.digital.data.local.database.dao.HistoryLogDao
import com.tasbeeh.digital.data.local.entities.CounterEntity
import com.tasbeeh.digital.data.local.entities.HistoryLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val counterDao: CounterDao,
    private val historyLogDao: HistoryLogDao,
    private val hapticEngine: HapticEngine,
    private val soundEngine: SoundEngine,
    private val volumeKeyHandler: VolumeKeyHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(CounterUiState())
    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        observeActiveCounter()
        observeHardwareKeys()
        startSessionTimer()
    }

    private fun observeActiveCounter() {
        viewModelScope.launch {
            counterDao.observeActiveCounter().collectLatest { entity ->
                if (entity != null) {
                    _uiState.update { current ->
                        val target = if (entity.targetLimit <= 0) 33 else entity.targetLimit
                        val progress = (entity.currentCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
                        current.copy(
                            counterId = entity.id,
                            counterName = entity.name,
                            currentCount = entity.currentCount,
                            targetCount = target,
                            roundsCount = entity.totalRounds,
                            progress = progress
                        )
                    }
                } else {
                    val defaultCounter = CounterEntity(
                        name = "SubhanAllah",
                        currentCount = 0,
                        targetLimit = 33,
                        totalRounds = 0,
                        isActive = true,
                        lastModifiedTimestamp = System.currentTimeMillis(),
                        syncStatus = SyncStatus.PENDING_SYNC
                    )
                    counterDao.upsertCounter(defaultCounter)
                }
            }
        }
    }

    private fun observeHardwareKeys() {
        viewModelScope.launch {
            volumeKeyHandler.keyEvents.collectLatest { eventType ->
                if (_uiState.value.isHardwareVolumeKeysEnabled) {
                    when (eventType) {
                        VolumeKeyEventType.INCREMENT -> onEvent(CounterUiEvent.OnTapIncrement)
                        VolumeKeyEventType.DECREMENT -> onEvent(CounterUiEvent.OnDecrement)
                    }
                }
            }
        }
    }

    fun onEvent(event: CounterUiEvent) {
        when (event) {
            CounterUiEvent.OnTapIncrement -> handleIncrement()
            CounterUiEvent.OnDecrement -> handleDecrement()
            CounterUiEvent.RequestResetConfirmation -> _uiState.update { it.copy(showResetConfirmationDialog = true) }
            CounterUiEvent.DismissResetConfirmation -> _uiState.update { it.copy(showResetConfirmationDialog = false) }
            CounterUiEvent.ConfirmReset -> handleReset()
            is CounterUiEvent.UpdateTargetLimit -> handleTargetLimitChange(event.newTarget)
            is CounterUiEvent.SetTheme -> _uiState.update { it.copy(selectedTheme = event.theme) }
            CounterUiEvent.ToggleTimerState -> toggleTimer()
            CounterUiEvent.OpenTargetDialog -> _uiState.update { it.copy(showTargetInputDialog = true) }
            CounterUiEvent.DismissTargetDialog -> _uiState.update { it.copy(showTargetInputDialog = false) }
        }
    }

    private fun handleIncrement() {
        val currentState = _uiState.value
        val nextCount = currentState.currentCount + 1
        val isRoundComplete = nextCount >= currentState.targetCount

        val finalCount = if (isRoundComplete) 0 else nextCount
        val finalRounds = if (isRoundComplete) currentState.roundsCount + 1 else currentState.roundsCount

        if (isRoundComplete) {
            if (currentState.isHapticEnabled) hapticEngine.performRoundCompleteFeedback(currentState.hapticIntensity)
            if (currentState.isSoundEnabled) soundEngine.playRoundCompletionChime()
        } else {
            if (currentState.isHapticEnabled) hapticEngine.performClickFeedback(currentState.hapticIntensity)
            if (currentState.isSoundEnabled) soundEngine.playClickSound()
        }

        viewModelScope.launch {
            val currentEntity = counterDao.getCounterById(currentState.counterId) ?: return@launch
            val updatedEntity = currentEntity.copy(
                currentCount = finalCount,
                totalRounds = finalRounds,
                lastModifiedTimestamp = System.currentTimeMillis(),
                syncStatus = SyncStatus.PENDING_SYNC
            )
            counterDao.updateCounter(updatedEntity)
            recordHistoryLog(currentState.counterId, 1)
        }
    }

    private fun handleDecrement() {
        val currentState = _uiState.value
        if (currentState.currentCount <= 0) return

        val newCount = currentState.currentCount - 1
        if (currentState.isHapticEnabled) hapticEngine.performClickFeedback(currentState.hapticIntensity * 0.7f)

        viewModelScope.launch {
            val currentEntity = counterDao.getCounterById(currentState.counterId) ?: return@launch
            counterDao.updateCounter(
                currentEntity.copy(
                    currentCount = newCount,
                    lastModifiedTimestamp = System.currentTimeMillis(),
                    syncStatus = SyncStatus.PENDING_SYNC
                )
            )
        }
    }

    private fun handleReset() {
        val currentState = _uiState.value
        _uiState.update { it.copy(showResetConfirmationDialog = false) }
        
        viewModelScope.launch {
            val currentEntity = counterDao.getCounterById(currentState.counterId) ?: return@launch
            counterDao.updateCounter(
                currentEntity.copy(
                    currentCount = 0,
                    totalRounds = 0,
                    lastModifiedTimestamp = System.currentTimeMillis(),
                    syncStatus = SyncStatus.PENDING_SYNC
                )
            )
        }
    }

    private fun handleTargetLimitChange(newTarget: Int) {
        if (newTarget <= 0) return
        _uiState.update { it.copy(showTargetInputDialog = false) }

        viewModelScope.launch {
            val currentEntity = counterDao.getCounterById(_uiState.value.counterId) ?: return@launch
            counterDao.updateCounter(
                currentEntity.copy(
                    targetLimit = newTarget,
                    lastModifiedTimestamp = System.currentTimeMillis(),
                    syncStatus = SyncStatus.PENDING_SYNC
                )
            )
        }
    }

    private suspend fun recordHistoryLog(counterId: String, incrementDelta: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(Date())
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val existingLog = historyLogDao.getHourlyLog(counterId, dateString, hour)

        if (existingLog != null) {
            historyLogDao.upsertLog(
                existingLog.copy(
                    aggregatedCount = existingLog.aggregatedCount + incrementDelta,
                    lastModifiedTimestamp = System.currentTimeMillis()
                )
            )
        } else {
            historyLogDao.upsertLog(
                HistoryLogEntity(
                    counterId = counterId,
                    dateString = dateString,
                    hourOfDay = hour,
                    aggregatedCount = incrementDelta
                )
            )
        }
    }

    private fun startSessionTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(isSessionTimerRunning = true) }
            while (isActive) {
                delay(1000L)
                _uiState.update { it.copy(elapsedSessionSeconds = it.elapsedSessionSeconds + 1) }
            }
        }
    }

    private fun toggleTimer() {
        if (_uiState.value.isSessionTimerRunning) {
            timerJob?.cancel()
            _uiState.update { it.copy(isSessionTimerRunning = false) }
        } else {
            startSessionTimer()
        }
    }

    fun onLifecyclePause() {
        timerJob?.cancel()
        _uiState.update { it.copy(isSessionTimerRunning = false) }
    }

    fun onLifecycleResume() {
        if (!_uiState.value.isSessionTimerRunning) {
            startSessionTimer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        soundEngine.release()
    }
}
