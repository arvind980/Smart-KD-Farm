package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import com.smartkdfarm.app.core.domain.model.FodderYieldLog
import com.smartkdfarm.app.core.domain.repository.FodderRepository

class FirebaseFodderRepository : FodderRepository {
    private val firestore = Firebase.firestore
    private val cache = MutableStateFlow<Map<String, List<FodderYieldLog>>>(emptyMap())

    override fun observeFodderYields(farmId: String): Flow<List<FodderYieldLog>> =
        cache.map { it[farmId].orEmpty() }

    override suspend fun getFodderYields(farmId: String): List<FodderYieldLog> {
        val snapshot = yieldsCollection(farmId).get()
        val logs = snapshot.documents
            .mapNotNull { runCatching { it.data<FodderYieldLogDocument>() }.getOrNull() }
            .map(FodderYieldLogDocument::toDomain)
            .sortedByDescending { it.harvestDateEpochMillis }
        cache.update { it + (farmId to logs) }
        return logs
    }

    override suspend fun saveFodderYield(log: FodderYieldLog): FodderYieldLog {
        yieldsCollection(log.farmId).document(log.id).set(log.toDocument())
        cache.update { current ->
            val updated = current[log.farmId].orEmpty().filterNot { it.id == log.id } + log
            current + (log.farmId to updated.sortedByDescending { it.harvestDateEpochMillis })
        }
        return log
    }

    override suspend fun deleteFodderYield(farmId: String, logId: String) {
        yieldsCollection(farmId).document(logId).delete()
        cache.update { current ->
            val updated = current[farmId].orEmpty().filterNot { it.id == logId }
            current + (farmId to updated)
        }
    }

    private fun yieldsCollection(farmId: String): CollectionReference =
        firestore
            .collection(FARMS_COLLECTION)
            .document(farmId)
            .collection(FODDER_COLLECTION)
            .document(FODDER_DOCUMENT)
            .collection(YIELDS_COLLECTION)

    private companion object {
        const val FARMS_COLLECTION  = "farms"
        const val FODDER_COLLECTION = "fodder"
        const val FODDER_DOCUMENT   = "operations"
        const val YIELDS_COLLECTION = "yields"
    }
}
