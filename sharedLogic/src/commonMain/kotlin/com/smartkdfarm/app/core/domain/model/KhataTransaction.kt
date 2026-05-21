package com.smartkdfarm.app.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class KhataDirection {
    OUTGOING,
}

@Serializable
enum class KhataTransactionCategory {
    LIVESTOCK_SALE,
}

@Serializable
data class KhataTransaction(
    val id: String,
    val farmId: String,
    val animalId: String,
    val animalTagNumber: String,
    val direction: KhataDirection,
    val category: KhataTransactionCategory,
    val amount: Double,
    val notes: String,
    val occurredAtEpochMillis: Long,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)
