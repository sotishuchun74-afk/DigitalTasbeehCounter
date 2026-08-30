package com.tasbeeh.digital.core.hardware.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface HapticEngine {
    fun performClickFeedback(intensityFraction: Float = 0.5f)
    fun performRoundCompleteFeedback(intensityFraction: Float = 1.0f)
}

@Singleton
class HapticEngineImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : HapticEngine {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    override fun performClickFeedback(intensityFraction: Float) {
        val clampedIntensity = intensityFraction.coerceIn(0.1f, 1.0f)
        if (vibrator == null || !vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitude = (clampedIntensity * 255).toInt().coerceIn(1, 255)
            val durationMs = (15 + (clampedIntensity * 25)).toLong()
            val effect = VibrationEffect.createOneShot(durationMs, amplitude)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(25L)
        }
    }

    override fun performRoundCompleteFeedback(intensityFraction: Float) {
        if (vibrator == null || !vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitude = (intensityFraction.coerceIn(0.2f, 1.0f) * 255).toInt().coerceIn(1, 255)
            val timings = longArrayOf(0, 80, 50, 120, 50, 180)
            val amplitudes = intArrayOf(0, amplitude / 2, 0, (amplitude * 0.75f).toInt(), 0, amplitude)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            val pattern = longArrayOf(0, 100, 70, 150)
            vibrator.vibrate(pattern, -1)
        }
    }
}
