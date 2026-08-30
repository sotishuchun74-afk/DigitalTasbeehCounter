package com.tasbeeh.digital.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "history_logs_table",
    foreignKeys = [
        ForeignKey(
            entity = CounterEntity::class,
            parentColumns = ["id"],
            childColumns = ["counter_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["counter_id"]), Index(value = ["date_string"])]
)
data class HistoryLogEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "counter_id")
    val counterId: String,

    @ColumnInfo(name = "date_string")
    val dateString: String,

    @ColumnInfo(name = "hour_of_day")
    val hourOfDay: Int,

    @ColumnInfo(name = "aggregated_count")
    val aggregatedCount: Int,

    @ColumnInfo(name = "last_modified_timestamp")
    val lastModifiedTimestamp: Long = System.currentTimeMillis()
)
