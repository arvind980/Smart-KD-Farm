package com.smartkdfarm.app.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AnimalStatus {
    MILKING,
    DRY,
    PREGNANT,
    SICK,
}

@Serializable
enum class BreedingOutcome {
    SCHEDULED,
    CONFIRMED_PREGNANT,
    REPEAT_REQUIRED,
}

@Serializable
enum class HealthSeverity {
    STABLE,
    OBSERVATION,
    CRITICAL,
}

@Serializable
enum class AnimalArchiveReason {
    SOLD,
    NATURAL_DEATH,
    TRANSFERRED,
    OTHER,
}

@Serializable
data class BreedingLog(
    val id: String,
    val aiDateEpochMillis: Long,
    val pregnancyDiagnosisDueEpochMillis: Long,
    val expectedCalvingEpochMillis: Long,
    val technicianName: String? = null,
    val notes: String? = null,
    val outcome: BreedingOutcome = BreedingOutcome.SCHEDULED,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

@Serializable
data class HealthLog(
    val id: String,
    val recordedAtEpochMillis: Long,
    val title: String,
    val notes: String? = null,
    val treatment: String? = null,
    val severity: HealthSeverity = HealthSeverity.OBSERVATION,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)

@Serializable
data class AnimalProfile(
    val id: String,
    val farmId: String,
    val tagNumber: String,
    val breed: String,
    val ageInMonths: Int,
    val purchasePrice: Double,
    val status: AnimalStatus,
    val breedingLogs: List<BreedingLog> = emptyList(),
    val healthLogs: List<HealthLog> = emptyList(),
    val isActive: Boolean = true,
    val archivedAtEpochMillis: Long? = null,
    val archiveReason: AnimalArchiveReason? = null,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
    val updatedAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)
