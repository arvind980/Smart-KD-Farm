package com.smartkdfarm.app.core.data

import kotlinx.serialization.Serializable
import com.smartkdfarm.app.core.domain.model.AnimalArchiveReason
import com.smartkdfarm.app.core.domain.model.AnimalProfile
import com.smartkdfarm.app.core.domain.model.AnimalStatus
import com.smartkdfarm.app.core.domain.model.AreaConfiguration
import com.smartkdfarm.app.core.domain.model.BreedingLog
import com.smartkdfarm.app.core.domain.model.BreedingOutcome
import com.smartkdfarm.app.core.domain.model.FarmLocation
import com.smartkdfarm.app.core.domain.model.FarmProfile
import com.smartkdfarm.app.core.domain.model.HealthLog
import com.smartkdfarm.app.core.domain.model.HealthSeverity
import com.smartkdfarm.app.core.domain.model.KhataDirection
import com.smartkdfarm.app.core.domain.model.KhataTransaction
import com.smartkdfarm.app.core.domain.model.KhataTransactionCategory
import com.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import com.smartkdfarm.app.core.domain.model.ProvisioningStatus
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserProvisioningRequest
import com.smartkdfarm.app.core.domain.model.UserRole

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
    val primaryPhoneNumber: String,
    val location: FarmLocation,
    val landArea: AreaConfiguration,
    val adminUserId: String? = null,
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

@Serializable
internal data class BreedingLogDocument(
    val id: String,
    val aiDateEpochMillis: Long,
    val pregnancyDiagnosisDueEpochMillis: Long,
    val expectedCalvingEpochMillis: Long,
    val technicianName: String? = null,
    val notes: String? = null,
    val outcome: BreedingOutcome,
    val createdAtEpochMillis: Long,
)

@Serializable
internal data class HealthLogDocument(
    val id: String,
    val recordedAtEpochMillis: Long,
    val title: String,
    val notes: String? = null,
    val treatment: String? = null,
    val severity: HealthSeverity,
    val createdAtEpochMillis: Long,
)

@Serializable
internal data class AnimalProfileDocument(
    val id: String,
    val farmId: String,
    val tagNumber: String,
    val breed: String,
    val ageInMonths: Int,
    val purchasePrice: Double,
    val status: AnimalStatus,
    val breedingLogs: List<BreedingLogDocument>,
    val healthLogs: List<HealthLogDocument>,
    val isActive: Boolean,
    val archivedAtEpochMillis: Long? = null,
    val archiveReason: AnimalArchiveReason? = null,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Serializable
internal data class KhataTransactionDocument(
    val id: String,
    val farmId: String,
    val animalId: String,
    val animalTagNumber: String,
    val direction: KhataDirection,
    val category: KhataTransactionCategory,
    val amount: Double,
    val notes: String,
    val occurredAtEpochMillis: Long,
    val createdAtEpochMillis: Long,
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
    primaryPhoneNumber = primaryPhoneNumber,
    location = location,
    landArea = landArea,
    adminUserId = adminUserId,
    notes = notes,
    isActive = isActive,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun FarmProfileDocument.toDomain(): FarmProfile = FarmProfile(
    id = id,
    farmName = farmName,
    primaryPhoneNumber = primaryPhoneNumber,
    location = location,
    landArea = landArea,
    adminUserId = adminUserId,
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

internal fun BreedingLog.toDocument(): BreedingLogDocument = BreedingLogDocument(
    id = id,
    aiDateEpochMillis = aiDateEpochMillis,
    pregnancyDiagnosisDueEpochMillis = pregnancyDiagnosisDueEpochMillis,
    expectedCalvingEpochMillis = expectedCalvingEpochMillis,
    technicianName = technicianName,
    notes = notes,
    outcome = outcome,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun BreedingLogDocument.toDomain(): BreedingLog = BreedingLog(
    id = id,
    aiDateEpochMillis = aiDateEpochMillis,
    pregnancyDiagnosisDueEpochMillis = pregnancyDiagnosisDueEpochMillis,
    expectedCalvingEpochMillis = expectedCalvingEpochMillis,
    technicianName = technicianName,
    notes = notes,
    outcome = outcome,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun HealthLog.toDocument(): HealthLogDocument = HealthLogDocument(
    id = id,
    recordedAtEpochMillis = recordedAtEpochMillis,
    title = title,
    notes = notes,
    treatment = treatment,
    severity = severity,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun HealthLogDocument.toDomain(): HealthLog = HealthLog(
    id = id,
    recordedAtEpochMillis = recordedAtEpochMillis,
    title = title,
    notes = notes,
    treatment = treatment,
    severity = severity,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun AnimalProfile.toDocument(): AnimalProfileDocument = AnimalProfileDocument(
    id = id,
    farmId = farmId,
    tagNumber = tagNumber,
    breed = breed,
    ageInMonths = ageInMonths,
    purchasePrice = purchasePrice,
    status = status,
    breedingLogs = breedingLogs.map(BreedingLog::toDocument),
    healthLogs = healthLogs.map(HealthLog::toDocument),
    isActive = isActive,
    archivedAtEpochMillis = archivedAtEpochMillis,
    archiveReason = archiveReason,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun AnimalProfileDocument.toDomain(): AnimalProfile = AnimalProfile(
    id = id,
    farmId = farmId,
    tagNumber = tagNumber,
    breed = breed,
    ageInMonths = ageInMonths,
    purchasePrice = purchasePrice,
    status = status,
    breedingLogs = breedingLogs.map(BreedingLogDocument::toDomain),
    healthLogs = healthLogs.map(HealthLogDocument::toDomain),
    isActive = isActive,
    archivedAtEpochMillis = archivedAtEpochMillis,
    archiveReason = archiveReason,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun KhataTransaction.toDocument(): KhataTransactionDocument = KhataTransactionDocument(
    id = id,
    farmId = farmId,
    animalId = animalId,
    animalTagNumber = animalTagNumber,
    direction = direction,
    category = category,
    amount = amount,
    notes = notes,
    occurredAtEpochMillis = occurredAtEpochMillis,
    createdAtEpochMillis = createdAtEpochMillis,
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
