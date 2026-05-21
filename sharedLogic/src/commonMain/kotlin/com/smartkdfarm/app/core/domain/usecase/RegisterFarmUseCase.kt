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

        return try {
            val admin = authRepository.registerInitialAdmin(
                farmId = farmId,
                fullName = command.adminName.trim(),
                email = command.adminEmail.trim(),
                password = command.adminPassword,
                phoneNumber = command.adminPhoneNumber.trim(),
            )
            farmRepository.upsertFarmProfile(
                FarmProfile(
                    id = farmId,
                    farmName = command.farmName.trim(),
                    primaryPhoneNumber = command.adminPhoneNumber.trim(),
                    location = command.location,
                    landArea = command.landArea,
                    adminUserId = admin.id,
                    notes = command.notes?.trim()?.takeIf { it.isNotEmpty() },
                    createdAtEpochMillis = now,
                    updatedAtEpochMillis = TimeProvider.nowEpochMillis(),
                )
            )
        } catch (throwable: Throwable) {
            runCatching { farmRepository.softDeactivateFarm(farmId) }
            throw throwable
        }
    }

    private fun validate(command: FarmRegistrationCommand) {
        if (command.farmName.isBlank()) {
            throw ValidationException("Farm name is required.")
        }
        if (command.adminName.isBlank()) {
            throw ValidationException("Admin name is required.")
        }
        if (command.adminPassword.length < 8) {
            throw ValidationException("Admin password must be at least 8 characters.")
        }
        if (command.landArea.value <= 0.0) {
            throw ValidationException("Farm area must be greater than zero.")
        }
        if (command.location.village.isBlank() || command.location.district.isBlank()) {
            throw ValidationException("Farm location must include village and district.")
        }
    }
}
