package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import com.smartkdfarm.app.core.domain.model.FeedBatchLog
import com.smartkdfarm.app.core.domain.model.FeedFormulaIngredient
import com.smartkdfarm.app.core.domain.model.InventoryItem
import com.smartkdfarm.app.core.domain.repository.InventoryRepository

class FirebaseInventoryRepository : InventoryRepository {
    private val firestore = Firebase.firestore
    private val itemsCache = MutableStateFlow<Map<String, List<InventoryItem>>>(emptyMap())
    private val batchesCache = MutableStateFlow<Map<String, List<FeedBatchLog>>>(emptyMap())

    override fun observeInventoryItems(farmId: String): Flow<List<InventoryItem>> =
        itemsCache.map { it[farmId].orEmpty() }

    override suspend fun getInventoryItems(farmId: String): List<InventoryItem> {
        val snapshot = itemsCollection(farmId).get()
        val items = snapshot.documents.mapNotNull { runCatching { it.data<InventoryItemDocument>() }.getOrNull() }
            .map {
                InventoryItem(
                    id = it.id,
                    farmId = it.farmId,
                    name = it.name,
                    category = it.category,
                    unit = it.unit,
                    currentStock = it.currentStock,
                    reorderLevel = it.reorderLevel,
                    vendorName = it.vendorName,
                    notes = it.notes,
                    updatedAtEpochMillis = it.updatedAtEpochMillis,
                )
            }.sortedBy { it.name }
        itemsCache.update { it + (farmId to items) }
        return items
    }

    override suspend fun upsertInventoryItem(item: InventoryItem): InventoryItem {
        itemsCollection(item.farmId).document(item.id).set(item.toDocument())
        itemsCache.update { current ->
            val updated = current[item.farmId].orEmpty().filterNot { it.id == item.id } + item
            current + (item.farmId to updated.sortedBy { it.name })
        }
        return item
    }

    override fun observeFeedBatches(farmId: String): Flow<List<FeedBatchLog>> =
        batchesCache.map { it[farmId].orEmpty() }

    override suspend fun getFeedBatches(farmId: String): List<FeedBatchLog> {
        val snapshot = feedBatchesCollection(farmId).get()
        val batches = snapshot.documents.mapNotNull { runCatching { it.data<FeedBatchLogDocument>() }.getOrNull() }
            .map {
                FeedBatchLog(
                    id = it.id,
                    farmId = it.farmId,
                    batchName = it.batchName,
                    producedAtEpochMillis = it.producedAtEpochMillis,
                    totalOutputKg = it.totalOutputKg,
                    ingredients = it.ingredients.map { ingredient ->
                        FeedFormulaIngredient(
                            inventoryItemId = ingredient.inventoryItemId,
                            itemName = ingredient.itemName,
                            quantityUsed = ingredient.quantityUsed,
                            unit = ingredient.unit,
                        )
                    },
                    autoStockDeducted = it.autoStockDeducted,
                    notes = it.notes,
                    createdByUserId = it.createdByUserId,
                    createdAtEpochMillis = it.createdAtEpochMillis,
                )
            }.sortedByDescending { it.producedAtEpochMillis }
        batchesCache.update { it + (farmId to batches) }
        return batches
    }

    override suspend fun saveFeedBatch(batch: FeedBatchLog): FeedBatchLog {
        feedBatchesCollection(batch.farmId).document(batch.id).set(batch.toDocument())
        batchesCache.update { current ->
            val updated = current[batch.farmId].orEmpty().filterNot { it.id == batch.id } + batch
            current + (batch.farmId to updated.sortedByDescending { it.producedAtEpochMillis })
        }
        return batch
    }

    private fun inventoryDocument(farmId: String) =
        firestore.collection(FARMS_COLLECTION).document(farmId).collection(INVENTORY_COLLECTION).document(INVENTORY_DOCUMENT)

    private fun itemsCollection(farmId: String): CollectionReference =
        inventoryDocument(farmId).collection(ITEMS_COLLECTION)

    private fun feedBatchesCollection(farmId: String): CollectionReference =
        inventoryDocument(farmId).collection(FEED_BATCHES_COLLECTION)

    private companion object {
        const val FARMS_COLLECTION = "farms"
        const val INVENTORY_COLLECTION = "inventory"
        const val INVENTORY_DOCUMENT = "operations"
        const val ITEMS_COLLECTION = "items"
        const val FEED_BATCHES_COLLECTION = "feed_batches"
    }
}
