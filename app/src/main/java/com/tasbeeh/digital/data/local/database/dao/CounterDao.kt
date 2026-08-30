package com.tasbeeh.digital.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tasbeeh.digital.data.local.database.SyncStatus
import com.tasbeeh.digital.data.local.entities.CounterEntity
import com.tasbeeh.digital.data.local.entities.CounterWithHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {
    @Query("SELECT * FROM counters_table WHERE is_active = 1 LIMIT 1")
    fun observeActiveCounter(): Flow<CounterEntity?>

    @Query("SELECT * FROM counters_table WHERE is_active = 1 LIMIT 1")
    suspend fun getActiveCounter(): CounterEntity?

    @Query("SELECT * FROM counters_table WHERE id = :id")
    suspend fun getCounterById(id: String): CounterEntity?

    @Query("SELECT * FROM counters_table ORDER BY last_modified_timestamp DESC")
    fun observeAllCounters(): Flow<List<CounterEntity>>

    @Query("SELECT * FROM counters_table WHERE sync_status = :status")
    suspend fun getPendingSyncCounters(status: SyncStatus = SyncStatus.PENDING_SYNC): List<CounterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCounter(counter: CounterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCounters(counters: List<CounterEntity>)

    @Update
    suspend fun updateCounter(counter: CounterEntity)

    @Query("UPDATE counters_table SET is_active = 0 WHERE id != :activeId")
    suspend fun deactivateOthers(activeId: String)

    @Transaction
    suspend fun setActiveCounter(counterId: String) {
        val target = getCounterById(counterId) ?: return
        deactivateOthers(counterId)
        upsertCounter(target.copy(isActive = true, lastModifiedTimestamp = System.currentTimeMillis()))
    }

    @Transaction
    @Query("SELECT * FROM counters_table WHERE id = :counterId")
    fun observeCounterWithHistory(counterId: String): Flow<CounterWithHistory?>

    @Query("DELETE FROM counters_table WHERE id = :counterId")
    suspend fun deleteCounterById(counterId: String)
}
