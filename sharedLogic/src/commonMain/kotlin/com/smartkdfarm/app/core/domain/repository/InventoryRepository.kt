package com.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.smartkdfarm.app.core.domain.model.FeedBatchLog
import com.smartkdfarm.app.core.domain.model.InventoryItem

interface InventoryRepository {
    fun observeInventoryItems(farmId: String): Flow<List<InventoryItem>>

    suspend fun getInventoryItems(farmId: String): List<InventoryItem>

    suspend fun upsertInventoryItem(item: InventoryItem): InventoryItem

    fun observeFeedBatches(farmId: String): Flow<List<FeedBatchLog>>

    suspend fun getFeedBatches(farmId: String): List<FeedBatchLog>

    suspend fun saveFeedBatch(batch: FeedBatchLog): FeedBatchLog
}
