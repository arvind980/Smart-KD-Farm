package com.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.smartkdfarm.app.core.domain.model.DashboardAlert
import com.smartkdfarm.app.core.domain.model.PredictionPoint

interface AnalyticsRepository {
    fun observeDashboardAlerts(farmId: String): Flow<List<DashboardAlert>>

    suspend fun getDashboardAlerts(farmId: String): List<DashboardAlert>

    suspend fun saveDashboardAlert(alert: DashboardAlert): DashboardAlert

    fun observePredictions(farmId: String): Flow<List<PredictionPoint>>

    suspend fun getPredictions(farmId: String): List<PredictionPoint>

    suspend fun savePredictions(farmId: String, points: List<PredictionPoint>)
}
