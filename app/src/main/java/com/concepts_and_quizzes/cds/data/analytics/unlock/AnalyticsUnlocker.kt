package com.concepts_and_quizzes.cds.data.analytics.unlock

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/** Modules available in the analytics catalogue. */
enum class AnalyticsModule { TREND, HEATMAP, PEER, TIME }

/** Unlock status for an [AnalyticsModule]. */
data class ModuleStatus(
    val module: AnalyticsModule,
    val unlocked: Boolean,
    val reason: LockedReason? = null
)

sealed interface LockedReason {
    data class MoreQuizzes(val remaining: Int) : LockedReason
    data class TimeGate(val duration: Duration) : LockedReason
}

/** Configuration controlling unlock thresholds. */
data class AnalyticsUnlockConfig(
    val trendQuizzes: Int = 3,
    val heatmapQuestions: Int = 50,
    val timeGateHours: Int = 24
)

/** Snapshot of quiz stats needed to compute unlock states. */
data class UnlockStats(
    val sessionsLast7d: Int,
    val answered: Int,
    val userSignedIn: Boolean,
    val lastQuizMillis: Long
)

/** Computes [ModuleStatus] for all [AnalyticsModule]s. */
class AnalyticsUnlocker @javax.inject.Inject constructor(
    private val config: AnalyticsUnlockConfig
) {
    constructor() : this(AnalyticsUnlockConfig())

    fun statuses(stats: UnlockStats, nowMillis: Long = System.currentTimeMillis()): List<ModuleStatus> {
        val res = mutableListOf<ModuleStatus>()

        if (stats.sessionsLast7d >= config.trendQuizzes) {
            res += ModuleStatus(AnalyticsModule.TREND, true)
        } else {
            res += ModuleStatus(
                AnalyticsModule.TREND,
                false,
                LockedReason.MoreQuizzes(config.trendQuizzes - stats.sessionsLast7d)
            )
        }

        if (stats.answered >= config.heatmapQuestions) {
            res += ModuleStatus(AnalyticsModule.HEATMAP, true)
        } else {
            res += ModuleStatus(
                AnalyticsModule.HEATMAP,
                false,
                LockedReason.MoreQuizzes(config.heatmapQuestions - stats.answered)
            )
        }

        res += ModuleStatus(AnalyticsModule.PEER, stats.userSignedIn, null)

        val requiredMs = config.timeGateHours * 60L * 60L * 1000L
        val elapsed = nowMillis - stats.lastQuizMillis
        if (elapsed >= requiredMs) {
            res += ModuleStatus(AnalyticsModule.TIME, true)
        } else {
            res += ModuleStatus(
                AnalyticsModule.TIME,
                false,
                LockedReason.TimeGate((requiredMs - elapsed).milliseconds)
            )
        }

        return res
    }
}
