package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import com.smartkdfarm.app.core.domain.exception.NotFoundException
import com.smartkdfarm.app.core.domain.model.FarmSetupConfig
import com.smartkdfarm.app.core.domain.model.LandPartition
import com.smartkdfarm.app.core.domain.model.StaffAccessProfile
import com.smartkdfarm.app.core.domain.repository.AdminRepository

class FirebaseAdminRepository : AdminRepository {
    private val firestore = Firebase.firestore
    private val staffCache = MutableStateFlow<Map<String, List<StaffAccessProfile>>>(emptyMap())
    private val configCache = MutableStateFlow<Map<String, FarmSetupConfig?>>(emptyMap())
    private val partitionCache = MutableStateFlow<Map<String, List<LandPartition>>>(emptyMap())

    override fun observeStaffProfiles(farmId: String): Flow<List<StaffAccessProfile>> =
        staffCache.map { it[farmId].orEmpty() }

    override suspend fun getStaffProfiles(farmId: String): List<StaffAccessProfile> {
        val snapshot = staffCollection(farmId).get()
        val profiles = snapshot.documents.mapNotNull { document ->
            runCatching { document.data<StaffAccessProfileDocument>().toDomain() }.getOrNull()
        }.sortedBy { it.fullName }
        staffCache.update { it + (farmId to profiles) }
        return profiles
    }

    override suspend fun upsertStaffProfile(profile: StaffAccessProfile): StaffAccessProfile {
        staffCollection(profile.farmId).document(profile.id).set(profile.toDocument())
        staffCache.update { current ->
            val updated = current[profile.farmId].orEmpty()
                .filterNot { it.id == profile.id } + profile
            current + (profile.farmId to updated.sortedBy { it.fullName })
        }
        return profile
    }

    override suspend fun deactivateStaffProfile(
        farmId: String,
        staffId: String,
        updatedAtEpochMillis: Long,
    ) {
        val existing = getStaffProfiles(farmId).firstOrNull { it.id == staffId }
            ?: throw NotFoundException("Staff profile not found for id=$staffId")
        upsertStaffProfile(
            existing.copy(
                isActive = false,
                updatedAtEpochMillis = updatedAtEpochMillis,
            )
        )
    }

    override fun observeFarmSetupConfig(farmId: String): Flow<FarmSetupConfig?> =
        configCache.map { it[farmId] }

    override suspend fun getFarmSetupConfig(farmId: String): FarmSetupConfig? {
        val snapshot = configDocument(farmId).get()
        val config = runCatching { snapshot.data<FarmSetupConfigDocument>().toDomain() }.getOrNull()
        configCache.update { it + (farmId to config) }
        return config
    }

    override suspend fun upsertFarmSetupConfig(config: FarmSetupConfig): FarmSetupConfig {
        configDocument(config.farmId).set(config.toDocument())
        configCache.update { it + (config.farmId to config) }
        return config
    }

    override fun observeLandPartitions(farmId: String): Flow<List<LandPartition>> =
        partitionCache.map { it[farmId].orEmpty() }

    override suspend fun getLandPartitions(farmId: String): List<LandPartition> {
        val snapshot = landPartitionsCollection(farmId).get()
        val partitions = snapshot.documents.mapNotNull { document ->
            runCatching { document.data<LandPartitionDocument>().toDomain() }.getOrNull()
        }.sortedBy { it.name }
        partitionCache.update { it + (farmId to partitions) }
        return partitions
    }

    override suspend fun upsertLandPartition(partition: LandPartition): LandPartition {
        landPartitionsCollection(partition.farmId).document(partition.id).set(partition.toDocument())
        partitionCache.update { current ->
            val updated = current[partition.farmId].orEmpty()
                .filterNot { it.id == partition.id } + partition
            current + (partition.farmId to updated.sortedBy { it.name })
        }
        return partition
    }

    private fun staffCollection(farmId: String): CollectionReference =
        firestore.collection(FARMS_COLLECTION).document(farmId).collection(STAFF_COLLECTION)

    private fun configCollection(farmId: String): CollectionReference =
        firestore.collection(FARMS_COLLECTION).document(farmId).collection(CONFIG_COLLECTION)

    private fun configDocument(farmId: String) =
        configCollection(farmId).document(SETUP_DOCUMENT)

    private fun landPartitionsCollection(farmId: String): CollectionReference =
        configDocument(farmId).collection(LAND_PARTITIONS_COLLECTION)

    private companion object {
        const val FARMS_COLLECTION = "farms"
        const val STAFF_COLLECTION = "staff"
        const val CONFIG_COLLECTION = "config"
        const val SETUP_DOCUMENT = "setup"
        const val LAND_PARTITIONS_COLLECTION = "land_partitions"
    }
}
