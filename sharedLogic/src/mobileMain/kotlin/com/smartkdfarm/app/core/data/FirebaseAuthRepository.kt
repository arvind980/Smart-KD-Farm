package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.smartkdfarm.app.core.domain.exception.AuthenticationException
import com.smartkdfarm.app.core.domain.exception.NotFoundException
import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.LoginCredentials
import com.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserProvisioningRequest
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.repository.AuthRepository

class FirebaseAuthRepository : AuthRepository {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val sessionState = MutableStateFlow<User?>(null)

    override fun observeAuthenticatedUser(): Flow<User?> = sessionState.asStateFlow()

    override suspend fun refreshAuthenticatedUser(): User? {
        val currentUid = auth.currentUser?.uid ?: return null.also { sessionState.value = null }
        val profile = fetchUserProfile(currentUid)
        sessionState.value = profile
        return profile
    }

    override suspend fun login(credentials: LoginCredentials): User {
        val mobileNumber = normalizePhoneNumber(credentials.mobileNumber)
        val email = resolveEmailForMobile(mobileNumber)
        try {
            auth.signInWithEmailAndPassword(
                email = email,
                password = credentials.password,
            )
        } catch (throwable: Throwable) {
            throw AuthenticationException(
                message = throwable.message ?: "Unable to sign in with Firebase Auth."
            )
        }

        val user = refreshAuthenticatedUser()
            ?: throw AuthenticationException("Login succeeded but no active user profile was found.")
        if (!user.isActive) {
            auth.signOut()
            throw AuthenticationException("This account has been deactivated.")
        }
        return user
    }

    override suspend fun registerInitialManager(
        farmId: String,
        fullName: String,
        email: String,
        password: String,
        phoneNumber: String,
    ): User {
        val authResult = try {
            auth.createUserWithEmailAndPassword(
                email = email.trim(),
                password = password,
            )
        } catch (throwable: Throwable) {
            throw AuthenticationException(
                message = throwable.message ?: "Unable to create the manager Firebase Auth account."
            )
        }

        val userId = authResult.user?.uid ?: auth.currentUser?.uid
            ?: throw AuthenticationException("Firebase Auth did not return a manager uid.")
        val now = TimeProvider.nowEpochMillis()
        val user = User(
            id = userId,
            farmId = farmId,
            fullName = fullName.trim(),
            email = email.trim(),
            phoneNumber = normalizePhoneNumber(phoneNumber),
            role = UserRole.MANAGER,
            isActive = true,
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now,
        )

        try {
            usersCollection().document(user.id).set(user.toDocument())
        } catch (throwable: Throwable) {
            throw AuthenticationException(
                message = throwable.message ?: "Manager account was created, but the user profile could not be stored."
            )
        }

        sessionState.value = user
        return user
    }

    override suspend fun createManagedUser(
        actor: User,
        registration: ManagedUserRegistration,
    ): UserProvisioningRequest {
        val now = TimeProvider.nowEpochMillis()
        val request = registration.toProvisioningRequest(
            id = IdGenerator.newId(prefix = "provision"),
            farmId = actor.farmId,
            requestedByUserId = actor.id,
            createdAtEpochMillis = now,
        )

        provisioningRequestsCollection().document(request.id).set(request.toDocument())
        return request
    }

    override suspend fun logout() {
        auth.signOut()
        sessionState.value = null
    }

    private suspend fun fetchUserProfile(userId: String): User {
        val snapshot = usersCollection().document(userId).get()
        return runCatching { snapshot.data<UserDocument>() }
            .getOrElse { throw NotFoundException("No user profile exists for uid=$userId.") }
            .toDomain()
    }

    private suspend fun resolveEmailForMobile(mobileNumber: String): String {
        val querySnapshot = usersCollection()
            .where { "phoneNumber" equalTo mobileNumber }
            .get()

        val matchingUser = querySnapshot.documents
            .firstOrNull()
            ?.data<UserDocument>()
            ?: throw AuthenticationException("No active account was found for this mobile number.")

        if (!matchingUser.isActive) {
            throw AuthenticationException("This account has been deactivated.")
        }
        return matchingUser.email
    }

    private fun normalizePhoneNumber(input: String): String =
        input.filter { it.isDigit() }
            .takeIf { it.isNotBlank() }
            ?: input.trim()

    private fun usersCollection() = firestore.collection(USERS_COLLECTION)

    private fun provisioningRequestsCollection() = firestore.collection(PROVISIONING_COLLECTION)

    private companion object {
        const val USERS_COLLECTION = "users"
        const val PROVISIONING_COLLECTION = "user_provisioning_requests"
    }
}
