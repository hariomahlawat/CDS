package com.concepts_and_quizzes.cds.data.analytics.scoring

import org.junit.Assert.assertEquals
import org.junit.Test

class ScorerTest {
    @Test
    fun cds_all_correct() {
        val s = Scorer.scoreCounts(CountBreakdown(100, 100, 100, 0, 0), MarkingScheme.CDS)
        assertEquals(100f, s, 0.001f)
    }

    @Test
    fun cds_mixed() {
        val s = Scorer.scoreCounts(CountBreakdown(10, 9, 6, 3, 1), MarkingScheme.CDS)
        // 6*1 - 3*(1/3) = 5
        assertEquals(5f, s, 0.001f)
    }

    @Test
    fun cds_unattempted() {
        val s = Scorer.scoreCounts(CountBreakdown(10, 0, 0, 0, 10), MarkingScheme.CDS)
        assertEquals(0f, s, 0.001f)
    }
}
