package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.AuthorizationException
import com.smartkdfarm.app.core.domain.exception.NotFoundException
import com.smartkdfarm.app.core.domain.exception.ValidationException
import com.smartkdfarm.app.core.domain.model.FeedBatchLog
import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.InventoryItem
import com.smartkdfarm.app.core.domain.model.KhataEntryKind
import com.smartkdfarm.app.core.domain.model.KhataLedgerEntry
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.repository.FinanceRepository
import com.smartkdfarm.app.core.domain.repository.InventoryRepository

class ProduceFeedBatchUseCase(
    private val inventoryRepository: InventoryRepository,
    private val financeRepository: FinanceRepository,
) {
    suspend operator fun invoke(
        actor: User,
        batch: FeedBatchLog,
    ): FeedBatchLog {
        if (actor.role != UserRole.ADMIN && actor.role != UserRole.LABOUR) {
            throw AuthorizationException("Only ADMIN or LABOUR can produce a feed batch.")
        }
        if (batch.farmId != actor.farmId) {
            throw AuthorizationException("Feed batch can only be produced inside the actor farm.")
        }
        if (batch.batchName.isBlank()) {
            throw ValidationException("Batch name is required.")
        }
        if (batch.totalOutputKg <= 0.0) {
            throw ValidationException("Batch output must be greater than zero.")
        }
        if (batch.ingredients.isEmpty()) {
            throw ValidationException("At least one ingredient is required.")
        }

        val inventory = inventoryRepository.getInventoryItems(actor.farmId)
        var totalExpense = 0.0
        batch.ingredients.forEach { ingredient ->
            val item = inventory.firstOrNull { it.id == ingredient.inventoryItemId }
                ?: throw NotFoundException("Inventory item not found: ${ingredient.itemName}")
            if (ingredient.quantityUsed > item.currentStock) {
                throw ValidationException("Insufficient stock for ${ingredient.itemName}.")
            }
            val updated = item.copy(
                currentStock = item.currentStock - ingredient.quantityUsed,
                updatedAtEpochMillis = TimeProvider.nowEpochMillis(),
            )
            inventoryRepository.upsertInventoryItem(updated)
            totalExpense += ingredient.quantityUsed
        }

        val now = TimeProvider.nowEpochMillis()
        val saved = inventoryRepository.saveFeedBatch(
            batch.copy(
                id = batch.id.ifBlank { IdGenerator.newId(prefix = "feed_batch") },
                createdByUserId = actor.id,
                createdAtEpochMillis = now,
                autoStockDeducted = true,
            )
        )
        financeRepository.recordLedgerEntry(
            KhataLedgerEntry(
                id = IdGenerator.newId(prefix = "ledger"),
                farmId = actor.farmId,
                kind = KhataEntryKind.EXPENSE,
                title = "Feed batch: ${saved.batchName}",
                amount = totalExpense,
                sourceModule = "Inventory",
                linkedRecordId = saved.id,
                occurredAtEpochMillis = saved.producedAtEpochMillis,
                createdByUserId = actor.id,
                notes = "Auto-posted from batch feed production",
            )
        )
        return saved
    }
}
