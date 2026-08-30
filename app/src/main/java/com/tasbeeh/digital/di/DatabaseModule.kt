package com.tasbeeh.digital.di

import android.content.Context
import androidx.room.Room
import com.tasbeeh.digital.data.local.database.TasbeehDatabase
import com.tasbeeh.digital.data.local.database.dao.CounterDao
import com.tasbeeh.digital.data.local.database.dao.HistoryLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TasbeehDatabase {
        return Room.databaseBuilder(
            context,
            TasbeehDatabase::class.java,
            TasbeehDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCounterDao(db: TasbeehDatabase): CounterDao = db.counterDao()

    @Provides
    fun provideHistoryLogDao(db: TasbeehDatabase): HistoryLogDao = db.historyLogDao()
}
