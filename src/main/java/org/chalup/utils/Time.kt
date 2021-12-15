package org.chalup.utils

import kotlin.time.measureTimedValue

inline fun <T, R> T.timedLet(label: String, block: (T) -> R): R = measureTimedValue { block(this) }
    .also { println("$label took ${it.duration}") }
    .value
