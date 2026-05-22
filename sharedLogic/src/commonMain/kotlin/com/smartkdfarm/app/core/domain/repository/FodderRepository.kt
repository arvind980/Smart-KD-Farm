package com.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.smartkdfarm.app.core.domain.model.FodderYieldLog

interface FodderRepository {
    fun observeFodderYields(farmId: String): Flow<List<FodderYieldLog>>

    suspend fun getFodderYields(farmId: String): List<FodderYieldLog>

    suspend fun saveFodderYield(log: FodderYieldLog): FodderYieldLog

    suspend fun deleteFodderYield(farmId: String, logId: String)
}
