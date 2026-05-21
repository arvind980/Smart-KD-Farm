package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.ValidationException
import com.smartkdfarm.app.core.domain.model.FarmProfile
import com.smartkdfarm.app.core.domain.model.FarmRegistrationCommand
import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.repository.AuthRepository
import com.smartkdfarm.app.core.domain.repository.FarmRepository

class RegisterFarmUseCase(
    private val authRepository: AuthRepository,
    private val farmRepository: FarmRepository,
) {
    suspend operator fun invoke(command: FarmRegistrationCommand): FarmProfile {
        validate(command)

        val now = TimeProvider.nowEpochMillis()
        val farmId = IdGenerator.newId(prefix = "farm")
        val draftProfile = FarmProfile(
            id = farmId,
            farmName = command.farmName.trim(),
            ownerName = command.ownerName.trim(),
            primaryPhoneNumber = command.primaryPhoneNumber.trim(),
            location = command.location,
            landArea = command.landArea,
            notes = command.notes?.trim()?.takeIf { it.isNotEmpty() },
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now,
        )

        val storedFarm = farmRepository.upsertFarmProfile(draftProfile)

        return try {
            val manager = authRepository.registerInitialManager(
                farmId = storedFarm.id,
                fullName = command.managerName.trim(),
                email = command.managerEmail.trim(),
                password = command.managerPassword,
                phoneNumber = command.managerPhoneNumber.trim(),
            )
            farmRepository.upsertFarmProfile(
                storedFarm.copy(
                    managerUserId = manager.id,
                    updatedAtEpochMillis = TimeProvider.nowEpochMillis(),
                )
            )
        } catch (throwable: Throwable) {
            farmRepository.softDeactivateFarm(storedFarm.id)
            throw throwable
        }
    }

    private fun validate(command: FarmRegistrationCommand) {
        if (command.farmName.isBlank()) {
            throw ValidationException("Farm name is required.")
        }
        if (command.managerName.isBlank()) {
            throw ValidationException("Primary manager name is required.")
        }
        if (!command.managerEmail.contains("@")) {
            throw ValidationException("A valid manager email is required.")
        }
        if (command.managerPassword.length < 8) {
            throw ValidationException("Manager password must be at least 8 characters.")
        }
        if (command.landArea.value <= 0.0) {
            throw ValidationException("Farm area must be greater than zero.")
        }
        if (command.location.village.isBlank() || command.location.district.isBlank()) {
            throw ValidationException("Farm location must include village and district.")
        }
    }
}
