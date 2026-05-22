package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.model.AnimalStatus
import com.smartkdfarm.app.core.domain.model.BreedingOutcome
import com.smartkdfarm.app.core.domain.model.HealthSeverity
import com.smartkdfarm.app.core.domain.model.PredictionPoint
import com.smartkdfarm.app.core.domain.model.PredictionSource
import com.smartkdfarm.app.core.domain.repository.AnalyticsRepository
import com.smartkdfarm.app.core.domain.repository.LivestockRepository
import kotlin.math.round

class GenerateSupplyPredictionUseCase(
    private val livestockRepository: LivestockRepository,
    private val analyticsRepository: AnalyticsRepository,
) {
    suspend operator fun invoke(farmId: String): List<PredictionPoint> {
        val animals = livestockRepository.getLivestock(farmId).filter { it.isActive }
        val milking = animals.count { it.status == AnimalStatus.MILKING }
        val pregnant = animals.count { it.status == AnimalStatus.PREGNANT }
        val dry = animals.count { it.status == AnimalStatus.DRY }
        val confirmedPregnancies = animals.sumOf { animal ->
            animal.breedingLogs.count { it.outcome == BreedingOutcome.CONFIRMED_PREGNANT }
        }
        val repeatRequired = animals.sumOf { animal ->
            animal.breedingLogs.count { it.outcome == BreedingOutcome.REPEAT_REQUIRED }
        }
        val criticalHealth = animals.sumOf { animal ->
            animal.healthLogs.count { it.severity == HealthSeverity.CRITICAL }
        }
        val observationHealth = animals.sumOf { animal ->
            animal.healthLogs.count { it.severity == HealthSeverity.OBSERVATION }
        }
        val breedingConfidenceBoost = confirmedPregnancies * 2.2
        val repeatPenalty = repeatRequired * 1.5
        val healthPenalty = criticalHealth * 3.0 + observationHealth * 1.1
        val base = milking * 18.0 + pregnant * 6.0 + dry * 2.5 + breedingConfidenceBoost - repeatPenalty - healthPenalty
        val points = listOf(
            buildPoint(0, "Now", base),
            buildPoint(1, "M+1", base + pregnant * 4.0 + confirmedPregnancies * 1.5 - healthPenalty * 0.2),
            buildPoint(2, "M+2", base + pregnant * 7.0 + confirmedPregnancies * 2.8 - healthPenalty * 0.15),
            buildPoint(3, "M+3", base + pregnant * 10.0 + confirmedPregnancies * 4.0 - healthPenalty * 0.1),
        )
        analyticsRepository.savePredictions(farmId, points)
        return points
    }

    private fun buildPoint(monthIndex: Int, label: String, liters: Double): PredictionPoint {
        val roundedLiters = round(liters * 10.0) / 10.0
        return PredictionPoint(
            monthIndex = monthIndex,
            label = label,
            projectedMilkLiters = roundedLiters,
            projectedRevenue = round(roundedLiters * 48.0 * 100.0) / 100.0,
            source = PredictionSource.SUPPLY_DEAL,
        )
    }
}
