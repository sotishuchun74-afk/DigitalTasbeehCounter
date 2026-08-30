package com.tasbeeh.digital.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tasbeeh.digital.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userEmail: String? = "user@example.com",
    lastSyncedDate: String = "2026-08-30 10:00:00",
    onBackupNow: () -> Unit = {},
    onRestoreRecords: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var isSoundClickEnabled by remember { mutableStateOf(true) }
    var isChimeEnabled by remember { mutableStateOf(true) }
    var isHapticEnabled by remember { mutableStateOf(true) }
    var hapticStrength by remember { mutableFloatStateOf(0.6f) }
    var isHardwareKeysEnabled by remember { mutableStateOf(true) }
    var isTimerVisible by remember { mutableStateOf(true) }
    var isRoundsVisible by remember { mutableStateOf(true) }
    var isDailyReminderEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp).clip(CircleShape),
                            tint = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(userEmail ?: "Signed In as Guest", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                stringResource(R.string.last_synced, lastSyncedDate),
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = onBackupNow,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.backup_now))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = onRestoreRecords,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.restore_records))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.controls_tuning), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            SettingRow(stringResource(R.string.sound_click), isSoundClickEnabled) { isSoundClickEnabled = it }
            SettingRow(stringResource(R.string.sound_chime), isChimeEnabled) { isChimeEnabled = it }
            SettingRow(stringResource(R.string.haptic_click), isHapticEnabled) { isHapticEnabled = it }

            if (isHapticEnabled) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(stringResource(R.string.haptic_strength), fontSize = 14.sp)
                    Slider(
                        value = hapticStrength,
                        onValueChange = { hapticStrength = it },
                        valueRange = 0.1f..1.0f
                    )
                }
            }

            SettingRow(stringResource(R.string.hardware_keys), isHardwareKeysEnabled) { isHardwareKeysEnabled = it }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.display_hud), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            SettingRow(stringResource(R.string.show_timer), isTimerVisible) { isTimerVisible = it }
            SettingRow(stringResource(R.string.show_rounds), isRoundsVisible) { isRoundsVisible = it }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.daily_reminder), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            SettingRow(stringResource(R.string.reminder_time, "06:00 AM"), isDailyReminderEnabled) { isDailyReminderEnabled = it }
        }
    }
}

@Composable
fun SettingRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, modifier = Modifier.weight(1f), fontSize = 15.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
