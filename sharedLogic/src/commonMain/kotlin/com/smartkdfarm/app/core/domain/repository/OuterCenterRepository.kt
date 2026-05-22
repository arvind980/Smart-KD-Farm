package com.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.smartkdfarm.app.core.domain.model.FarmerNotification
import com.smartkdfarm.app.core.domain.model.MilkCollectionEntry

interface OuterCenterRepository {
    fun observeCollections(farmId: String): Flow<List<MilkCollectionEntry>>

    suspend fun getCollections(farmId: String): List<MilkCollectionEntry>

    suspend fun saveCollection(entry: MilkCollectionEntry): MilkCollectionEntry

    fun observeNotifications(farmId: String): Flow<List<FarmerNotification>>

    suspend fun getNotifications(farmId: String): List<FarmerNotification>

    suspend fun saveNotification(notification: FarmerNotification): FarmerNotification
}
