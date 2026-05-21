package com.smartkdfarm.app.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentials(
    val mobileNumber: String,
    val password: String,
)

@Serializable
data class FarmRegistrationCommand(
    val farmName: String,
    val ownerName: String,
    val primaryPhoneNumber: String,
    val managerName: String,
    val managerEmail: String,
    val managerPassword: String,
    val managerPhoneNumber: String,
    val location: FarmLocation,
    val landArea: AreaConfiguration,
    val notes: String? = null,
)

@Serializable
data class ManagedUserRegistration(
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val role: UserRole,
)

@Serializable
enum class ProvisioningStatus {
    PENDING,
    COMPLETED,
    REJECTED,
}

@Serializable
data class UserProvisioningRequest(
    val id: String,
    val farmId: String,
    val requestedByUserId: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val role: UserRole,
    val status: ProvisioningStatus = ProvisioningStatus.PENDING,
    val isActive: Boolean = true,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
    val updatedAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)
