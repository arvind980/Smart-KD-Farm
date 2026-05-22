package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.ValidationException
import com.smartkdfarm.app.core.domain.model.HealthLog
import com.smartkdfarm.app.core.domain.model.HealthSeverity
import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.PermissionChecker
import com.smartkdfarm.app.core.domain.model.StaffModule
import com.smartkdfarm.app.core.domain.model.PermissionLevel
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.repository.LivestockRepository

class LogHealthEventUseCase(
    private val livestockRepository: LivestockRepository,
) {
    /**
     * Appends a [HealthLog] entry to the specified animal's profile.
     *
     * Required permission: LIVESTOCK → MANAGE
     * Allowed roles (default): ADMIN, DAIRY_MAN, LABOUR
     *
     * @param actor      Authenticated user performing the action
     * @param farmId     Farm owning the animal
     * @param animalId   Target animal's Firestore document ID
     * @param title      Short event title (e.g. "Foot-and-Mouth check")
     * @param notes      Optional clinical notes
     * @param treatment  Optional treatment administered
     * @param severity   Health severity level (default: OBSERVATION)
     */
    suspend operator fun invoke(
        actor: User,
        farmId: String,
        animalId: String,
        title: String,
        notes: String? = null,
        treatment: String? = null,
        severity: HealthSeverity = HealthSeverity.OBSERVATION,
    ) {
        PermissionChecker.requireAccess(actor, StaffModule.LIVESTOCK, PermissionLevel.MANAGE)
        if (title.isBlank()) throw ValidationException("Health event title is required.")

        val animals = livestockRepository.getLivestock(farmId)
        val animal = animals.firstOrNull { it.id == animalId }
            ?: throw ValidationException("Animal not found: $animalId")

        val newLog = HealthLog(
            id = IdGenerator.newId(prefix = "health"),
            recordedAtEpochMillis = TimeProvider.nowEpochMillis(),
            title = title.trim(),
            notes = notes?.trim(),
            treatment = treatment?.trim(),
            severity = severity,
        )

        val updated = animal.copy(
            healthLogs = animal.healthLogs + newLog,
            updatedAtEpochMillis = TimeProvider.nowEpochMillis(),
        )
        livestockRepository.upsertAnimal(updated)
    }
}
