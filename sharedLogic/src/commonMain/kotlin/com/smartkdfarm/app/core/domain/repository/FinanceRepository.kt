package com.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.smartkdfarm.app.core.domain.model.KhataLedgerEntry
import com.smartkdfarm.app.core.domain.model.KhataTransaction

interface FinanceRepository {
    fun observeLedgerEntries(farmId: String): Flow<List<KhataLedgerEntry>>

    suspend fun getLedgerEntries(farmId: String): List<KhataLedgerEntry>

    suspend fun recordLedgerEntry(entry: KhataLedgerEntry)

    suspend fun recordOutgoingTransaction(transaction: KhataTransaction)
}
