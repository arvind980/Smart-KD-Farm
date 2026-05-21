package com.smartkdfarm.app.core.domain.repository

import com.smartkdfarm.app.core.domain.model.KhataTransaction

interface FinanceRepository {
    suspend fun recordOutgoingTransaction(transaction: KhataTransaction)
}
