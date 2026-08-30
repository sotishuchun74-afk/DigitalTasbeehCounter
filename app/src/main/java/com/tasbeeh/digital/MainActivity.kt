package com.tasbeeh.digital

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tasbeeh.digital.core.hardware.keyevent.VolumeKeyHandler
import com.tasbeeh.digital.presentation.counter.CounterViewModel
import com.tasbeeh.digital.presentation.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var volumeKeyHandler: VolumeKeyHandler

    private val counterViewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavGraph(counterViewModel = counterViewModel)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            val consumed = volumeKeyHandler.onKeyEvent(
                event = event,
                isHardwareCaptureEnabled = counterViewModel.uiState.value.isHardwareVolumeKeysEnabled
            )
            if (consumed) return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        super.onPause()
        counterViewModel.onLifecyclePause()
    }

    override fun onResume() {
        super.onResume()
        counterViewModel.onLifecycleResume()
    }
}
