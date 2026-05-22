package com.smartkdfarm.app.presentation.operations

import com.smartkdfarm.app.core.domain.model.DashboardAlert
import com.smartkdfarm.app.core.domain.model.FarmerNotification
import com.smartkdfarm.app.core.domain.model.FeedBatchLog
import com.smartkdfarm.app.core.domain.model.FodderYieldLog
import com.smartkdfarm.app.core.domain.model.InventoryItem
import com.smartkdfarm.app.core.domain.model.KhataLedgerEntry
import com.smartkdfarm.app.core.domain.model.MilkCollectionEntry
import com.smartkdfarm.app.core.domain.model.PredictionPoint
import com.smartkdfarm.app.core.domain.model.WasteLog

data class OperationsUiState(
    val farmId: String? = null,
    val isLoading: Boolean = false,
    val milkCollections: List<MilkCollectionEntry> = emptyList(),
    val notifications: List<FarmerNotification> = emptyList(),
    val inventoryItems: List<InventoryItem> = emptyList(),
    val feedBatches: List<FeedBatchLog> = emptyList(),
    val fodderYields: List<FodderYieldLog> = emptyList(),
    val wasteLogs: List<WasteLog> = emptyList(),
    val ledgerEntries: List<KhataLedgerEntry> = emptyList(),
    val predictions: List<PredictionPoint> = emptyList(),
    val alerts: List<DashboardAlert> = emptyList(),
    val errorMessage: String? = null,
)
