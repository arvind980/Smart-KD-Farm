package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.NotFoundException
import com.smartkdfarm.app.core.domain.model.AnimalProfile
import com.smartkdfarm.app.core.domain.model.AnimalStatus
import com.smartkdfarm.app.core.domain.model.BreedingLog
import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.repository.LivestockRepository

class LogInseminationUseCase(
    private val livestockRepository: LivestockRepository,
) {
    suspend operator fun invoke(
        farmId: String,
        animalId: String,
        aiDateEpochMillis: Long,
        technicianName: String? = null,
        notes: String? = null,
    ): AnimalProfile {
        val animal = livestockRepository.getAnimal(farmId, animalId)
            ?: throw NotFoundException("No livestock profile found for animalId=$animalId.")

        require(animal.isActive) { "Only active animals can be updated with insemination logs." }

        val breedingPlan = resolveBreedingPlan(animal)
        val entry = BreedingLog(
            id = IdGenerator.newId(prefix = "breeding"),
            aiDateEpochMillis = aiDateEpochMillis,
            pregnancyDiagnosisDueEpochMillis = aiDateEpochMillis + breedingPlan.diagnosisDays * MILLIS_PER_DAY,
            expectedCalvingEpochMillis = aiDateEpochMillis + breedingPlan.calvingDays * MILLIS_PER_DAY,
            technicianName = technicianName?.trim()?.takeIf { it.isNotBlank() },
            notes = notes?.trim()?.takeIf { it.isNotBlank() },
        )

        val updatedAnimal = animal.copy(
            status = if (animal.status == AnimalStatus.SICK) AnimalStatus.SICK else animal.status,
            breedingLogs = animal.breedingLogs + entry,
            updatedAtEpochMillis = TimeProvider.nowEpochMillis(),
        )
        return livestockRepository.upsertAnimal(updatedAnimal)
    }

    private fun resolveBreedingPlan(animal: AnimalProfile): BreedingPlan {
        val normalizedBreed = animal.breed.lowercase()
        val buffaloBreeds = listOf("buffalo", "murrah", "mehsana", "nili", "jafarabadi", "banni")
        val isBuffaloFamily = buffaloBreeds.any { normalizedBreed.contains(it) }

        val diagnosisBase = if (isBuffaloFamily) 90L else 60L
        val calvingBase = if (isBuffaloFamily) 310L else 280L
        val ageAdjustment = when {
            animal.ageInMonths <= 30 -> -3L
            animal.ageInMonths >= 96 -> 4L
            else -> 0L
        }
        val healthAdjustment = if (animal.status == AnimalStatus.SICK) 7L else 0L

        return BreedingPlan(
            diagnosisDays = diagnosisBase + healthAdjustment,
            calvingDays = calvingBase + ageAdjustment,
        )
    }

    private data class BreedingPlan(
        val diagnosisDays: Long,
        val calvingDays: Long,
    )

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
