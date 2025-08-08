package com.concepts_and_quizzes.cds.data.analytics.unlock

import com.concepts_and_quizzes.cds.core.config.DefaultRemoteConfig
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/** Modules available in the analytics catalogue. */
enum class AnalyticsModule { TREND, HEATMAP, PEER, TIME }

/** Unlock status for an [AnalyticsModule]. */
data class ModuleStatus(
    val module: AnalyticsModule,
    val unlocked: Boolean,
    val progress: Float,
    val reason: LockedReason? = null
)

sealed interface LockedReason {
    data class MoreQuizzes(val remaining: Int) : LockedReason
    data class TimeGate(val duration: Duration) : LockedReason
}
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
    constructor() : this(AnalyticsUnlockConfig(DefaultRemoteConfig()))

    fun statuses(stats: UnlockStats, nowMillis: Long = System.currentTimeMillis()): List<ModuleStatus> {
        val res = mutableListOf<ModuleStatus>()

        val trendProgress = min(
            stats.sessionsLast7d / config.trendQuizzes.toFloat(),
            1f
        )
        if (stats.sessionsLast7d >= config.trendQuizzes) {
            res += ModuleStatus(AnalyticsModule.TREND, true, trendProgress)
        } else {
            res += ModuleStatus(
                AnalyticsModule.TREND,
                false,
                trendProgress,
                LockedReason.MoreQuizzes(config.trendQuizzes - stats.sessionsLast7d)
            )
        }

        val heatmapProgress = min(
            stats.answered / config.heatmapQuestions.toFloat(),
            1f
        )
        if (stats.answered >= config.heatmapQuestions) {
            res += ModuleStatus(AnalyticsModule.HEATMAP, true, heatmapProgress)
        } else {
            res += ModuleStatus(
                AnalyticsModule.HEATMAP,
                false,
                heatmapProgress,
                LockedReason.MoreQuizzes(config.heatmapQuestions - stats.answered)
            )
        }

        val peerProgress = if (stats.userSignedIn) 1f else 0f
        res += ModuleStatus(AnalyticsModule.PEER, stats.userSignedIn, peerProgress, null)

        val requiredMs = config.timeGateHours * 60L * 60L * 1000L
        val elapsed = nowMillis - stats.lastQuizMillis
        val timeProgress = min(elapsed / requiredMs.toFloat(), 1f)
        if (elapsed >= requiredMs) {
            res += ModuleStatus(AnalyticsModule.TIME, true, timeProgress)
        } else {
            res += ModuleStatus(
                AnalyticsModule.TIME,
                false,
                timeProgress,
                LockedReason.TimeGate((requiredMs - elapsed).milliseconds)
            )
        }

        return res
    }
}
