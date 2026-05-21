package om.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import om.smartkdfarm.app.core.domain.model.LoginCredentials
import om.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import om.smartkdfarm.app.core.domain.model.User
import om.smartkdfarm.app.core.domain.model.UserProvisioningRequest

interface AuthRepository {
    fun observeAuthenticatedUser(): Flow<User?>

    suspend fun refreshAuthenticatedUser(): User?

    suspend fun login(credentials: LoginCredentials): User

    suspend fun registerInitialManager(
        farmId: String,
        fullName: String,
        email: String,
        password: String,
        phoneNumber: String,
    ): User

    suspend fun createManagedUser(
        actor: User,
        registration: ManagedUserRegistration,
    ): UserProvisioningRequest

    suspend fun logout()
}
