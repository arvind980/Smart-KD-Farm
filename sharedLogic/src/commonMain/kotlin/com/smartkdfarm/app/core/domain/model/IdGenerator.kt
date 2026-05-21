package com.smartkdfarm.app.core.domain.model

import kotlin.random.Random

internal object IdGenerator {
    fun newId(prefix: String): String {
        val now = TimeProvider.nowEpochMillis()
        val entropy = Random.nextInt(1000, 9999)
        return "${prefix}_${now}_$entropy"
    }
}
