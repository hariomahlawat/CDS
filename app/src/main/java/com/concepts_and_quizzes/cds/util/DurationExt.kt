package com.concepts_and_quizzes.cds.util

import kotlin.time.Duration

/**
 * Returns a human-friendly string representation of this [Duration].
 *
 * The output uses day (`d`), hour (`h`), minute (`m`), and second (`s`) units
 * and omits zero-valued components except when the duration is zero seconds.
 * Examples:
 *  - `65.seconds.toPretty()` returns `"1m 5s"`
 *  - `25.hours.toPretty()` returns `"1d 1h"`
 */
fun Duration.toPretty(): String {
    val totalSeconds = inWholeSeconds
    var remaining = totalSeconds

    val days = remaining / 86_400
    remaining %= 86_400

    val hours = remaining / 3_600
    remaining %= 3_600

    val minutes = remaining / 60
    val seconds = remaining % 60

    val parts = mutableListOf<String>()
    if (days > 0) parts += "${days}d"
    if (hours > 0) parts += "${hours}h"
    if (minutes > 0) parts += "${minutes}m"
    if (seconds > 0 || parts.isEmpty()) parts += "${seconds}s"

    return parts.joinToString(" ")
}

