package com.tasbeeh.digital.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CounterWithHistory(
    @Embedded val counter: CounterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "counter_id"
    )
    val historyLogs: List<HistoryLogEntity>
)
