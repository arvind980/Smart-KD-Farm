package com.smartkdfarm.app.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class DashboardAlertSeverity {
    INFO,
    WARNING,
    CRITICAL,
}

@Serializable
enum class StaffModule {
    DASHBOARD,
    LIVESTOCK,
    OUTER_CENTER,
    KHATA,
    INVENTORY,
    FARM_CONFIG,
    STAFF_CONTROL,
    REPORTS,
}

@Serializable
enum class PermissionLevel {
    NONE,
    VIEW,
    MANAGE,
    APPROVE,
}

@Serializable
enum class MilkSession {
    MORNING,
    EVENING,
}

@Serializable
enum class CollectionPaymentStatus {
    PENDING,
    APPROVED,
    PAID,
}

@Serializable
enum class InventoryCategory {
    LIVESTOCK,
    FODDER,
    FEED,
    MEDICINE,
    EQUIPMENT,
}

@Serializable
enum class KhataEntryKind {
    INCOME,
    EXPENSE,
}

@Serializable
enum class PredictionSource {
    LACTATION_CURVE,
    SUPPLY_DEAL,
}

@Serializable
enum class NotificationChannel {
    SMS,
    WHATSAPP,
    PUSH,
}

@Serializable
enum class NotificationStatus {
    QUEUED,
    SENT,
    FAILED,
}

@Serializable
data class DashboardAlert(
    val id: String,
    val farmId: String,
    val title: String,
    val message: String,
    val severity: DashboardAlertSeverity,
    val actionLabel: String? = null,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
    val acknowledged: Boolean = false,
)

@Serializable
data class KpiSnapshot(
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
data class PredictionPoint(
    val monthIndex: Int,
    val label: String,
    val projectedMilkLiters: Double,
    val projectedRevenue: Double,
    val source: PredictionSource,
)

@Serializable
data class LandPartition(
    val id: String,
    val farmId: String,
    val name: String,
    val area: AreaConfiguration,
    val cropOrUse: String,
    val colorHex: String,
    val notes: String? = null,
)

@Serializable
data class FarmSetupConfig(
    val id: String,
    val farmId: String,
    val totalArea: AreaConfiguration,
    val partitionAreaTotalInAcres: Double,
    val sumOfPartsValid: Boolean,
    val validationStatus: String,
    val notes: String? = null,
    val updatedAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

@Serializable
data class StaffPermission(
    val module: StaffModule,
    val level: PermissionLevel,
)

@Serializable
data class StaffAccessProfile(
    val id: String,
    val farmId: String,
    val userId: String,
    val fullName: String,
    val role: UserRole,
    val phoneNumber: String,
    val permissions: List<StaffPermission>,
    val shiftLabel: String? = null,
    val isActive: Boolean = true,
    val lastActiveAtEpochMillis: Long? = null,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
    val updatedAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

@Serializable
data class AutoRateBreakdown(
    val fat: Double,
    val snf: Double,
    val quantityLiters: Double,
    val baseRatePerLiter: Double,
    val bonusAmount: Double = 0.0,
    val deductionAmount: Double = 0.0,
    val totalAmount: Double,
)

@Serializable
data class MilkCollectionEntry(
    val id: String,
    val farmId: String,
    val farmerId: String,
    val farmerName: String,
    val session: MilkSession,
    val collectedAtEpochMillis: Long,
    val rateBreakdown: AutoRateBreakdown,
    val paymentStatus: CollectionPaymentStatus = CollectionPaymentStatus.PENDING,
    val smsTriggered: Boolean = false,
    val createdByUserId: String,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

@Serializable
data class FarmerNotification(
    val id: String,
    val farmId: String,
    val farmerId: String,
    val channel: NotificationChannel,
    val message: String,
    val status: NotificationStatus,
    val triggeredByRecordId: String,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

@Serializable
data class InventoryItem(
    val id: String,
    val farmId: String,
    val name: String,
    val category: InventoryCategory,
    val unit: String,
    val currentStock: Double,
    val reorderLevel: Double,
    val vendorName: String? = null,
    val notes: String? = null,
    val updatedAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

@Serializable
data class FeedFormulaIngredient(
    val inventoryItemId: String,
    val itemName: String,
    val quantityUsed: Double,
    val unit: String,
)

@Serializable
data class FeedBatchLog(
    val id: String,
    val farmId: String,
    val batchName: String,
    val producedAtEpochMillis: Long,
    val totalOutputKg: Double,
    val ingredients: List<FeedFormulaIngredient>,
    val autoStockDeducted: Boolean,
    val notes: String? = null,
    val createdByUserId: String,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

@Serializable
data class KhataLedgerEntry(
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
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

// ─── Fodder Yield ─────────────────────────────────────────────────────────────

@Serializable
data class FodderYieldLog(
    val id: String,
    val farmId: String,
    val landPartitionId: String,
    val partitionName: String,
    val cropType: String,
    val yieldKg: Double,
    val harvestDateEpochMillis: Long,
    val stockFraction: Float = 1.0f,
    val notes: String? = null,
    val createdByUserId: String,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

// ─── Biogas & Waste Management ────────────────────────────────────────────────

@Serializable
enum class WasteCategory {
    DUNG,
    SLURRY,
    BIOGAS_OUTPUT,
    COMPOST,
    GENERAL,
}

@Serializable
data class WasteLog(
    val id: String,
    val farmId: String,
    val category: WasteCategory,
    val quantityKg: Double,
    val biogasOutputCubicMeters: Double? = null,
    val compositeReadyKg: Double? = null,
    val recordedAtEpochMillis: Long,
    val notes: String? = null,
    val createdByUserId: String,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

// ─── SMS Gateway ──────────────────────────────────────────────────────────────

@Serializable
enum class SmsProvider {
    MSG91,
    TWILIO,
    FAST2SMS,
    CUSTOM_HTTP,
}

@Serializable
data class SmsGatewayConfig(
    val provider: SmsProvider,
    val apiKey: String,
    val senderId: String,
    val routeId: String? = null,
    val baseUrl: String? = null,
    val isActive: Boolean = true,
)

@Serializable
data class SmsDeliveryReport(
    val id: String,
    val farmId: String,
    val notificationId: String,
    val toPhoneNumber: String,
    val provider: SmsProvider,
    val externalMessageId: String? = null,
    val statusCode: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null,
    val sentAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)
