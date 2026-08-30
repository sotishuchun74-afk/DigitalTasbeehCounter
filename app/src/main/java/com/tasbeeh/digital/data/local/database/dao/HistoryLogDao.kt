package com.tasbeeh.digital.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tasbeeh.digital.data.local.entities.HistoryLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryLogDao {
    @Query("SELECT * FROM history_logs_table WHERE counter_id = :counterId AND date_string = :dateString AND hour_of_day = :hour LIMIT 1")
    suspend fun getHourlyLog(counterId: String, dateString: String, hour: Int): HistoryLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLog(log: HistoryLogEntity)

    @Query("SELECT * FROM history_logs_table WHERE counter_id = :counterId ORDER BY date_string DESC, hour_of_day DESC")
    fun observeLogsForCounter(counterId: String): Flow<List<HistoryLogEntity>>

    @Query("SELECT SUM(aggregated_count) FROM history_logs_table WHERE date_string = :dateString")
    fun observeDailyTotal(dateString: String): Flow<Int?>
}
