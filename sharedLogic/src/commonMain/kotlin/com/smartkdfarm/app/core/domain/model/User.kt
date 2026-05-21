package com.smartkdfarm.app.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    MANAGER,
    DAIRY_MAN,
    LABOUR,
    FARMER,
}

@Serializable
data class User(
    val id: String,
    val farmId: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
    val updatedAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
)
