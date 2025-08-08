package com.concepts_and_quizzes.cds.data.analytics.unlock

import com.concepts_and_quizzes.cds.core.config.RemoteConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

class AnalyticsUnlockerTest {
    private val unlocker = AnalyticsUnlocker()

    @Test
    fun `locked states when below thresholds`() {
        val stats = UnlockStats(
            sessionsLast7d = 2,
            answered = 40,
            userSignedIn = false,
            lastQuizMillis = 0L
        )
        val now = 2.hours.inWholeMilliseconds
        val statuses = unlocker.statuses(stats, now)

        val trend = statuses[0]
        assertEquals(AnalyticsModule.TREND, trend.module)
        assertEquals(false, trend.unlocked)
        assertEquals(2f / 3f, trend.progress, 0.001f)
        assertEquals(LockedReason.MoreQuizzes(1), trend.reason)

        val heatmap = statuses[1]
        assertEquals(AnalyticsModule.HEATMAP, heatmap.module)
        assertEquals(false, heatmap.unlocked)
        assertEquals(0.8f, heatmap.progress, 0.001f)
        assertEquals(LockedReason.MoreQuizzes(10), heatmap.reason)

        val peer = statuses[2]
        assertEquals(AnalyticsModule.PEER, peer.module)
        assertEquals(false, peer.unlocked)
        assertEquals(0f, peer.progress, 0.001f)
        assertEquals(null, peer.reason)

        val time = statuses[3]
        assertEquals(AnalyticsModule.TIME, time.module)
        assertEquals(false, time.unlocked)
        assertEquals(2f / 24f, time.progress, 0.001f)
        val timeGate = time.reason as LockedReason.TimeGate
        assertEquals(22.hours, timeGate.duration)
    }

    @Test
    fun `unlocks when criteria met`() {
        val stats = UnlockStats(
            sessionsLast7d = 3,
            answered = 50,
            userSignedIn = true,
            lastQuizMillis = 0L
        )
        val now = 25.hours.inWholeMilliseconds
        val statuses = unlocker.statuses(stats, now)
        statuses.forEach {
            assertEquals(true, it.unlocked)
            assertEquals(1f, it.progress, 0.001f)
        }
    }

    @Test
    fun `remote config override changes thresholds`() {
        val rc = object : RemoteConfig {
            override fun getBoolean(key: String) = true
            override fun getString(key: String): String? =
                """{"trendQuizzes":5,"heatmapQuestions":40,"timeGateHours":12}"""
        }
        val unlocker = AnalyticsUnlocker(AnalyticsUnlockConfig(rc))
        val stats = UnlockStats(
            sessionsLast7d = 4,
            answered = 50,
            userSignedIn = true,
            lastQuizMillis = 0L
        )
        val now = 12.hours.inWholeMilliseconds
        val statuses = unlocker.statuses(stats, now)

        val trend = statuses.first { it.module == AnalyticsModule.TREND }
        assertEquals(false, trend.unlocked)
        assertEquals(4f / 5f, trend.progress, 0.001f)

        val heatmap = statuses.first { it.module == AnalyticsModule.HEATMAP }
        assertEquals(true, heatmap.unlocked)

        val time = statuses.first { it.module == AnalyticsModule.TIME }
        assertEquals(true, time.unlocked)
    }
}
