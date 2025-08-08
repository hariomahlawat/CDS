package com.concepts_and_quizzes.cds.ui.english.analysis

import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReport

/**
 * Returns the topic id (as string) of the weakest topic in the given report.
 * Topics with fewer than [minAttempts] attempts are ignored to avoid noise.
 */
fun weakestTopic(report: QuizReport, minAttempts: Int = 6): String? =
    report.timePerSection
        .filter { it.attempts >= minAttempts }
        .minByOrNull { it.accuracy }
        ?.topicId
        ?.toString()
