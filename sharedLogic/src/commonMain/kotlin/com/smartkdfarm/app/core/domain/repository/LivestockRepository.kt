package com.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.smartkdfarm.app.core.domain.model.AnimalProfile

interface LivestockRepository {
    fun observeLivestock(farmId: String): Flow<List<AnimalProfile>>

    suspend fun getLivestock(farmId: String): List<AnimalProfile>

    suspend fun getAnimal(farmId: String, animalId: String): AnimalProfile?

    suspend fun upsertAnimal(profile: AnimalProfile): AnimalProfile

    suspend fun archiveAnimal(farmId: String, profile: AnimalProfile): AnimalProfile
}
