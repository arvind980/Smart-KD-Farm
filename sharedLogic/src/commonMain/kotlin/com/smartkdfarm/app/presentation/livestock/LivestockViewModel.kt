package com.smartkdfarm.app.presentation.livestock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.smartkdfarm.app.core.domain.model.AnimalArchiveReason
import com.smartkdfarm.app.core.domain.model.AnimalProfile
import com.smartkdfarm.app.core.domain.model.AnimalStatus
import com.smartkdfarm.app.core.domain.model.HealthSeverity
import com.smartkdfarm.app.core.domain.repository.AuthRepository
import com.smartkdfarm.app.core.domain.repository.LivestockRepository
import com.smartkdfarm.app.core.domain.usecase.ArchiveAnimalUseCase
import com.smartkdfarm.app.core.domain.usecase.LogHealthEventUseCase
import com.smartkdfarm.app.core.domain.usecase.LogInseminationUseCase
import com.smartkdfarm.app.presentation.auth.CloseableHandle

class LivestockViewModel(
    private val authRepository: AuthRepository,
    private val livestockRepository: LivestockRepository,
    private val logInseminationUseCase: LogInseminationUseCase,
    private val archiveAnimalUseCase: ArchiveAnimalUseCase,
    private val logHealthEventUseCase: LogHealthEventUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mutableUiState = MutableStateFlow(LivestockUiState())
    val uiState: StateFlow<LivestockUiState> = mutableUiState.asStateFlow()

    private var authObservationJob: Job? = null
    private var livestockObservationJob: Job? = null

    init {
        observeSession()
    }

    fun currentState(): LivestockUiState = uiState.value

    fun refresh() {
        val farmId = mutableUiState.value.farmId ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { livestockRepository.getLivestock(farmId) }
                .onSuccess(::publishAnimals)
                .onFailure { error ->
                    mutableUiState.value = mutableUiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message,
                    )
                }
        }
    }

    fun logInsemination(
        animalId: String,
        aiDateEpochMillis: Long,
        technicianName: String? = null,
        notes: String? = null,
    ) {
        val farmId = mutableUiState.value.farmId ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                logInseminationUseCase(
                    farmId = farmId,
                    animalId = animalId,
                    aiDateEpochMillis = aiDateEpochMillis,
                    technicianName = technicianName,
                    notes = notes,
                )
            }.onSuccess {
                val refreshed = livestockRepository.getLivestock(farmId)
                publishAnimals(refreshed)
            }.onFailure { error ->
                mutableUiState.value = mutableUiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message,
                )
            }
        }
    }

    fun archiveAnimal(
        animalId: String,
        reason: AnimalArchiveReason,
        saleAmount: Double? = null,
        notes: String? = null,
    ) {
        val farmId = mutableUiState.value.farmId ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                archiveAnimalUseCase(
                    farmId = farmId,
                    animalId = animalId,
                    reason = reason,
                    saleAmount = saleAmount,
                    notes = notes,
                )
            }.onSuccess {
                val refreshed = livestockRepository.getLivestock(farmId)
                publishAnimals(refreshed)
            }.onFailure { error ->
                mutableUiState.value = mutableUiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message,
                )
            }
        }
    }

    fun logHealthEvent(
        animalId: String,
        title: String,
        notes: String? = null,
        treatment: String? = null,
        severity: HealthSeverity = HealthSeverity.OBSERVATION,
    ) {
        val farmId = mutableUiState.value.farmId ?: return
        scope.launch {
            mutableUiState.value = mutableUiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                logHealthEventUseCase(
                    farmId = farmId,
                    animalId = animalId,
                    title = title,
                    notes = notes,
                    treatment = treatment,
                    severity = severity,
                )
            }.onSuccess {
                val refreshed = livestockRepository.getLivestock(farmId)
                publishAnimals(refreshed)
            }.onFailure { error ->
                mutableUiState.value = mutableUiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message,
                )
            }
        }
    }

    fun clearTransientMessage() {
        mutableUiState.value = mutableUiState.value.copy(errorMessage = null)
    }

    fun startObserving(observer: (LivestockUiState) -> Unit): CloseableHandle {
        val job = scope.launch {
            uiState.collectLatest(observer)
        }
        return CloseableHandle(job::cancel)
    }

    fun dispose() {
        authObservationJob?.cancel()
        livestockObservationJob?.cancel()
        scope.coroutineContext[Job]?.cancel()
    }

    private fun observeSession() {
        authObservationJob?.cancel()
        authObservationJob = scope.launch {
            authRepository.observeAuthenticatedUser().collectLatest { user ->
                livestockObservationJob?.cancel()
                if (user == null) {
                    mutableUiState.value = LivestockUiState()
                    return@collectLatest
                }

                mutableUiState.value = mutableUiState.value.copy(
                    isLoading = true,
                    farmId = user.farmId,
                    errorMessage = null,
                )

                runCatching { livestockRepository.getLivestock(user.farmId) }
                    .onSuccess { initial ->
                        publishAnimals(initial, farmId = user.farmId)
                    }
                    .onFailure { error ->
                        mutableUiState.value = mutableUiState.value.copy(
                            isLoading = false,
                            farmId = user.farmId,
                            errorMessage = error.message,
                        )
                    }

                livestockObservationJob = launch {
                    livestockRepository.observeLivestock(user.farmId).collectLatest { animals ->
                        publishAnimals(animals, farmId = user.farmId)
                    }
                }
            }
        }
    }

    private fun publishAnimals(
        animals: List<AnimalProfile>,
        farmId: String? = mutableUiState.value.farmId,
    ) {
        val sorted = animals.sortedBy { it.tagNumber }
        mutableUiState.value = mutableUiState.value.copy(
            isLoading = false,
            farmId = farmId,
            animals = sorted,
            animalCards = sorted.map(::toCardModel),
            statusSummary = buildStatusSummary(sorted),
        )
    }

    private fun toCardModel(animal: AnimalProfile): AnimalCardUiModel {
        val latestBreeding = animal.breedingLogs.maxByOrNull { it.aiDateEpochMillis }
        val latestHealth = animal.healthLogs.maxByOrNull { it.recordedAtEpochMillis }
        return AnimalCardUiModel(
            id = animal.id,
            tagNumber = animal.tagNumber,
            breed = animal.breed,
            ageLabel = formatAge(animal.ageInMonths),
            purchasePriceLabel = "Rs ${animal.purchasePrice.toLong()}",
            statusLabel = animal.status.name.replace("_", " "),
            statusColorHex = statusColorHex(animal.status),
            breedingTimelineLabel = latestBreeding?.let {
                "PD ${formatDate(it.pregnancyDiagnosisDueEpochMillis)} • Calving ${formatDate(it.expectedCalvingEpochMillis)}"
            } ?: "No AI schedule logged",
            healthHeadline = latestHealth?.let {
                "${it.title} • ${it.severity.name.replace("_", " ")}"
            } ?: "No health alert",
            isActive = animal.isActive,
        )
    }

    private fun buildStatusSummary(animals: List<AnimalProfile>): List<LivestockStatusSummary> =
        AnimalStatus.entries.map { status ->
            LivestockStatusSummary(
                label = status.name.replace("_", " "),
                count = animals.count { it.status == status && it.isActive },
                colorHex = statusColorHex(status),
            )
        }

    private fun statusColorHex(status: AnimalStatus): String =
        when (status) {
            AnimalStatus.MILKING -> "#7EB55D"
            AnimalStatus.DRY -> "#C89B3C"
            AnimalStatus.PREGNANT -> "#2E7D68"
            AnimalStatus.SICK -> "#C74B50"
        }

    private fun formatAge(ageInMonths: Int): String {
        val years = ageInMonths / 12
        val months = ageInMonths % 12
        return when {
            years <= 0 -> "$months mo"
            months == 0 -> "$years yr"
            else -> "$years yr $months mo"
        }
    }

    private fun formatDate(epochMillis: Long): String {
        val date = Instant.fromEpochMilliseconds(epochMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        return "${date.dayOfMonth}/${date.monthNumber}/${date.year}"
    }
}
