package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import com.smartkdfarm.app.core.domain.model.KhataLedgerEntry
import com.smartkdfarm.app.core.domain.model.KhataTransaction
import com.smartkdfarm.app.core.domain.repository.FinanceRepository

class FirebaseFinanceRepository : FinanceRepository {
    private val firestore = Firebase.firestore
    private val ledgerCache = MutableStateFlow<Map<String, List<KhataLedgerEntry>>>(emptyMap())

    override fun observeLedgerEntries(farmId: String): Flow<List<KhataLedgerEntry>> =
        ledgerCache.map { it[farmId].orEmpty() }

    override suspend fun getLedgerEntries(farmId: String): List<KhataLedgerEntry> {
        val snapshot = transactionsCollection(farmId).get()
        val entries = snapshot.documents.mapNotNull { document ->
            runCatching { document.data<KhataLedgerEntryDocument>() }.getOrNull()
        }.map {
            KhataLedgerEntry(
                id = it.id,
                farmId = it.farmId,
                kind = it.kind,
                title = it.title,
                amount = it.amount,
                sourceModule = it.sourceModule,
                linkedRecordId = it.linkedRecordId,
                occurredAtEpochMillis = it.occurredAtEpochMillis,
                createdByUserId = it.createdByUserId,
                notes = it.notes,
                createdAtEpochMillis = it.createdAtEpochMillis,
            )
        }.sortedByDescending { it.occurredAtEpochMillis }
        ledgerCache.update { it + (farmId to entries) }
        return entries
    }

    override suspend fun recordLedgerEntry(entry: KhataLedgerEntry) {
        transactionsCollection(entry.farmId)
            .document(entry.id)
            .set(entry.toDocument())
        ledgerCache.update { current ->
            val updated = current[entry.farmId].orEmpty().filterNot { it.id == entry.id } + entry
            current + (entry.farmId to updated.sortedByDescending { it.occurredAtEpochMillis })
        }
    }

    override suspend fun recordOutgoingTransaction(transaction: KhataTransaction) {
        transactionsCollection(transaction.farmId)
            .document(transaction.id)
            .set(transaction.toDocument())
    }

    private fun transactionsCollection(farmId: String): CollectionReference =
        firestore.collection(FARMS_COLLECTION)
            .document(farmId)
            .collection(KHATA_COLLECTION)
            .document(TRANSACTIONS_DOCUMENT)
            .collection(TRANSACTIONS_COLLECTION)

    private companion object {
        const val FARMS_COLLECTION = "farms"
        const val KHATA_COLLECTION = "khata"
        const val TRANSACTIONS_DOCUMENT = "finance"
        const val TRANSACTIONS_COLLECTION = "transactions"
    }
}
