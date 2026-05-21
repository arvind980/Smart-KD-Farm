package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import com.smartkdfarm.app.core.domain.model.AnimalProfile
import com.smartkdfarm.app.core.domain.repository.LivestockRepository

class FirebaseLivestockRepository : LivestockRepository {
    private val firestore = Firebase.firestore
    private val cache = MutableStateFlow<Map<String, List<AnimalProfile>>>(emptyMap())

    override fun observeLivestock(farmId: String): Flow<List<AnimalProfile>> =
        cache.map { farmMap -> farmMap[farmId].orEmpty() }

    override suspend fun getLivestock(farmId: String): List<AnimalProfile> {
        val snapshot = livestockCollection(farmId).get()
        val animals = snapshot.documents.mapNotNull { document ->
            runCatching { document.data<AnimalProfileDocument>().toDomain() }.getOrNull()
        }.sortedBy { it.tagNumber }
        cache.update { it + (farmId to animals) }
        return animals
    }

    override suspend fun getAnimal(farmId: String, animalId: String): AnimalProfile? {
        cache.value[farmId]?.firstOrNull { it.id == animalId }?.let { return it }
        val snapshot = livestockCollection(farmId).document(animalId).get()
        val animal = runCatching { snapshot.data<AnimalProfileDocument>().toDomain() }.getOrNull()
        if (animal != null) {
            cache.update { current ->
                val updatedList = current[farmId].orEmpty()
                    .filterNot { it.id == animal.id } + animal
                current + (farmId to updatedList.sortedBy { it.tagNumber })
            }
        }
        return animal
    }

    override suspend fun upsertAnimal(profile: AnimalProfile): AnimalProfile {
        livestockCollection(profile.farmId).document(profile.id).set(profile.toDocument())
        cache.update { current ->
            val updated = current[profile.farmId].orEmpty()
                .filterNot { it.id == profile.id } + profile
            current + (profile.farmId to updated.sortedBy { it.tagNumber })
        }
        return profile
    }

    override suspend fun archiveAnimal(farmId: String, profile: AnimalProfile): AnimalProfile {
        livestockCollection(farmId).document(profile.id).set(profile.toDocument())
        cache.update { current ->
            val updated = current[farmId].orEmpty()
                .filterNot { it.id == profile.id } + profile
            current + (farmId to updated.sortedBy { it.tagNumber })
        }
        return profile
    }

    private fun livestockCollection(farmId: String): CollectionReference =
        firestore.collection(FARMS_COLLECTION).document(farmId).collection(LIVESTOCK_COLLECTION)

    private companion object {
        const val FARMS_COLLECTION = "farms"
        const val LIVESTOCK_COLLECTION = "livestock"
    }
}
