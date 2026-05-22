package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import com.smartkdfarm.app.core.domain.model.FarmerNotification
import com.smartkdfarm.app.core.domain.model.MilkCollectionEntry
import com.smartkdfarm.app.core.domain.repository.OuterCenterRepository

class FirebaseOuterCenterRepository : OuterCenterRepository {
    private val firestore = Firebase.firestore
    private val collectionsCache = MutableStateFlow<Map<String, List<MilkCollectionEntry>>>(emptyMap())
    private val notificationsCache = MutableStateFlow<Map<String, List<FarmerNotification>>>(emptyMap())

    override fun observeCollections(farmId: String): Flow<List<MilkCollectionEntry>> =
        collectionsCache.map { it[farmId].orEmpty() }

    override suspend fun getCollections(farmId: String): List<MilkCollectionEntry> {
        val snapshot = collectionsCollection(farmId).get()
        val entries = snapshot.documents.mapNotNull { runCatching { it.data<MilkCollectionEntryDocument>() }.getOrNull() }
            .map { document ->
                MilkCollectionEntry(
                    id = document.id,
                    farmId = document.farmId,
                    farmerId = document.farmerId,
                    farmerName = document.farmerName,
                    session = document.session,
                    collectedAtEpochMillis = document.collectedAtEpochMillis,
                    rateBreakdown = document.rateBreakdown.toDomain(),
                    paymentStatus = document.paymentStatus,
                    smsTriggered = document.smsTriggered,
                    createdByUserId = document.createdByUserId,
                    createdAtEpochMillis = document.createdAtEpochMillis,
                )
            }
            .sortedByDescending { it.collectedAtEpochMillis }
        collectionsCache.update { it + (farmId to entries) }
        return entries
    }

    override suspend fun saveCollection(entry: MilkCollectionEntry): MilkCollectionEntry {
        collectionsCollection(entry.farmId).document(entry.id).set(entry.toDocument())
        collectionsCache.update { current ->
            val updated = current[entry.farmId].orEmpty().filterNot { it.id == entry.id } + entry
            current + (entry.farmId to updated.sortedByDescending { it.collectedAtEpochMillis })
        }
        return entry
    }

    override fun observeNotifications(farmId: String): Flow<List<FarmerNotification>> =
        notificationsCache.map { it[farmId].orEmpty() }

    override suspend fun getNotifications(farmId: String): List<FarmerNotification> {
        val snapshot = notificationsCollection(farmId).get()
        val notifications = snapshot.documents.mapNotNull { runCatching { it.data<FarmerNotificationDocument>() }.getOrNull() }
            .map { document ->
                FarmerNotification(
                    id = document.id,
                    farmId = document.farmId,
                    farmerId = document.farmerId,
                    channel = document.channel,
                    message = document.message,
                    status = document.status,
                    triggeredByRecordId = document.triggeredByRecordId,
                    createdAtEpochMillis = document.createdAtEpochMillis,
                )
            }
            .sortedByDescending { it.createdAtEpochMillis }
        notificationsCache.update { it + (farmId to notifications) }
        return notifications
    }

    override suspend fun saveNotification(notification: FarmerNotification): FarmerNotification {
        notificationsCollection(notification.farmId).document(notification.id).set(notification.toDocument())
        notificationsCache.update { current ->
            val updated = current[notification.farmId].orEmpty().filterNot { it.id == notification.id } + notification
            current + (notification.farmId to updated.sortedByDescending { it.createdAtEpochMillis })
        }
        return notification
    }

    private fun outerCenterDocument(farmId: String) =
        firestore.collection(FARMS_COLLECTION).document(farmId).collection(OUTER_CENTER_COLLECTION).document(OUTER_CENTER_DOCUMENT)

    private fun collectionsCollection(farmId: String): CollectionReference =
        outerCenterDocument(farmId).collection(COLLECTIONS_COLLECTION)

    private fun notificationsCollection(farmId: String): CollectionReference =
        outerCenterDocument(farmId).collection(NOTIFICATIONS_COLLECTION)

    private companion object {
        const val FARMS_COLLECTION = "farms"
        const val OUTER_CENTER_COLLECTION = "outer_center"
        const val OUTER_CENTER_DOCUMENT = "operations"
        const val COLLECTIONS_COLLECTION = "collections"
        const val NOTIFICATIONS_COLLECTION = "notifications"
    }
}
