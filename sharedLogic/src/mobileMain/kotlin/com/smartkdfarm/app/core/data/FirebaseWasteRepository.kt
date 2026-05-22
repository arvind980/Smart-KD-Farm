package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import com.smartkdfarm.app.core.domain.model.WasteLog
import com.smartkdfarm.app.core.domain.repository.WasteRepository

class FirebaseWasteRepository : WasteRepository {
    private val firestore = Firebase.firestore
    private val cache = MutableStateFlow<Map<String, List<WasteLog>>>(emptyMap())

    override fun observeWasteLogs(farmId: String): Flow<List<WasteLog>> =
        cache.map { it[farmId].orEmpty() }

    override suspend fun getWasteLogs(farmId: String): List<WasteLog> {
        val snapshot = logsCollection(farmId).get()
        val logs = snapshot.documents
            .mapNotNull { runCatching { it.data<WasteLogDocument>() }.getOrNull() }
            .map(WasteLogDocument::toDomain)
            .sortedByDescending { it.recordedAtEpochMillis }
        cache.update { it + (farmId to logs) }
        return logs
    }

    override suspend fun saveWasteLog(log: WasteLog): WasteLog {
        logsCollection(log.farmId).document(log.id).set(log.toDocument())
        cache.update { current ->
            val updated = current[log.farmId].orEmpty().filterNot { it.id == log.id } + log
            current + (log.farmId to updated.sortedByDescending { it.recordedAtEpochMillis })
        }
        return log
    }

    override suspend fun deleteWasteLog(farmId: String, logId: String) {
        logsCollection(farmId).document(logId).delete()
        cache.update { current ->
            val updated = current[farmId].orEmpty().filterNot { it.id == logId }
            current + (farmId to updated)
        }
    }

    private fun logsCollection(farmId: String): CollectionReference =
        firestore
            .collection(FARMS_COLLECTION)
            .document(farmId)
            .collection(WASTE_COLLECTION)
            .document(WASTE_DOCUMENT)
            .collection(LOGS_COLLECTION)

    private companion object {
        const val FARMS_COLLECTION  = "farms"
        const val WASTE_COLLECTION  = "waste"
        const val WASTE_DOCUMENT    = "operations"
        const val LOGS_COLLECTION   = "logs"
    }
}
