package com.tasbeeh.digital.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tasbeeh.digital.data.local.database.dao.CounterDao
import com.tasbeeh.digital.data.local.database.dao.HistoryLogDao
import com.tasbeeh.digital.data.local.entities.CounterEntity
import com.tasbeeh.digital.data.local.entities.HistoryLogEntity

@Database(
    entities = [CounterEntity::class, HistoryLogEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TasbeehDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
    abstract fun historyLogDao(): HistoryLogDao

    companion object {
        const val DATABASE_NAME = "tasbeeh_master.db"
    }
}
