package com.concepts_and_quizzes.cds.data.analytics.unlock

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
        assertEquals(
            ModuleStatus(AnalyticsModule.TREND, false, LockedReason.MoreQuizzes(1)),
            statuses[0]
        )
        assertEquals(
            ModuleStatus(AnalyticsModule.HEATMAP, false, LockedReason.MoreQuizzes(10)),
            statuses[1]
        )
        assertEquals(ModuleStatus(AnalyticsModule.PEER, false, null), statuses[2])
        val timeGate = statuses[3].reason as LockedReason.TimeGate
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
        statuses.forEach { assertEquals(true, it.unlocked) }
    }
}
