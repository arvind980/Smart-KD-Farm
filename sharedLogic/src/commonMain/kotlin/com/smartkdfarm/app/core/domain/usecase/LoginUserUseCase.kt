package com.smartkdfarm.app.core.domain.usecase

import com.smartkdfarm.app.core.domain.exception.ValidationException
import com.smartkdfarm.app.core.domain.model.LoginCredentials
import com.smartkdfarm.app.core.domain.model.User
import com.smartkdfarm.app.core.domain.repository.AuthRepository

class LoginUserUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(credentials: LoginCredentials): User {
        if (credentials.mobileNumber.isBlank() || credentials.password.isBlank()) {
            throw ValidationException("Mobile number and password are both required.")
        }
        return authRepository.login(credentials)
    }
}
