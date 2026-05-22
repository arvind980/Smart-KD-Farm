package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.AuthorizationException
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.repository.AdminRepository

class DeactivateStaffAccessProfileUseCase(
    private val adminRepository: AdminRepository,
) {
    suspend operator fun invoke(
        actor: User,
        staffId: String,
    ) {
        if (actor.role != UserRole.ADMIN) {
            throw AuthorizationException("Only ADMIN can deactivate staff access.")
        }
        adminRepository.deactivateStaffProfile(
            farmId = actor.farmId,
            staffId = staffId,
            updatedAtEpochMillis = TimeProvider.nowEpochMillis(),
        )
    }
}
