package com.tasbeeh.digital.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tasbeeh.digital.data.local.database.SyncStatus
import java.util.UUID

@Entity(
    tableName = "counters_table",
    indices = [Index(value = ["is_active"]), Index(value = ["last_modified_timestamp"])]
)
data class CounterEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "current_count")
    val currentCount: Int = 0,

    @ColumnInfo(name = "target_limit")
    val targetLimit: Int = 33,

    @ColumnInfo(name = "total_rounds")
    val totalRounds: Int = 0,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = false,

    @ColumnInfo(name = "last_modified_timestamp")
    val lastModifiedTimestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.PENDING_SYNC
)
