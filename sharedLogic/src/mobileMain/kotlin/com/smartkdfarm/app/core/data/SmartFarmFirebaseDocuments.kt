package com.smartkdfarm.app.core.data

import kotlinx.serialization.Serializable
import com.smartkdfarm.app.core.domain.model.AreaConfiguration
import com.smartkdfarm.app.core.domain.model.AutoRateBreakdown
import com.smartkdfarm.app.core.domain.model.CollectionPaymentStatus
import com.smartkdfarm.app.core.domain.model.DashboardAlert
import com.smartkdfarm.app.core.domain.model.DashboardAlertSeverity
import com.smartkdfarm.app.core.domain.model.FarmerNotification
import com.smartkdfarm.app.core.domain.model.FeedBatchLog
import com.smartkdfarm.app.core.domain.model.FeedFormulaIngredient
import com.smartkdfarm.app.core.domain.model.FarmSetupConfig
import com.smartkdfarm.app.core.domain.model.FodderYieldLog
import com.smartkdfarm.app.core.domain.model.InventoryCategory
import com.smartkdfarm.app.core.domain.model.InventoryItem
import com.smartkdfarm.app.core.domain.model.KhataEntryKind
import com.smartkdfarm.app.core.domain.model.KhataLedgerEntry
import com.smartkdfarm.app.core.domain.model.KpiSnapshot
import com.smartkdfarm.app.core.domain.model.LandPartition
import com.smartkdfarm.app.core.domain.model.MilkCollectionEntry
import com.smartkdfarm.app.core.domain.model.MilkSession
import com.smartkdfarm.app.core.domain.model.NotificationChannel
import com.smartkdfarm.app.core.domain.model.NotificationStatus
import com.smartkdfarm.app.core.domain.model.PermissionLevel
import com.smartkdfarm.app.core.domain.model.PredictionPoint
import com.smartkdfarm.app.core.domain.model.PredictionSource
import com.smartkdfarm.app.core.domain.model.StaffAccessProfile
import com.smartkdfarm.app.core.domain.model.StaffModule
import com.smartkdfarm.app.core.domain.model.StaffPermission
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.model.WasteCategory
import com.smartkdfarm.app.core.domain.model.WasteLog

@Serializable
internal data class DashboardAlertDocument(
    val id: String,
    val farmId: String,
    val title: String,
    val message: String,
    val severity: DashboardAlertSeverity,
    val actionLabel: String? = null,
    val createdAtEpochMillis: Long,
    val acknowledged: Boolean,
)

@Serializable
internal data class KpiSnapshotDocument(
    val id: String,
    val farmId: String,
    val asOfEpochMillis: Long,
    val totalMilkLiters: Double,
    val activeAnimals: Int,
    val netProfitLoss: Double,
    val lowStockItemName: String? = null,
    val lowStockBalance: Double? = null,
)

@Serializable
internal data class PredictionPointDocument(
    val monthIndex: Int,
    val label: String,
    val projectedMilkLiters: Double,
    val projectedRevenue: Double,
    val source: PredictionSource,
)

@Serializable
internal data class LandPartitionDocument(
    val id: String,
    val farmId: String,
    val name: String,
    val area: AreaConfiguration,
    val cropOrUse: String,
    val colorHex: String,
    val notes: String? = null,
)

@Serializable
internal data class FarmSetupConfigDocument(
    val id: String,
    val farmId: String,
    val totalArea: AreaConfiguration,
    val partitionAreaTotalInAcres: Double,
    val sumOfPartsValid: Boolean,
    val validationStatus: String,
    val notes: String? = null,
    val updatedAtEpochMillis: Long,
)

@Serializable
internal data class StaffPermissionDocument(
    val module: StaffModule,
    val level: PermissionLevel,
)

@Serializable
internal data class StaffAccessProfileDocument(
    val id: String,
    val farmId: String,
    val userId: String,
    val fullName: String,
    val role: UserRole,
    val phoneNumber: String,
    val permissions: List<StaffPermissionDocument>,
    val shiftLabel: String? = null,
    val isActive: Boolean,
    val lastActiveAtEpochMillis: Long? = null,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Serializable
internal data class AutoRateBreakdownDocument(
    val fat: Double,
    val snf: Double,
    val quantityLiters: Double,
    val baseRatePerLiter: Double,
    val bonusAmount: Double,
    val deductionAmount: Double,
    val totalAmount: Double,
)

@Serializable
internal data class MilkCollectionEntryDocument(
    val id: String,
    val farmId: String,
    val farmerId: String,
    val farmerName: String,
    val session: MilkSession,
    val collectedAtEpochMillis: Long,
    val rateBreakdown: AutoRateBreakdownDocument,
    val paymentStatus: CollectionPaymentStatus,
    val smsTriggered: Boolean,
    val createdByUserId: String,
    val createdAtEpochMillis: Long,
)

@Serializable
internal data class FarmerNotificationDocument(
    val id: String,
    val farmId: String,
    val farmerId: String,
    val channel: NotificationChannel,
    val message: String,
    val status: NotificationStatus,
    val triggeredByRecordId: String,
    val createdAtEpochMillis: Long,
)

@Serializable
internal data class InventoryItemDocument(
    val id: String,
    val farmId: String,
    val name: String,
    val category: InventoryCategory,
    val unit: String,
    val currentStock: Double,
    val reorderLevel: Double,
    val vendorName: String? = null,
    val notes: String? = null,
    val updatedAtEpochMillis: Long,
)

@Serializable
internal data class FeedFormulaIngredientDocument(
    val inventoryItemId: String,
    val itemName: String,
    val quantityUsed: Double,
    val unit: String,
)

@Serializable
internal data class FeedBatchLogDocument(
    val id: String,
    val farmId: String,
    val batchName: String,
    val producedAtEpochMillis: Long,
    val totalOutputKg: Double,
    val ingredients: List<FeedFormulaIngredientDocument>,
    val autoStockDeducted: Boolean,
    val notes: String? = null,
    val createdByUserId: String,
    val createdAtEpochMillis: Long,
)

@Serializable
internal data class KhataLedgerEntryDocument(
    val id: String,
    val farmId: String,
    val kind: KhataEntryKind,
    val title: String,
    val amount: Double,
    val sourceModule: String,
    val linkedRecordId: String? = null,
    val occurredAtEpochMillis: Long,
    val createdByUserId: String,
    val notes: String? = null,
    val createdAtEpochMillis: Long,
)

internal fun DashboardAlert.toDocument(): DashboardAlertDocument = DashboardAlertDocument(
    id = id,
    farmId = farmId,
    title = title,
    message = message,
    severity = severity,
    actionLabel = actionLabel,
    createdAtEpochMillis = createdAtEpochMillis,
    acknowledged = acknowledged,
)

internal fun KpiSnapshot.toDocument(): KpiSnapshotDocument = KpiSnapshotDocument(
    id = id,
    farmId = farmId,
    asOfEpochMillis = asOfEpochMillis,
    totalMilkLiters = totalMilkLiters,
    activeAnimals = activeAnimals,
    netProfitLoss = netProfitLoss,
    lowStockItemName = lowStockItemName,
    lowStockBalance = lowStockBalance,
)

internal fun PredictionPoint.toDocument(): PredictionPointDocument = PredictionPointDocument(
    monthIndex = monthIndex,
    label = label,
    projectedMilkLiters = projectedMilkLiters,
    projectedRevenue = projectedRevenue,
    source = source,
)

internal fun LandPartition.toDocument(): LandPartitionDocument = LandPartitionDocument(
    id = id,
    farmId = farmId,
    name = name,
    area = area,
    cropOrUse = cropOrUse,
    colorHex = colorHex,
    notes = notes,
)

internal fun LandPartitionDocument.toDomain(): LandPartition = LandPartition(
    id = id,
    farmId = farmId,
    name = name,
    area = area,
    cropOrUse = cropOrUse,
    colorHex = colorHex,
    notes = notes,
)

internal fun FarmSetupConfig.toDocument(): FarmSetupConfigDocument = FarmSetupConfigDocument(
    id = id,
    farmId = farmId,
    totalArea = totalArea,
    partitionAreaTotalInAcres = partitionAreaTotalInAcres,
    sumOfPartsValid = sumOfPartsValid,
    validationStatus = validationStatus,
    notes = notes,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun FarmSetupConfigDocument.toDomain(): FarmSetupConfig = FarmSetupConfig(
    id = id,
    farmId = farmId,
    totalArea = totalArea,
    partitionAreaTotalInAcres = partitionAreaTotalInAcres,
    sumOfPartsValid = sumOfPartsValid,
    validationStatus = validationStatus,
    notes = notes,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun StaffPermission.toDocument(): StaffPermissionDocument = StaffPermissionDocument(
    module = module,
    level = level,
)

internal fun StaffAccessProfile.toDocument(): StaffAccessProfileDocument = StaffAccessProfileDocument(
    id = id,
    farmId = farmId,
    userId = userId,
    fullName = fullName,
    role = role,
    phoneNumber = phoneNumber,
    permissions = permissions.map(StaffPermission::toDocument),
    shiftLabel = shiftLabel,
    isActive = isActive,
    lastActiveAtEpochMillis = lastActiveAtEpochMillis,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun StaffPermissionDocument.toDomain(): StaffPermission = StaffPermission(
    module = module,
    level = level,
)

internal fun StaffAccessProfileDocument.toDomain(): StaffAccessProfile = StaffAccessProfile(
    id = id,
    farmId = farmId,
    userId = userId,
    fullName = fullName,
    role = role,
    phoneNumber = phoneNumber,
    permissions = permissions.map(StaffPermissionDocument::toDomain),
    shiftLabel = shiftLabel,
    isActive = isActive,
    lastActiveAtEpochMillis = lastActiveAtEpochMillis,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun AutoRateBreakdown.toDocument(): AutoRateBreakdownDocument = AutoRateBreakdownDocument(
    fat = fat,
    snf = snf,
    quantityLiters = quantityLiters,
    baseRatePerLiter = baseRatePerLiter,
    bonusAmount = bonusAmount,
    deductionAmount = deductionAmount,
    totalAmount = totalAmount,
)

internal fun AutoRateBreakdownDocument.toDomain(): AutoRateBreakdown = AutoRateBreakdown(
    fat = fat,
    snf = snf,
    quantityLiters = quantityLiters,
    baseRatePerLiter = baseRatePerLiter,
    bonusAmount = bonusAmount,
    deductionAmount = deductionAmount,
    totalAmount = totalAmount,
)

internal fun MilkCollectionEntry.toDocument(): MilkCollectionEntryDocument = MilkCollectionEntryDocument(
    id = id,
    farmId = farmId,
    farmerId = farmerId,
    farmerName = farmerName,
    session = session,
    collectedAtEpochMillis = collectedAtEpochMillis,
    rateBreakdown = rateBreakdown.toDocument(),
    paymentStatus = paymentStatus,
    smsTriggered = smsTriggered,
    createdByUserId = createdByUserId,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun FarmerNotification.toDocument(): FarmerNotificationDocument = FarmerNotificationDocument(
    id = id,
    farmId = farmId,
    farmerId = farmerId,
    channel = channel,
    message = message,
    status = status,
    triggeredByRecordId = triggeredByRecordId,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun InventoryItem.toDocument(): InventoryItemDocument = InventoryItemDocument(
    id = id,
    farmId = farmId,
    name = name,
    category = category,
    unit = unit,
    currentStock = currentStock,
    reorderLevel = reorderLevel,
    vendorName = vendorName,
    notes = notes,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun FeedFormulaIngredient.toDocument(): FeedFormulaIngredientDocument = FeedFormulaIngredientDocument(
    inventoryItemId = inventoryItemId,
    itemName = itemName,
    quantityUsed = quantityUsed,
    unit = unit,
)

internal fun FeedBatchLog.toDocument(): FeedBatchLogDocument = FeedBatchLogDocument(
    id = id,
    farmId = farmId,
    batchName = batchName,
    producedAtEpochMillis = producedAtEpochMillis,
    totalOutputKg = totalOutputKg,
    ingredients = ingredients.map(FeedFormulaIngredient::toDocument),
    autoStockDeducted = autoStockDeducted,
    notes = notes,
    createdByUserId = createdByUserId,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun KhataLedgerEntry.toDocument(): KhataLedgerEntryDocument = KhataLedgerEntryDocument(
    id = id,
    farmId = farmId,
    kind = kind,
    title = title,
    amount = amount,
    sourceModule = sourceModule,
    linkedRecordId = linkedRecordId,
    occurredAtEpochMillis = occurredAtEpochMillis,
    createdByUserId = createdByUserId,
    notes = notes,
    createdAtEpochMillis = createdAtEpochMillis,
)

// ─── Fodder Yield Documents ───────────────────────────────────────────────────

@Serializable
internal data class FodderYieldLogDocument(
    val id: String,
    val farmId: String,
    val landPartitionId: String,
    val partitionName: String,
    val cropType: String,
    val yieldKg: Double,
    val harvestDateEpochMillis: Long,
    val stockFraction: Float,
    val notes: String? = null,
    val createdByUserId: String,
    val createdAtEpochMillis: Long,
)

internal fun FodderYieldLog.toDocument(): FodderYieldLogDocument = FodderYieldLogDocument(
    id = id,
    farmId = farmId,
    landPartitionId = landPartitionId,
    partitionName = partitionName,
    cropType = cropType,
    yieldKg = yieldKg,
    harvestDateEpochMillis = harvestDateEpochMillis,
    stockFraction = stockFraction,
    notes = notes,
    createdByUserId = createdByUserId,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun FodderYieldLogDocument.toDomain(): FodderYieldLog = FodderYieldLog(
    id = id,
    farmId = farmId,
    landPartitionId = landPartitionId,
    partitionName = partitionName,
    cropType = cropType,
    yieldKg = yieldKg,
    harvestDateEpochMillis = harvestDateEpochMillis,
    stockFraction = stockFraction,
    notes = notes,
    createdByUserId = createdByUserId,
    createdAtEpochMillis = createdAtEpochMillis,
)

// ─── Waste Log Documents ──────────────────────────────────────────────────────

@Serializable
internal data class WasteLogDocument(
    val id: String,
    val farmId: String,
    val category: WasteCategory,
    val quantityKg: Double,
    val biogasOutputCubicMeters: Double? = null,
    val compositeReadyKg: Double? = null,
    val recordedAtEpochMillis: Long,
    val notes: String? = null,
    val createdByUserId: String,
    val createdAtEpochMillis: Long,
)

internal fun WasteLog.toDocument(): WasteLogDocument = WasteLogDocument(
    id = id,
    farmId = farmId,
    category = category,
    quantityKg = quantityKg,
    biogasOutputCubicMeters = biogasOutputCubicMeters,
    compositeReadyKg = compositeReadyKg,
    recordedAtEpochMillis = recordedAtEpochMillis,
    notes = notes,
    createdByUserId = createdByUserId,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun WasteLogDocument.toDomain(): WasteLog = WasteLog(
    id = id,
    farmId = farmId,
    category = category,
    quantityKg = quantityKg,
    biogasOutputCubicMeters = biogasOutputCubicMeters,
    compositeReadyKg = compositeReadyKg,
    recordedAtEpochMillis = recordedAtEpochMillis,
    notes = notes,
    createdByUserId = createdByUserId,
    createdAtEpochMillis = createdAtEpochMillis,
)
