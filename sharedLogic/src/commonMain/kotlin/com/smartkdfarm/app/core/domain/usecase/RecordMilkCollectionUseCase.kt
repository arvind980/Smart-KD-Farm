package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.AuthorizationException
import com.smartkdfarm.app.core.domain.exception.ValidationException
import com.smartkdfarm.app.core.domain.model.FarmerNotification
import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.KhataEntryKind
import com.smartkdfarm.app.core.domain.model.KhataLedgerEntry
import com.smartkdfarm.app.core.domain.model.MilkCollectionEntry
import com.smartkdfarm.app.core.domain.model.NotificationChannel
import com.smartkdfarm.app.core.domain.model.NotificationStatus
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.repository.FarmRepository
import com.smartkdfarm.app.core.domain.repository.FinanceRepository
import com.smartkdfarm.app.core.domain.repository.OuterCenterRepository
import com.smartkdfarm.app.core.domain.service.SmsGatewayService

class RecordMilkCollectionUseCase(
    private val outerCenterRepository: OuterCenterRepository,
    private val financeRepository: FinanceRepository,
    private val farmRepository: FarmRepository,
    private val smsGatewayService: SmsGatewayService,
) {
    suspend operator fun invoke(
        actor: User,
        entry: MilkCollectionEntry,
    ): MilkCollectionEntry {
        if (actor.role != UserRole.ADMIN && actor.role != UserRole.DAIRY_MAN) {
            throw AuthorizationException("Only ADMIN or DAIRY_MAN can record milk collections.")
        }
        if (entry.farmId != actor.farmId) {
            throw AuthorizationException("Milk collection can only be recorded within the actor farm.")
        }
        if (entry.farmerName.isBlank()) {
            throw ValidationException("Farmer name is required.")
        }
        if (entry.rateBreakdown.quantityLiters <= 0.0) {
            throw ValidationException("Milk quantity must be greater than zero.")
        }
        if (entry.rateBreakdown.totalAmount < 0.0) {
            throw ValidationException("Collection amount cannot be negative.")
        }

        val now = TimeProvider.nowEpochMillis()
        val saved = outerCenterRepository.saveCollection(
            entry.copy(
                id = entry.id.ifBlank { IdGenerator.newId(prefix = "collection") },
                createdByUserId = actor.id,
                createdAtEpochMillis = now,
            )
        )

        // ── Build SMS message ──────────────────────────────────────────────────
        val smsMessage = "Smart KD Farm: Aaj ${saved.rateBreakdown.quantityLiters}L doodh receive hua. " +
            "Total: Rs ${saved.rateBreakdown.totalAmount}."

        val notificationId = IdGenerator.newId(prefix = "notification")

        // ── Attempt real SMS delivery if gateway is configured ─────────────────
        val farmProfile = runCatching { farmRepository.getFarmProfile(actor.farmId) }.getOrNull()
        val smsConfig = farmProfile?.smsGatewayConfig

        val finalStatus = if (smsConfig != null && smsConfig.isActive) {
            val report = runCatching {
                smsGatewayService.sendSms(
                    farmId = actor.farmId,
                    notificationId = notificationId,
                    toPhoneNumber = saved.farmerId,    // farmerId holds the phone number in this design
                    message = smsMessage,
                    config = smsConfig,
                )
            }.getOrNull()
            if (report?.isSuccess == true) NotificationStatus.SENT else NotificationStatus.FAILED
        } else {
            NotificationStatus.QUEUED
        }

        outerCenterRepository.saveNotification(
            FarmerNotification(
                id = notificationId,
                farmId = actor.farmId,
                farmerId = saved.farmerId,
                channel = NotificationChannel.SMS,
                message = smsMessage,
                status = finalStatus,
                triggeredByRecordId = saved.id,
                createdAtEpochMillis = now,
            )
        )

        financeRepository.recordLedgerEntry(
            KhataLedgerEntry(
                id = IdGenerator.newId(prefix = "ledger"),
                farmId = actor.farmId,
                kind = KhataEntryKind.INCOME,
                title = "Milk collection: ${saved.farmerName}",
                amount = saved.rateBreakdown.totalAmount,
                sourceModule = "Outer Center",
                linkedRecordId = saved.id,
                occurredAtEpochMillis = saved.collectedAtEpochMillis,
                createdByUserId = actor.id,
                notes = "Auto-posted from milk collection",
            )
        )
        return saved
    }
}
