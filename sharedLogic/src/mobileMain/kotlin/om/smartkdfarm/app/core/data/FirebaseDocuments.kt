package om.smartkdfarm.app.core.data

import kotlinx.serialization.Serializable
import om.smartkdfarm.app.core.domain.model.AreaConfiguration
import om.smartkdfarm.app.core.domain.model.FarmLocation
import om.smartkdfarm.app.core.domain.model.FarmProfile
import om.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import om.smartkdfarm.app.core.domain.model.ProvisioningStatus
import om.smartkdfarm.app.core.domain.model.User
import om.smartkdfarm.app.core.domain.model.UserProvisioningRequest
import om.smartkdfarm.app.core.domain.model.UserRole

@Serializable
internal data class UserDocument(
    val id: String,
    val farmId: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val role: UserRole,
    val isActive: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Serializable
internal data class FarmProfileDocument(
    val id: String,
    val farmName: String,
    val ownerName: String,
    val primaryPhoneNumber: String,
    val location: FarmLocation,
    val landArea: AreaConfiguration,
    val managerUserId: String? = null,
    val notes: String? = null,
    val isActive: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Serializable
internal data class UserProvisioningRequestDocument(
    val id: String,
    val farmId: String,
    val requestedByUserId: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val role: UserRole,
    val status: ProvisioningStatus,
    val isActive: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

internal fun User.toDocument(): UserDocument = UserDocument(
    id = id,
    farmId = farmId,
    fullName = fullName,
    email = email,
    phoneNumber = phoneNumber,
    role = role,
    isActive = isActive,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun UserDocument.toDomain(): User = User(
    id = id,
    farmId = farmId,
    fullName = fullName,
    email = email,
    phoneNumber = phoneNumber,
    role = role,
    isActive = isActive,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun FarmProfile.toDocument(): FarmProfileDocument = FarmProfileDocument(
    id = id,
    farmName = farmName,
    ownerName = ownerName,
    primaryPhoneNumber = primaryPhoneNumber,
    location = location,
    landArea = landArea,
    managerUserId = managerUserId,
    notes = notes,
    isActive = isActive,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun FarmProfileDocument.toDomain(): FarmProfile = FarmProfile(
    id = id,
    farmName = farmName,
    ownerName = ownerName,
    primaryPhoneNumber = primaryPhoneNumber,
    location = location,
    landArea = landArea,
    managerUserId = managerUserId,
    notes = notes,
    isActive = isActive,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun UserProvisioningRequest.toDocument(): UserProvisioningRequestDocument =
    UserProvisioningRequestDocument(
        id = id,
        farmId = farmId,
        requestedByUserId = requestedByUserId,
        fullName = fullName,
        email = email,
        phoneNumber = phoneNumber,
        role = role,
        status = status,
        isActive = isActive,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )

internal fun UserProvisioningRequestDocument.toDomain(): UserProvisioningRequest =
    UserProvisioningRequest(
        id = id,
        farmId = farmId,
        requestedByUserId = requestedByUserId,
        fullName = fullName,
        email = email,
        phoneNumber = phoneNumber,
        role = role,
        status = status,
        isActive = isActive,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )

internal fun ManagedUserRegistration.toProvisioningRequest(
    id: String,
    farmId: String,
    requestedByUserId: String,
    createdAtEpochMillis: Long,
): UserProvisioningRequest = UserProvisioningRequest(
    id = id,
    farmId = farmId,
    requestedByUserId = requestedByUserId,
    fullName = fullName.trim(),
    email = email.trim(),
    phoneNumber = phoneNumber.filter { it.isDigit() }.takeIf { it.isNotBlank() } ?: phoneNumber.trim(),
    role = role,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = createdAtEpochMillis,
)
