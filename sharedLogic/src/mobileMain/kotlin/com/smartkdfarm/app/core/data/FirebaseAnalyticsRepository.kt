package com.smartkdfarm.app.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import com.smartkdfarm.app.core.domain.model.DashboardAlert
import com.smartkdfarm.app.core.domain.model.PredictionPoint
import com.smartkdfarm.app.core.domain.repository.AnalyticsRepository

class FirebaseAnalyticsRepository : AnalyticsRepository {
    private val firestore = Firebase.firestore
    private val alertsCache = MutableStateFlow<Map<String, List<DashboardAlert>>>(emptyMap())
    private val predictionsCache = MutableStateFlow<Map<String, List<PredictionPoint>>>(emptyMap())

    override fun observeDashboardAlerts(farmId: String): Flow<List<DashboardAlert>> =
        alertsCache.map { it[farmId].orEmpty() }

    override suspend fun getDashboardAlerts(farmId: String): List<DashboardAlert> {
        val snapshot = alertsCollection(farmId).get()
        val alerts = snapshot.documents.mapNotNull { runCatching { it.data<DashboardAlertDocument>() }.getOrNull() }
            .map {
                DashboardAlert(
                    id = it.id,
                    farmId = it.farmId,
                    title = it.title,
                    message = it.message,
                    severity = it.severity,
                    actionLabel = it.actionLabel,
                    createdAtEpochMillis = it.createdAtEpochMillis,
                    acknowledged = it.acknowledged,
                )
            }.sortedByDescending { it.createdAtEpochMillis }
        alertsCache.update { it + (farmId to alerts) }
        return alerts
    }

    override suspend fun saveDashboardAlert(alert: DashboardAlert): DashboardAlert {
        alertsCollection(alert.farmId).document(alert.id).set(alert.toDocument())
        alertsCache.update { current ->
            val updated = current[alert.farmId].orEmpty().filterNot { it.id == alert.id } + alert
            current + (alert.farmId to updated.sortedByDescending { it.createdAtEpochMillis })
        }
        return alert
    }

    override fun observePredictions(farmId: String): Flow<List<PredictionPoint>> =
        predictionsCache.map { it[farmId].orEmpty() }

    override suspend fun getPredictions(farmId: String): List<PredictionPoint> {
        val snapshot = predictionsCollection(farmId).get()
        val points = snapshot.documents.mapNotNull { runCatching { it.data<PredictionPointDocument>() }.getOrNull() }
            .map {
                PredictionPoint(
                    monthIndex = it.monthIndex,
                    label = it.label,
                    projectedMilkLiters = it.projectedMilkLiters,
                    projectedRevenue = it.projectedRevenue,
                    source = it.source,
                )
            }.sortedBy { it.monthIndex }
        predictionsCache.update { it + (farmId to points) }
        return points
    }

    override suspend fun savePredictions(farmId: String, points: List<PredictionPoint>) {
        points.forEach { point ->
            predictionsCollection(farmId).document(point.monthIndex.toString()).set(point.toDocument())
        }
        predictionsCache.update { it + (farmId to points.sortedBy { point -> point.monthIndex }) }
    }

    private fun alertsCollection(farmId: String): CollectionReference =
        firestore.collection(FARMS_COLLECTION).document(farmId).collection(ALERTS_COLLECTION)

    private fun predictionsCollection(farmId: String): CollectionReference =
        firestore.collection(FARMS_COLLECTION).document(farmId).collection(ANALYTICS_COLLECTION).document(PREDICTIONS_DOCUMENT).collection(PREDICTIONS_COLLECTION)

    private companion object {
        const val FARMS_COLLECTION = "farms"
        const val ALERTS_COLLECTION = "alerts"
        const val ANALYTICS_COLLECTION = "analytics"
        const val PREDICTIONS_DOCUMENT = "supply_predictions"
        const val PREDICTIONS_COLLECTION = "points"
    }
}
