package com.tasbeeh.digital.presentation.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tasbeeh.digital.R
import com.tasbeeh.digital.domain.model.CounterThemeType
import com.tasbeeh.digital.presentation.themes.skins.ClassicLcdCounter
import com.tasbeeh.digital.presentation.themes.skins.MinimalRingCounter
import com.tasbeeh.digital.presentation.themes.skins.MisbahaBeadsSkin
import com.tasbeeh.digital.presentation.themes.skins.TallyCounterSkin

@Composable
fun CounterScreen(
    state: CounterUiState,
    onEvent: (CounterUiEvent) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showThemeMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Active Skin Background & Canvas
        when (state.selectedTheme) {
            CounterThemeType.MINIMAL_RING -> MinimalRingCounter(
                count = state.currentCount,
                target = state.targetCount,
                progress = state.progress,
                onTap = { onEvent(CounterUiEvent.OnTapIncrement) }
            )
            CounterThemeType.CLASSIC_LCD -> ClassicLcdCounter(
                count = state.currentCount,
                rounds = state.roundsCount,
                onTap = { onEvent(CounterUiEvent.OnTapIncrement) }
            )
            CounterThemeType.TALLY_COUNTER -> TallyCounterSkin(
                count = state.currentCount,
                onTap = { onEvent(CounterUiEvent.OnTapIncrement) }
            )
            CounterThemeType.MISBAHA_BEADS -> MisbahaBeadsSkin(
                count = state.currentCount,
                target = state.targetCount,
                rounds = state.roundsCount,
                onTap = { onEvent(CounterUiEvent.OnTapIncrement) }
            )
        }

        // Overlay HUD Elements
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top HUD Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.showCounterName) {
                    Text(
                        text = state.counterName,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Target Switcher
                    Text(
                        text = "Goal: ",
                        color = Color(0xFFF5A623),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0x33000000), RoundedCornerShape(8.dp))
                            .clickable { onEvent(CounterUiEvent.OpenTargetDialog) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )

                    IconButton(onClick = { showThemeMenu = true }) {
                        Icon(Icons.Default.Palette, contentDescription = "Themes", tint = Color.White)
                    }

                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            }

            // Bottom Floating Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.showSessionTimer) {
                    val minutes = state.elapsedSessionSeconds / 60
                    val seconds = state.elapsedSessionSeconds % 60
                    val timeStr = String.format("%02d:%02d", minutes, seconds)
                    Text(
                        text = "Session: ",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onEvent(CounterUiEvent.ToggleTimerState) }
                    )
                }

                IconButton(
                    onClick = { onEvent(CounterUiEvent.RequestResetConfirmation) },
                    modifier = Modifier.background(Color(0x33000000), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                }
            }
        }

        // Theme Selector Dialog
        if (showThemeMenu) {
            AlertDialog(
                onDismissRequest = { showThemeMenu = false },
                title = { Text("Select Counter Theme") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CounterThemeType.entries.forEach { theme ->
                            Button(
                                onClick = {
                                    onEvent(CounterUiEvent.SetTheme(theme))
                                    showThemeMenu = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(theme.name.replace("_", " "))
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeMenu = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        // Reset Confirmation Dialog
        if (state.showResetConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { onEvent(CounterUiEvent.DismissResetConfirmation) },
                title = { Text(stringResource(R.string.reset_confirm_title)) },
                text = { Text(stringResource(R.string.reset_confirm_message)) },
                confirmButton = {
                    TextButton(onClick = { onEvent(CounterUiEvent.ConfirmReset) }) {
                        Text(stringResource(R.string.reset), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(CounterUiEvent.DismissResetConfirmation) }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        // Target Input Dialog
        if (state.showTargetInputDialog) {
            var targetInput by remember { mutableStateOf(state.targetCount.toString()) }
            AlertDialog(
                onDismissRequest = { onEvent(CounterUiEvent.DismissTargetDialog) },
                title = { Text(stringResource(R.string.target_goal)) },
                text = {
                    OutlinedTextField(
                        value = targetInput,
                        onValueChange = { targetInput = it.filter { c -> c.isDigit() } },
                        label = { Text(stringResource(R.string.enter_target)) },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val num = targetInput.toIntOrNull() ?: 33
                        onEvent(CounterUiEvent.UpdateTargetLimit(num))
                    }) {
                        Text(stringResource(R.string.save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(CounterUiEvent.DismissTargetDialog) }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}
