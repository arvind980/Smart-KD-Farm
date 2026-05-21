package om.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import om.smartkdfarm.app.core.domain.model.FarmProfile

interface FarmRepository {
    fun observeFarmProfile(farmId: String): Flow<FarmProfile?>

    suspend fun getFarmProfile(farmId: String): FarmProfile?

    suspend fun upsertFarmProfile(profile: FarmProfile): FarmProfile

    suspend fun softDeactivateFarm(farmId: String)
}
