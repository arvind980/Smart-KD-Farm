package com.smartkdfarm.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.smartkdfarm.app.core.domain.model.WasteLog

interface WasteRepository {
    fun observeWasteLogs(farmId: String): Flow<List<WasteLog>>

    suspend fun getWasteLogs(farmId: String): List<WasteLog>

    suspend fun saveWasteLog(log: WasteLog): WasteLog

    suspend fun deleteWasteLog(farmId: String, logId: String)
}
