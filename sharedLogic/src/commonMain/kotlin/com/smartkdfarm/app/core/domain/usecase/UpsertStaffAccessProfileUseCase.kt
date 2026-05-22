package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.AuthorizationException
import com.smartkdfarm.app.core.domain.exception.ValidationException
import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.StaffAccessProfile
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.repository.AdminRepository

class UpsertStaffAccessProfileUseCase(
    private val adminRepository: AdminRepository,
) {
    suspend operator fun invoke(
        actor: User,
        profile: StaffAccessProfile,
    ): StaffAccessProfile {
        if (actor.role != UserRole.ADMIN) {
            throw AuthorizationException("Only ADMIN can configure staff access.")
        }
        if (profile.farmId != actor.farmId) {
            throw AuthorizationException("Staff access can only be configured within the actor farm.")
        }
        if (profile.fullName.isBlank()) {
            throw ValidationException("Staff full name is required.")
        }
        if (profile.phoneNumber.isBlank()) {
            throw ValidationException("Staff phone number is required.")
        }
        if (profile.permissions.isEmpty()) {
            throw ValidationException("At least one module permission must be assigned.")
        }
        val now = TimeProvider.nowEpochMillis()
        val normalized = profile.copy(
            id = profile.id.ifBlank { IdGenerator.newId(prefix = "staff") },
            updatedAtEpochMillis = now,
        )
        return adminRepository.upsertStaffProfile(normalized)
    }
}
