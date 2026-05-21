package com.smartkdfarm.app.presentation.auth

import kotlinx.serialization.Serializable
import com.smartkdfarm.app.core.domain.model.FarmProfile
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.model.UserProvisioningRequest
import com.smartkdfarm.app.core.domain.model.UserRole

@Serializable
enum class AuthFlowStatus {
    IDLE,
    LOADING,
    AUTHENTICATED,
    SIGNED_OUT,
    FAILED,
}

@Serializable
data class AuthUiState(
    val status: AuthFlowStatus = AuthFlowStatus.IDLE,
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val farmProfile: FarmProfile? = null,
    val provisioningRequest: UserProvisioningRequest? = null,
    val errorMessage: String? = null,
) {
    val currentRole: UserRole? get() = currentUser?.role
}
