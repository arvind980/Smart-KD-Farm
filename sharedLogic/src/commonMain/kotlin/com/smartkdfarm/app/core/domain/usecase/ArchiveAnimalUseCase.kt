package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.NotFoundException
import com.smartkdfarm.app.core.domain.model.AnimalArchiveReason
import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.KhataDirection
import com.smartkdfarm.app.core.domain.model.KhataTransaction
import com.smartkdfarm.app.core.domain.model.KhataTransactionCategory
import com.smartkdfarm.app.core.domain.model.PermissionChecker
import com.smartkdfarm.app.core.domain.model.PermissionLevel
import com.smartkdfarm.app.core.domain.model.StaffModule
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.repository.FinanceRepository
import com.smartkdfarm.app.core.domain.repository.LivestockRepository

class ArchiveAnimalUseCase(
    private val livestockRepository: LivestockRepository,
    private val financeRepository: FinanceRepository,
) {
    /**
     * Archives (soft-deletes) an animal and optionally records a sale ledger entry.
     *
     * Required permission: LIVESTOCK → APPROVE  (ADMIN only by default)
     * This is a high-impact, irreversible action — only ADMIN may perform it.
     */
    suspend operator fun invoke(
        actor: User,
        farmId: String,
        animalId: String,
        reason: AnimalArchiveReason,
        saleAmount: Double? = null,
        eventEpochMillis: Long = TimeProvider.nowEpochMillis(),
        notes: String? = null,
    ): com.smartkdfarm.app.core.domain.model.AnimalProfile {
        PermissionChecker.requireRole(
            actor, UserRole.ADMIN,
            message = "Only ADMIN can archive animals."
        )
        val animal = livestockRepository.getAnimal(farmId, animalId)
            ?: throw NotFoundException("No livestock profile found for animalId=$animalId.")

        require(animal.isActive) { "This animal is already archived." }

        val archivedAnimal = animal.copy(
            isActive = false,
            archiveReason = reason,
            archivedAtEpochMillis = eventEpochMillis,
            updatedAtEpochMillis = TimeProvider.nowEpochMillis(),
        )
        val savedAnimal = livestockRepository.archiveAnimal(farmId, archivedAnimal)

        if (reason == AnimalArchiveReason.SOLD) {
            val amount = (saleAmount ?: animal.purchasePrice).coerceAtLeast(0.0)
            financeRepository.recordOutgoingTransaction(
                KhataTransaction(
                    id = IdGenerator.newId(prefix = "khata"),
                    farmId = farmId,
                    animalId = animal.id,
                    animalTagNumber = animal.tagNumber,
                    direction = KhataDirection.OUTGOING,
                    category = KhataTransactionCategory.LIVESTOCK_SALE,
                    amount = amount,
                    notes = notes?.trim().takeUnless { it.isNullOrBlank() }
                        ?: "Animal ${animal.tagNumber} archived after sale.",
                    occurredAtEpochMillis = eventEpochMillis,
                )
            )
        }
        return savedAnimal
    }
}
