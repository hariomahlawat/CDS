package com.concepts_and_quizzes.cds.ui.reports

enum class WindowRange { D7, D30, ALL }

fun WindowRange.label(): String = when (this) {
    WindowRange.D7  -> "7D"
    WindowRange.D30 -> "30D"
    WindowRange.ALL -> "All"
}

/** What your pages already accept: "D7" | "D30" | "LIFETIME" */
fun WindowRange.asWindowArg(): String = when (this) {
    WindowRange.D7  -> "D7"
    WindowRange.D30 -> "D30"
    WindowRange.ALL -> "LIFETIME"
}
