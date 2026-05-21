package com.smartkdfarm.app.presentation.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.smartkdfarm.app.core.domain.model.FarmRegistrationCommand
import com.smartkdfarm.app.core.domain.model.LoginCredentials
import com.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import com.smartkdfarm.app.core.domain.repository.AuthRepository
import com.smartkdfarm.app.core.domain.repository.FarmRepository
import com.smartkdfarm.app.core.domain.usecase.AddStaffUserUseCase
import com.smartkdfarm.app.core.domain.usecase.LoginUserUseCase
import com.smartkdfarm.app.core.domain.usecase.RegisterFarmUseCase

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val farmRepository: FarmRepository,
    private val registerFarmUseCase: RegisterFarmUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val addStaffUserUseCase: AddStaffUserUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mutableUiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = mutableUiState.asStateFlow()

    private var authObservationJob: Job? = null
    private var farmObservationJob: Job? = null

    init {
        observeSession()
        refreshSession()
    }

    fun currentState(): AuthUiState = uiState.value

    fun refreshSession() {
        scope.launch {
            runCatching { authRepository.refreshAuthenticatedUser() }
                .onFailure { error ->
                    mutableUiState.value = mutableUiState.value.copy(
                        status = AuthFlowStatus.FAILED,
                        errorMessage = error.message,
                        isLoading = false,
                    )
                }
        }
    }

    fun login(mobileNumber: String, password: String) {
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(
                status = AuthFlowStatus.LOADING,
                isLoading = true,
                errorMessage = null,
            )
            runCatching {
                loginUserUseCase(LoginCredentials(mobileNumber = mobileNumber, password = password))
            }.onFailure { error ->
                mutableUiState.value = mutableUiState.value.copy(
                    status = AuthFlowStatus.FAILED,
                    isLoading = false,
                    errorMessage = error.message,
                )
            }
        }
    }

    fun registerFarm(command: FarmRegistrationCommand) {
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(
                status = AuthFlowStatus.LOADING,
                isLoading = true,
                errorMessage = null,
            )
            runCatching { registerFarmUseCase(command) }
                .onFailure { error ->
                    mutableUiState.value = mutableUiState.value.copy(
                        status = AuthFlowStatus.FAILED,
                        isLoading = false,
                        errorMessage = error.message,
                    )
                }
        }
    }

    fun addManagedUser(registration: ManagedUserRegistration) {
        val actor = mutableUiState.value.currentUser ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(
                status = AuthFlowStatus.LOADING,
                isLoading = true,
                errorMessage = null,
            )
            runCatching { addStaffUserUseCase(actor, registration) }
                .onSuccess { request ->
                    mutableUiState.value = mutableUiState.value.copy(
                        status = AuthFlowStatus.AUTHENTICATED,
                        isLoading = false,
                        provisioningRequest = request,
                    )
                }
                .onFailure { error ->
                    mutableUiState.value = mutableUiState.value.copy(
                        status = AuthFlowStatus.FAILED,
                        isLoading = false,
                        errorMessage = error.message,
                    )
                }
        }
    }

    fun logout() {
        scope.launch {
            runCatching { authRepository.logout() }
                .onFailure { error ->
                    mutableUiState.value = mutableUiState.value.copy(
                        status = AuthFlowStatus.FAILED,
                        errorMessage = error.message,
                    )
                }
        }
    }

    fun clearTransientMessage() {
        mutableUiState.value = mutableUiState.value.copy(
            errorMessage = null,
            provisioningRequest = null,
        )
    }

    fun startObserving(observer: (AuthUiState) -> Unit): CloseableHandle {
        val job = scope.launch {
            uiState.collectLatest(observer)
        }
        return CloseableHandle(job::cancel)
    }

    fun dispose() {
        authObservationJob?.cancel()
        farmObservationJob?.cancel()
        scope.coroutineContext[Job]?.cancel()
    }

    private fun observeSession() {
        authObservationJob?.cancel()
        authObservationJob = scope.launch {
            authRepository.observeAuthenticatedUser().collectLatest { user ->
                if (user == null) {
                    mutableUiState.value = mutableUiState.value.copy(
                        status = AuthFlowStatus.SIGNED_OUT,
                        isLoading = false,
                        currentUser = null,
                        farmProfile = null,
                        errorMessage = null,
                    )
                } else {
                    mutableUiState.value = mutableUiState.value.copy(
                        status = AuthFlowStatus.AUTHENTICATED,
                        isLoading = false,
                        currentUser = user,
                        errorMessage = null,
                    )

                    farmObservationJob?.cancel()
                    val farmId = user.farmId
                    farmObservationJob = launch {
                        runCatching { farmRepository.getFarmProfile(farmId) }
                            .onSuccess { farm ->
                                mutableUiState.value = mutableUiState.value.copy(farmProfile = farm)
                            }
                            .onFailure { error ->
                                mutableUiState.value = mutableUiState.value.copy(
                                    errorMessage = error.message,
                                    farmProfile = null,
                                )
                            }

                        farmRepository.observeFarmProfile(farmId).collectLatest { updatedFarm ->
                            mutableUiState.value = mutableUiState.value.copy(farmProfile = updatedFarm)
                        }
                    }
                }
            }
        }
    }
}

class CloseableHandle(
    private val closer: () -> Unit,
) {
    fun close() {
        closer()
    }
}
