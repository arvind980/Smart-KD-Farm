package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.AuthorizationException
import com.smartkdfarm.app.core.domain.exception.ValidationException
import com.smartkdfarm.app.core.domain.model.AreaUnit
import com.smartkdfarm.app.core.domain.model.FarmSetupConfig
import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.LandPartition
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.repository.AdminRepository
import com.smartkdfarm.app.core.domain.repository.FarmRepository
import kotlin.math.round

class SaveFarmSetupUseCase(
    private val adminRepository: AdminRepository,
    private val farmRepository: FarmRepository,
) {
    suspend operator fun invoke(
        actor: User,
        partitions: List<LandPartition>,
        notes: String? = null,
    ): FarmSetupConfig {
        if (actor.role != UserRole.ADMIN) {
            throw AuthorizationException("Only ADMIN can update farm setup.")
        }
        val farm = farmRepository.getFarmProfile(actor.farmId)
            ?: throw ValidationException("Farm profile not found for setup update.")
        if (partitions.isEmpty()) {
            throw ValidationException("At least one land partition is required.")
        }
        partitions.forEach { partition ->
            if (partition.farmId != actor.farmId) {
                throw ValidationException("Each land partition must belong to the admin farm.")
            }
            if (partition.name.isBlank()) {
                throw ValidationException("Partition name is required.")
            }
            if (partition.area.value <= 0.0) {
                throw ValidationException("Partition area must be greater than zero.")
            }
        }

        val totalAreaInAcres = farm.areaIn(AreaUnit.ACRE)
        val partitionAreaTotalInAcres = partitions.sumOf { it.area.convertTo(AreaUnit.ACRE) }
        val sumOfPartsValid = partitionAreaTotalInAcres <= totalAreaInAcres
        if (!sumOfPartsValid) {
            throw ValidationException("Sum of land partitions cannot exceed the total farm area.")
        }

        val now = TimeProvider.nowEpochMillis()
        partitions.forEach { partition ->
            val normalized = if (partition.id.isBlank()) {
                partition.copy(id = IdGenerator.newId(prefix = "partition"))
            } else {
                partition
            }
            adminRepository.upsertLandPartition(normalized)
        }

        val config = FarmSetupConfig(
            id = "setup",
            farmId = actor.farmId,
            totalArea = farm.landArea,
            partitionAreaTotalInAcres = round(partitionAreaTotalInAcres * 100.0) / 100.0,
            sumOfPartsValid = true,
            validationStatus = "VALID",
            notes = notes?.trim()?.takeIf { it.isNotEmpty() },
            updatedAtEpochMillis = now,
        )
        return adminRepository.upsertFarmSetupConfig(config)
    }
}
