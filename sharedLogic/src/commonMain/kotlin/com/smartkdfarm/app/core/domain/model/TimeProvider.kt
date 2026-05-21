package com.smartkdfarm.app.core.domain.model

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal object TimeProvider {
    fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
