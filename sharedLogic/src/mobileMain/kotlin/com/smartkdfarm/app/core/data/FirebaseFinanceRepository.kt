package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import com.smartkdfarm.app.core.domain.model.KhataTransaction
import com.smartkdfarm.app.core.domain.repository.FinanceRepository

class FirebaseFinanceRepository : FinanceRepository {
    private val firestore = Firebase.firestore

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
