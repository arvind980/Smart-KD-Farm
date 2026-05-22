package com.smartkdfarm.app.presentation.operations

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.smartkdfarm.app.core.domain.model.FeedFormulaIngredient
import com.smartkdfarm.app.core.domain.model.FarmerNotification
import com.smartkdfarm.app.core.domain.model.FeedBatchLog
import com.smartkdfarm.app.core.domain.model.FodderYieldLog
import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.InventoryCategory
import com.smartkdfarm.app.core.domain.model.InventoryItem
import com.smartkdfarm.app.core.domain.model.KhataEntryKind
import com.smartkdfarm.app.core.domain.model.KhataLedgerEntry
import com.smartkdfarm.app.core.domain.model.MilkCollectionEntry
import com.smartkdfarm.app.core.domain.model.NotificationStatus
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.model.WasteCategory
import com.smartkdfarm.app.core.domain.model.WasteLog
import com.smartkdfarm.app.core.domain.repository.AnalyticsRepository
import com.smartkdfarm.app.core.domain.repository.AuthRepository
import com.smartkdfarm.app.core.domain.repository.FinanceRepository
import com.smartkdfarm.app.core.domain.repository.FodderRepository
import com.smartkdfarm.app.core.domain.repository.InventoryRepository
import com.smartkdfarm.app.core.domain.repository.OuterCenterRepository
import com.smartkdfarm.app.core.domain.repository.WasteRepository
import com.smartkdfarm.app.core.domain.usecase.GenerateSupplyPredictionUseCase
import com.smartkdfarm.app.core.domain.usecase.ProduceFeedBatchUseCase
import com.smartkdfarm.app.core.domain.usecase.RecordMilkCollectionUseCase
import com.smartkdfarm.app.presentation.auth.CloseableHandle

class OperationsViewModel(
    private val authRepository: AuthRepository,
    private val outerCenterRepository: OuterCenterRepository,
    private val inventoryRepository: InventoryRepository,
    private val financeRepository: FinanceRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val fodderRepository: FodderRepository,
    private val wasteRepository: WasteRepository,
    private val recordMilkCollectionUseCase: RecordMilkCollectionUseCase,
    private val produceFeedBatchUseCase: ProduceFeedBatchUseCase,
    private val generateSupplyPredictionUseCase: GenerateSupplyPredictionUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mutableUiState = MutableStateFlow(OperationsUiState())
    val uiState: StateFlow<OperationsUiState> = mutableUiState.asStateFlow()

    init {
        observeSession()
    }

    fun currentState(): OperationsUiState = uiState.value

    fun startObserving(observer: (OperationsUiState) -> Unit): CloseableHandle {
        val job = scope.launch {
            uiState.collectLatest(observer)
        }
        return CloseableHandle(job::cancel)
    }

    fun refresh() {
        val farmId = mutableUiState.value.farmId ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                mutableUiState.value = mutableUiState.value.copy(
                    milkCollections = outerCenterRepository.getCollections(farmId),
                    notifications = outerCenterRepository.getNotifications(farmId),
                    inventoryItems = inventoryRepository.getInventoryItems(farmId),
                    feedBatches = inventoryRepository.getFeedBatches(farmId),
                    fodderYields = fodderRepository.getFodderYields(farmId),
                    wasteLogs = wasteRepository.getWasteLogs(farmId),
                    ledgerEntries = financeRepository.getLedgerEntries(farmId),
                    predictions = analyticsRepository.getPredictions(farmId),
                    alerts = analyticsRepository.getDashboardAlerts(farmId),
                    isLoading = false,
                )
            }.onFailure {
                mutableUiState.value = mutableUiState.value.copy(isLoading = false, errorMessage = it.message)
            }
        }
    }

    fun recordMilkCollection(entry: MilkCollectionEntry) {
        val actor = authRepository.currentAuthenticatedUser() ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { recordMilkCollectionUseCase(actor, entry) }
                .onSuccess { refresh() }
                .onFailure { mutableUiState.value = mutableUiState.value.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun produceFeedBatch(batch: FeedBatchLog) {
        val actor = authRepository.currentAuthenticatedUser() ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { produceFeedBatchUseCase(actor, batch) }
                .onSuccess { refresh() }
                .onFailure { mutableUiState.value = mutableUiState.value.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun generatePredictions() {
        val farmId = mutableUiState.value.farmId ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { generateSupplyPredictionUseCase(farmId) }
                .onSuccess { refresh() }
                .onFailure { mutableUiState.value = mutableUiState.value.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun updateNotificationStatus(
        notification: FarmerNotification,
        status: NotificationStatus,
    ) {
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                outerCenterRepository.saveNotification(notification.copy(status = status))
            }.onSuccess { refresh() }
                .onFailure { mutableUiState.value = mutableUiState.value.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun saveInventoryItem(
        existingId: String?,
        name: String,
        category: InventoryCategory,
        unit: String,
        currentStock: Double,
        reorderLevel: Double,
        vendorName: String? = null,
    ) {
        val actor = authRepository.currentAuthenticatedUser() ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                inventoryRepository.upsertInventoryItem(
                    InventoryItem(
                        id = existingId?.takeIf { it.isNotBlank() } ?: IdGenerator.newId(prefix = "inventory"),
                        farmId = actor.farmId,
                        name = name,
                        category = category,
                        unit = unit,
                        currentStock = currentStock,
                        reorderLevel = reorderLevel,
                        vendorName = vendorName,
                        updatedAtEpochMillis = TimeProvider.nowEpochMillis(),
                    )
                )
            }.onSuccess { refresh() }
                .onFailure { mutableUiState.value = mutableUiState.value.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun recordManualLedgerEntry(
        title: String,
        amount: Double,
        kind: KhataEntryKind,
        notes: String? = null,
    ) {
        val actor = authRepository.currentAuthenticatedUser() ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                financeRepository.recordLedgerEntry(
                    KhataLedgerEntry(
                        id = IdGenerator.newId(prefix = "ledger"),
                        farmId = actor.farmId,
                        kind = kind,
                        title = title,
                        amount = amount,
                        sourceModule = "Manual Khata",
                        occurredAtEpochMillis = TimeProvider.nowEpochMillis(),
                        createdByUserId = actor.id,
                        notes = notes,
                    )
                )
            }.onSuccess { refresh() }
                .onFailure { mutableUiState.value = mutableUiState.value.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun produceSimpleFeedBatch(
        batchName: String,
        totalOutputKg: Double,
        inventoryItemId: String,
        quantityUsed: Double,
    ) {
        val actor = authRepository.currentAuthenticatedUser() ?: return
        val inventoryItem = mutableUiState.value.inventoryItems.firstOrNull { it.id == inventoryItemId } ?: return
        produceFeedBatch(
            FeedBatchLog(
                id = "",
                farmId = actor.farmId,
                batchName = batchName,
                producedAtEpochMillis = TimeProvider.nowEpochMillis(),
                totalOutputKg = totalOutputKg,
                ingredients = listOf(
                    FeedFormulaIngredient(
                        inventoryItemId = inventoryItem.id,
                        itemName = inventoryItem.name,
                        quantityUsed = quantityUsed,
                        unit = inventoryItem.unit,
                    )
                ),
                autoStockDeducted = false,
                createdByUserId = actor.id,
            )
        )
    }

    // ─── Fodder Yield ─────────────────────────────────────────────────────────

    fun saveFodderYield(
        landPartitionId: String,
        partitionName: String,
        cropType: String,
        yieldKg: Double,
        harvestDateEpochMillis: Long,
        stockFraction: Float = 1.0f,
        notes: String? = null,
    ) {
        val actor = authRepository.currentAuthenticatedUser() ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                fodderRepository.saveFodderYield(
                    FodderYieldLog(
                        id = IdGenerator.newId(prefix = "fodder"),
                        farmId = actor.farmId,
                        landPartitionId = landPartitionId,
                        partitionName = partitionName,
                        cropType = cropType,
                        yieldKg = yieldKg,
                        harvestDateEpochMillis = harvestDateEpochMillis,
                        stockFraction = stockFraction,
                        notes = notes,
                        createdByUserId = actor.id,
                    )
                )
            }.onSuccess { refresh() }
                .onFailure { mutableUiState.value = mutableUiState.value.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    // ─── Waste & Biogas ───────────────────────────────────────────────────────

    fun saveWasteLog(
        category: WasteCategory,
        quantityKg: Double,
        biogasOutputCubicMeters: Double? = null,
        compositeReadyKg: Double? = null,
        notes: String? = null,
    ) {
        val actor = authRepository.currentAuthenticatedUser() ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                wasteRepository.saveWasteLog(
                    WasteLog(
                        id = IdGenerator.newId(prefix = "waste"),
                        farmId = actor.farmId,
                        category = category,
                        quantityKg = quantityKg,
                        biogasOutputCubicMeters = biogasOutputCubicMeters,
                        compositeReadyKg = compositeReadyKg,
                        recordedAtEpochMillis = TimeProvider.nowEpochMillis(),
                        notes = notes,
                        createdByUserId = actor.id,
                    )
                )
            }.onSuccess { refresh() }
                .onFailure { mutableUiState.value = mutableUiState.value.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun logBiogasOutput(
        dungKg: Double,
        biogasCubicMeters: Double,
        compositeReadyKg: Double? = null,
        notes: String? = null,
    ) {
        saveWasteLog(
            category = WasteCategory.BIOGAS_OUTPUT,
            quantityKg = dungKg,
            biogasOutputCubicMeters = biogasCubicMeters,
            compositeReadyKg = compositeReadyKg,
            notes = notes,
        )
    }

    // ─── Misc ─────────────────────────────────────────────────────────────────

    fun clearTransientMessage() {
        mutableUiState.value = mutableUiState.value.copy(errorMessage = null)
    }

    private fun observeSession() {
        scope.launch {
            authRepository.observeAuthenticatedUser().collectLatest { user ->
                mutableUiState.value = if (user == null) {
                    OperationsUiState()
                } else {
                    OperationsUiState(farmId = user.farmId, isLoading = true)
                }
                if (user != null) {
                    refresh()
                }
            }
        }
    }

    fun dispose() {
        scope.coroutineContext[Job]?.cancel()
    }
}
