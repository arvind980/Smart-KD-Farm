package com.smartkdfarm.app.presentation.admin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.smartkdfarm.app.core.domain.model.FarmSetupConfig
import com.smartkdfarm.app.core.domain.model.LandPartition
import com.smartkdfarm.app.core.domain.model.StaffAccessProfile
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.repository.AdminRepository
import com.smartkdfarm.app.core.domain.repository.AuthRepository
import com.smartkdfarm.app.core.domain.usecase.DeactivateStaffAccessProfileUseCase
import com.smartkdfarm.app.core.domain.usecase.SaveFarmSetupUseCase
import com.smartkdfarm.app.core.domain.usecase.UpsertStaffAccessProfileUseCase
import com.smartkdfarm.app.presentation.auth.CloseableHandle

class AdminViewModel(
    private val authRepository: AuthRepository,
    private val adminRepository: AdminRepository,
    private val upsertStaffAccessProfileUseCase: UpsertStaffAccessProfileUseCase,
    private val deactivateStaffAccessProfileUseCase: DeactivateStaffAccessProfileUseCase,
    private val saveFarmSetupUseCase: SaveFarmSetupUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mutableUiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = mutableUiState.asStateFlow()

    private var authObservationJob: Job? = null
    private var staffObservationJob: Job? = null
    private var configObservationJob: Job? = null
    private var partitionObservationJob: Job? = null

    init {
        observeSession()
    }

    fun refresh() {
        val farmId = mutableUiState.value.farmId ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { adminRepository.getStaffProfiles(farmId) }
                .onSuccess { publishStaff(it, farmId) }
                .onFailure(::publishError)
            runCatching { adminRepository.getFarmSetupConfig(farmId) }
                .onSuccess { publishConfig(it, farmId) }
                .onFailure(::publishError)
            runCatching { adminRepository.getLandPartitions(farmId) }
                .onSuccess { publishPartitions(it, farmId) }
                .onFailure(::publishError)
        }
    }

    fun saveStaffProfile(profile: StaffAccessProfile) {
        val actor = currentActor() ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { upsertStaffAccessProfileUseCase(actor, profile) }
                .onSuccess {
                    val refreshed = adminRepository.getStaffProfiles(actor.farmId)
                    publishStaff(refreshed, actor.farmId)
                }
                .onFailure(::publishError)
        }
    }

    fun deactivateStaffProfile(staffId: String) {
        val actor = currentActor() ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { deactivateStaffAccessProfileUseCase(actor, staffId) }
                .onSuccess {
                    val refreshed = adminRepository.getStaffProfiles(actor.farmId)
                    publishStaff(refreshed, actor.farmId)
                }
                .onFailure(::publishError)
        }
    }

    fun saveFarmSetup(partitions: List<LandPartition>, notes: String? = null) {
        val actor = currentActor() ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { saveFarmSetupUseCase(actor, partitions, notes) }
                .onSuccess { config ->
                    publishConfig(config, actor.farmId)
                    publishPartitions(adminRepository.getLandPartitions(actor.farmId), actor.farmId)
                }
                .onFailure(::publishError)
        }
    }

    fun clearTransientMessage() {
        mutableUiState.value = mutableUiState.value.copy(errorMessage = null)
    }

    fun startObserving(observer: (AdminUiState) -> Unit): CloseableHandle {
        val job = scope.launch { uiState.collectLatest(observer) }
        return CloseableHandle(job::cancel)
    }

    fun dispose() {
        authObservationJob?.cancel()
        staffObservationJob?.cancel()
        configObservationJob?.cancel()
        partitionObservationJob?.cancel()
        scope.coroutineContext[Job]?.cancel()
    }

    private fun observeSession() {
        authObservationJob?.cancel()
        authObservationJob = scope.launch {
            authRepository.observeAuthenticatedUser().collectLatest { user ->
                clearChildren()
                if (user == null) {
                    mutableUiState.value = AdminUiState()
                    return@collectLatest
                }
                val farmId = user.farmId
                mutableUiState.value = mutableUiState.value.copy(
                    farmId = farmId,
                    isLoading = true,
                    errorMessage = null,
                )
                refresh()
                staffObservationJob = launch {
                    adminRepository.observeStaffProfiles(farmId).collectLatest {
                        publishStaff(it, farmId)
                    }
                }
                configObservationJob = launch {
                    adminRepository.observeFarmSetupConfig(farmId).collectLatest {
                        publishConfig(it, farmId)
                    }
                }
                partitionObservationJob = launch {
                    adminRepository.observeLandPartitions(farmId).collectLatest {
                        publishPartitions(it, farmId)
                    }
                }
            }
        }
    }

    private fun clearChildren() {
        staffObservationJob?.cancel()
        configObservationJob?.cancel()
        partitionObservationJob?.cancel()
    }

    private fun publishStaff(profiles: List<StaffAccessProfile>, farmId: String?) {
        mutableUiState.value = mutableUiState.value.copy(
            farmId = farmId,
            isLoading = false,
            staffProfiles = profiles.sortedBy { it.fullName },
        )
    }

    private fun publishConfig(config: FarmSetupConfig?, farmId: String?) {
        mutableUiState.value = mutableUiState.value.copy(
            farmId = farmId,
            isLoading = false,
            farmSetupConfig = config,
        )
    }

    private fun publishPartitions(partitions: List<LandPartition>, farmId: String?) {
        mutableUiState.value = mutableUiState.value.copy(
            farmId = farmId,
            isLoading = false,
            landPartitions = partitions.sortedBy { it.name },
        )
    }

    private fun publishError(error: Throwable) {
        mutableUiState.value = mutableUiState.value.copy(
            isLoading = false,
            errorMessage = error.message,
        )
    }

    private fun currentActor(): User? = authRepository.currentAuthenticatedUser()
}
