package com.tasbeeh.digital.di

import com.tasbeeh.digital.core.hardware.audio.SoundEngine
import com.tasbeeh.digital.core.hardware.audio.SoundEngineImpl
import com.tasbeeh.digital.core.hardware.haptics.HapticEngine
import com.tasbeeh.digital.core.hardware.haptics.HapticEngineImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindHapticEngine(impl: HapticEngineImpl): HapticEngine

    @Binds
    @Singleton
    abstract fun bindSoundEngine(impl: SoundEngineImpl): SoundEngine
}
