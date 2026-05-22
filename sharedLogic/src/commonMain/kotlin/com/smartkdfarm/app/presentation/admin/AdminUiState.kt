package com.smartkdfarm.app.presentation.admin

import com.smartkdfarm.app.core.domain.model.FarmSetupConfig
import com.smartkdfarm.app.core.domain.model.LandPartition
import com.smartkdfarm.app.core.domain.model.StaffAccessProfile

data class AdminUiState(
    val farmId: String? = null,
    val isLoading: Boolean = false,
    val staffProfiles: List<StaffAccessProfile> = emptyList(),
    val farmSetupConfig: FarmSetupConfig? = null,
    val landPartitions: List<LandPartition> = emptyList(),
    val errorMessage: String? = null,
)
