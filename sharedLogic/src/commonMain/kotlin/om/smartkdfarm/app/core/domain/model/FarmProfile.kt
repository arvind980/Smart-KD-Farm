package om.smartkdfarm.app.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AreaUnit {
    BIGHA,
    ACRE,
    HECTARE,
    SQUARE_METER,
    SQUARE_FOOT,
    ;

    internal fun toSquareMeters(value: Double, bighaSizeInSquareMeters: Double): Double =
        when (this) {
            BIGHA -> value * bighaSizeInSquareMeters
            ACRE -> value * 4046.8564224
            HECTARE -> value * 10_000.0
            SQUARE_METER -> value
            SQUARE_FOOT -> value * 0.09290304
        }

    internal fun fromSquareMeters(value: Double, bighaSizeInSquareMeters: Double): Double =
        when (this) {
            BIGHA -> value / bighaSizeInSquareMeters
            ACRE -> value / 4046.8564224
            HECTARE -> value / 10_000.0
            SQUARE_METER -> value
            SQUARE_FOOT -> value / 0.09290304
        }
}

@Serializable
data class AreaConfiguration(
    val value: Double,
    val unit: AreaUnit,
    val localBighaSizeInSquareMeters: Double = DEFAULT_BIGHA_SQ_METERS,
) {
    fun convertTo(targetUnit: AreaUnit): Double {
        val squareMeters = unit.toSquareMeters(
            value = value,
            bighaSizeInSquareMeters = localBighaSizeInSquareMeters,
        )
        return targetUnit.fromSquareMeters(
            value = squareMeters,
            bighaSizeInSquareMeters = localBighaSizeInSquareMeters,
        )
    }

    companion object {
        const val DEFAULT_BIGHA_SQ_METERS: Double = 2_500.0
    }
}

@Serializable
data class FarmLocation(
    val village: String,
    val district: String,
    val state: String,
    val country: String = "India",
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class FarmProfile(
    val id: String,
    val farmName: String,
    val ownerName: String,
    val primaryPhoneNumber: String,
    val location: FarmLocation,
    val landArea: AreaConfiguration,
    val managerUserId: String? = null,
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
    val updatedAtEpochMillis: Long = TimeProvider.nowEpochMillis(),
) {
    fun areaIn(unit: AreaUnit): Double = landArea.convertTo(unit)
}
