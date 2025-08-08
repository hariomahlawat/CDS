package com.concepts_and_quizzes.cds.data.analytics.scoring

/** Defines marking scheme for scoring quizzes. */
data class MarkingScheme(
    val markPerCorrect: Float,
    val negativePerWrong: Float,
    val markPerUnattempted: Float = 0f
) {
    companion object {
        // UPSC CDS: +1 for correct, -1/3 for wrong, 0 for unattempted
        val CDS = MarkingScheme(markPerCorrect = 1f, negativePerWrong = 1f / 3f)
    }
}

data class CountBreakdown(
    val total: Int,
    val attempted: Int,
    val correct: Int,
    val wrong: Int,
    val unattempted: Int
)

object Scorer {
    /** Returns the raw score for given counts under [scheme]. */
    fun scoreCounts(counts: CountBreakdown, scheme: MarkingScheme = MarkingScheme.CDS): Float {
        return counts.correct * scheme.markPerCorrect -
            counts.wrong * scheme.negativePerWrong +
            counts.unattempted * scheme.markPerUnattempted
    }

    /** Rounds the score to two decimal places for display. */
    fun roundScore(value: Float): Float = ((value * 100).toInt() / 100f)
}
