package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.AuthorizationException
import com.smartkdfarm.app.core.domain.exception.ValidationException
import com.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserProvisioningRequest
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.repository.AuthRepository

class AddStaffUserUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        actor: User,
        registration: ManagedUserRegistration,
    ): UserProvisioningRequest {
        validate(actor, registration)
        return authRepository.createManagedUser(actor, registration)
    }

    private fun validate(actor: User, registration: ManagedUserRegistration) {
        if (registration.fullName.isBlank()) {
            throw ValidationException("Staff full name is required.")
        }
        if (!registration.email.contains("@")) {
            throw ValidationException("A valid staff email is required.")
        }
        if (registration.phoneNumber.isBlank()) {
            throw ValidationException("A phone number is required.")
        }

        when (registration.role) {
            UserRole.FARMER -> {
                if (actor.role != UserRole.ADMIN && actor.role != UserRole.DAIRY_MAN) {
                    throw AuthorizationException(
                        "Only ADMIN or DAIRY_MAN can register a FARMER."
                    )
                }
            }

            UserRole.ADMIN,
            UserRole.DAIRY_MAN,
            UserRole.LABOUR,
            -> {
                if (actor.role != UserRole.ADMIN) {
                    throw AuthorizationException("Only ADMIN can add staff users.")
                }
            }
        }
    }
}
