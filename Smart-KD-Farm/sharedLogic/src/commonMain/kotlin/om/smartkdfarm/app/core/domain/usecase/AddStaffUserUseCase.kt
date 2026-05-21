package om.smartkdfarm.app.core.domain.usecase

import om.smartkdfarm.app.core.domain.exception.AuthorizationException
import om.smartkdfarm.app.core.domain.exception.ValidationException
import om.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import om.smartkdfarm.app.core.domain.model.User
import om.smartkdfarm.app.core.domain.model.UserProvisioningRequest
import om.smartkdfarm.app.core.domain.model.UserRole
import om.smartkdfarm.app.core.domain.repository.AuthRepository

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
                if (actor.role != UserRole.MANAGER && actor.role != UserRole.DAIRY_MAN) {
                    throw AuthorizationException(
                        "Only MANAGER or DAIRY_MAN can register a FARMER."
                    )
                }
            }

            UserRole.MANAGER,
            UserRole.DAIRY_MAN,
            UserRole.LABOUR,
            -> {
                if (actor.role != UserRole.MANAGER) {
                    throw AuthorizationException("Only MANAGER can add staff users.")
                }
            }
        }
    }
}
