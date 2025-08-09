package com.concepts_and_quizzes.cds.ui.reports.trend


import androidx.compose.runtime.Immutable

/** UI contracts for the Trend feature. Pure data, no logic. */
@Immutable
data class TrendPointUI(
    val label: String,          // e.g., "07-27"
    val attempts: Int,
    val correct: Int,
    val avgSecPerQ: Int         // integer for stable text layout
)

@Immutable
data class TrendKpiUI(
    val attempts: Int,
    val accuracyPct: Int,
    val avgSecPerQ: Int,
    val bestDayLabel: String?
)

@Immutable
data class TopicMomentumUI(
    val topic: String,          // display name (fallback: topicId)
    val accuracyPct: Int,
    val deltaPct: Int
)

@Immutable
data class InsightUI(
    val text: String,
    val actionLabel: String? = null,
    val actionRoute: String? = null
)

@Immutable
data class TrendUi(
    val windowLabel: String,            // "7D" | "30D" | "All"
    val windows: List<String>,          // ["7D","30D","All"]
    val selectedWindowIndex: Int,
    val kpis: TrendKpiUI,
    val points: List<TrendPointUI>,
    val topics: List<TopicMomentumUI>,
    val insights: List<InsightUI>
)
