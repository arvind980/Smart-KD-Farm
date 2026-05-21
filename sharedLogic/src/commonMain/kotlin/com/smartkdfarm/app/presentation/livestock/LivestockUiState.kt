package com.smartkdfarm.app.presentation.livestock

import kotlinx.serialization.Serializable
import com.smartkdfarm.app.core.domain.model.AnimalProfile

@Serializable
data class LivestockStatusSummary(
    val label: String,
    val count: Int,
    val colorHex: String,
)

@Serializable
data class AnimalCardUiModel(
    val id: String,
    val tagNumber: String,
    val breed: String,
    val ageLabel: String,
    val purchasePriceLabel: String,
    val statusLabel: String,
    val statusColorHex: String,
    val breedingTimelineLabel: String,
    val healthHeadline: String,
    val isActive: Boolean,
)

@Serializable
data class LivestockUiState(
    val isLoading: Boolean = false,
    val farmId: String? = null,
    val animals: List<AnimalProfile> = emptyList(),
    val animalCards: List<AnimalCardUiModel> = emptyList(),
    val statusSummary: List<LivestockStatusSummary> = emptyList(),
    val errorMessage: String? = null,
)
