package com.concepts_and_quizzes.cds.data.analytics

import com.concepts_and_quizzes.cds.data.analytics.db.QuizTrace
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReportBuilder
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class QuizReportBuilderTest {

    @Test
    fun QuizReportBuilder_NoRepeats() {
        val traces = listOf(
            QuizTrace("s", 1, 1, 0L, 10L, true),
            QuizTrace("s", 2, 1, 0L, 10L, true),
            QuizTrace("s", 3, 2, 0L, 10L, false),
            QuizTrace("s", 4, 2, 0L, 10L, false),
            QuizTrace("s", 5, 2, 0L, 10L, false),
            QuizTrace("s", 6, 2, 0L, 10L, false),
            QuizTrace("s", 7, 2, 0L, 10L, false)
        )
        val report = QuizReportBuilder(traces).build()
        assertNotEquals(report.strongestTopic, report.weakestTopic)
    }

    @Test
    fun TimeP90Computation() {
        val traces = (1..10).map { i ->
            QuizTrace("s", i, 1, 0L, (i * 10).toLong(), i % 2 == 0)
        }
        val report = QuizReportBuilder(traces).build()
        val durations = traces.map { it.answeredAt - it.startedAt }.sorted()
        val p90 = durations[((durations.size - 1) * 0.9).toInt()]
        report.bottlenecks.forEach {
            assertTrue((it.answeredAt - it.startedAt) >= p90)
        }
    }

    @Test
    fun Suggestions_lowAccuracy() {
        val traces = listOf(
            QuizTrace("s", 1, 1, 0L, 1000L, true),
            QuizTrace("s", 2, 1, 0L, 1000L, true),
            QuizTrace("s", 3, 1, 0L, 100L, false),
            QuizTrace("s", 4, 1, 0L, 100L, false),
            QuizTrace("s", 5, 1, 0L, 100L, false)
        )
        val report = QuizReportBuilder(traces).build()
        assertTrue(report.suggestions.contains("Revise 1 – only 40 % correct"))
        assertFalse(report.suggestions.contains("Re-attempt flagged slow questions"))
    }

    @Test
    fun Suggestions_slowWrong() {
        val traces = listOf(
            QuizTrace("s", 1, 1, 0L, 1000L, false),
            QuizTrace("s", 2, 1, 0L, 1000L, false),
            QuizTrace("s", 3, 1, 0L, 1000L, false),
            QuizTrace("s", 4, 1, 0L, 100L, true),
            QuizTrace("s", 5, 1, 0L, 100L, true),
            QuizTrace("s", 6, 1, 0L, 100L, true),
            QuizTrace("s", 7, 1, 0L, 100L, true)
        )
        val report = QuizReportBuilder(traces).build()
        assertTrue(report.suggestions.contains("Re-attempt flagged slow questions"))
    }

    @Test
    fun Suggestions_allGood() {
        val traces = listOf(
            QuizTrace("s", 1, 1, 0L, 100L, true),
            QuizTrace("s", 2, 1, 0L, 100L, true),
            QuizTrace("s", 3, 1, 0L, 100L, true),
            QuizTrace("s", 4, 1, 0L, 100L, true),
            QuizTrace("s", 5, 1, 0L, 100L, true)
        )
        val report = QuizReportBuilder(traces).build()
        assertTrue(report.suggestions.isEmpty())
    }

    @Test
    fun Handles_noAttempts() {
        val traces = listOf(
            QuizTrace("s", 1, 1, 0L, 0L, false),
            QuizTrace("s", 2, 1, 0L, 0L, false)
        )
        val report = QuizReportBuilder(traces).build()
        assertEquals(0, report.attempted)
        assertTrue(report.timePerSection.isEmpty())
        assertTrue(report.bottlenecks.isEmpty())
    }

    @Test
    fun Clamps_negativeDurations() {
        val traces = listOf(
            QuizTrace("s", 1, 1, 100L, 50L, false)
        )
        val report = QuizReportBuilder(traces).build()
        assertEquals(0.0, report.timePerSection.first().avgTime)
    }
}
