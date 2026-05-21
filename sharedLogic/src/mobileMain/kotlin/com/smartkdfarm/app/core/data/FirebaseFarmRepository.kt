package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.model.FarmProfile
import com.smartkdfarm.app.core.domain.repository.FarmRepository

class FirebaseFarmRepository : FarmRepository {
    private val firestore = Firebase.firestore
    private val cache = MutableStateFlow<Map<String, FarmProfile?>>(emptyMap())

    override fun observeFarmProfile(farmId: String): Flow<FarmProfile?> =
        cache.map { profiles -> profiles[farmId] }

    override suspend fun getFarmProfile(farmId: String): FarmProfile? {
        val snapshot = farmsCollection().document(farmId).get()
        val profile = runCatching { snapshot.data<FarmProfileDocument>().toDomain() }.getOrNull()
        cache.value = cache.value + (farmId to profile)
        return profile
    }

    override suspend fun upsertFarmProfile(profile: FarmProfile): FarmProfile {
        farmsCollection().document(profile.id).set(profile.toDocument())
        cache.value = cache.value + (profile.id to profile)
        return profile
    }

    override suspend fun softDeactivateFarm(farmId: String) {
        val existing = getFarmProfile(farmId) ?: return
        val deactivated = existing.copy(
            isActive = false,
            updatedAtEpochMillis = TimeProvider.nowEpochMillis(),
        )
        farmsCollection().document(farmId).set(deactivated.toDocument())
        cache.value = cache.value + (farmId to deactivated)
    }

    private fun farmsCollection() = firestore.collection(FARMS_COLLECTION)

    private companion object {
        const val FARMS_COLLECTION = "farms"
    }
}
