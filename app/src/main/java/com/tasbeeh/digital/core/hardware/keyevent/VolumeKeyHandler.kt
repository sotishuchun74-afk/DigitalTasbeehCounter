package com.tasbeeh.digital.core.hardware.keyevent

import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class VolumeKeyEventType {
    INCREMENT,
    DECREMENT
}

@Singleton
class VolumeKeyHandler @Inject constructor() {
    private val _keyEvents = MutableSharedFlow<VolumeKeyEventType>(extraBufferCapacity = 1)
    val keyEvents: SharedFlow<VolumeKeyEventType> = _keyEvents.asSharedFlow()

    fun onKeyEvent(event: KeyEvent, isHardwareCaptureEnabled: Boolean): Boolean {
        if (!isHardwareCaptureEnabled) return false

        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    _keyEvents.tryEmit(VolumeKeyEventType.INCREMENT)
                    return true
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    _keyEvents.tryEmit(VolumeKeyEventType.DECREMENT)
                    return true
                }
            }
        }
        return false
    }
}
