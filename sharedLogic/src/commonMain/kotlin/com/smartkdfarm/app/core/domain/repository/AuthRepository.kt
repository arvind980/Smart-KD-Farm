package com.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.smartkdfarm.app.core.domain.model.LoginCredentials
import com.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserProvisioningRequest

interface AuthRepository {
    fun observeAuthenticatedUser(): Flow<User?>

    fun currentAuthenticatedUser(): User?

    suspend fun refreshAuthenticatedUser(): User?

    suspend fun login(credentials: LoginCredentials): User

    suspend fun registerInitialAdmin(
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
