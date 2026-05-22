package com.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.smartkdfarm.app.core.domain.model.FarmSetupConfig
import com.smartkdfarm.app.core.domain.model.LandPartition
import com.smartkdfarm.app.core.domain.model.StaffAccessProfile

interface AdminRepository {
    fun observeStaffProfiles(farmId: String): Flow<List<StaffAccessProfile>>

    suspend fun getStaffProfiles(farmId: String): List<StaffAccessProfile>

    suspend fun upsertStaffProfile(profile: StaffAccessProfile): StaffAccessProfile

    suspend fun deactivateStaffProfile(
        farmId: String,
        staffId: String,
        updatedAtEpochMillis: Long,
    )

    fun observeFarmSetupConfig(farmId: String): Flow<FarmSetupConfig?>

    suspend fun getFarmSetupConfig(farmId: String): FarmSetupConfig?

    suspend fun upsertFarmSetupConfig(config: FarmSetupConfig): FarmSetupConfig

    fun observeLandPartitions(farmId: String): Flow<List<LandPartition>>

    suspend fun getLandPartitions(farmId: String): List<LandPartition>

    suspend fun upsertLandPartition(partition: LandPartition): LandPartition
}
