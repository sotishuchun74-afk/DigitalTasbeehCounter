package com.tasbeeh.digital.data.remote.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tasbeeh.digital.data.local.database.SyncStatus
import com.tasbeeh.digital.data.local.database.dao.CounterDao
import com.tasbeeh.digital.data.local.entities.CounterEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncEngine @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val counterDao: CounterDao
) {

    suspend fun synchronize(): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("User not authenticated.")
        val userCountersRef = firestore.collection("users").document(user.uid).collection("counters")

        val remoteSnapshot = userCountersRef.get().await()
        val remoteCounters = remoteSnapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            CounterEntity(
                id = doc.id,
                name = data["name"] as? String ?: "Tasbeeh",
                currentCount = (data["current_count"] as? Long)?.toInt() ?: 0,
                targetLimit = (data["target_limit"] as? Long)?.toInt() ?: 33,
                totalRounds = (data["total_rounds"] as? Long)?.toInt() ?: 0,
                isActive = data["is_active"] as? Boolean ?: false,
                lastModifiedTimestamp = data["last_modified_timestamp"] as? Long ?: 0L,
                syncStatus = SyncStatus.SYNCED
            )
        }

        val localPending = counterDao.getPendingSyncCounters()
        val localMap = localPending.associateBy { it.id }.toMutableMap()

        for (remote in remoteCounters) {
            val local = counterDao.getCounterById(remote.id)
            if (local == null) {
                counterDao.upsertCounter(remote)
            } else {
                if (remote.lastModifiedTimestamp > local.lastModifiedTimestamp) {
                    counterDao.upsertCounter(remote)
                } else if (local.lastModifiedTimestamp > remote.lastModifiedTimestamp) {
                    val uploadMap = hashMapOf(
                        "name" to local.name,
                        "current_count" to local.currentCount,
                        "target_limit" to local.targetLimit,
                        "total_rounds" to local.totalRounds,
                        "is_active" to local.isActive,
                        "last_modified_timestamp" to local.lastModifiedTimestamp
                    )
                    userCountersRef.document(local.id).set(uploadMap, SetOptions.merge()).await()
                    counterDao.updateCounter(local.copy(syncStatus = SyncStatus.SYNCED))
                } else {
                    if (local.syncStatus != SyncStatus.SYNCED) {
                        counterDao.updateCounter(local.copy(syncStatus = SyncStatus.SYNCED))
                    }
                }
                localMap.remove(remote.id)
            }
        }

        for ((_, newLocal) in localMap) {
            val uploadMap = hashMapOf(
                "name" to newLocal.name,
                "current_count" to newLocal.currentCount,
                "target_limit" to newLocal.targetLimit,
                "total_rounds" to newLocal.totalRounds,
                "is_active" to newLocal.isActive,
                "last_modified_timestamp" to newLocal.lastModifiedTimestamp
            )
            userCountersRef.document(newLocal.id).set(uploadMap, SetOptions.merge()).await()
            counterDao.updateCounter(newLocal.copy(syncStatus = SyncStatus.SYNCED))
        }
    }
}
