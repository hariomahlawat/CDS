package com.concepts_and_quizzes.cds.data.analytics.repo

import com.concepts_and_quizzes.cds.data.analytics.AnalyticsConfig
import com.concepts_and_quizzes.cds.data.analytics.db.QuizTrace
import kotlin.math.roundToInt

/** Builds a [QuizReport] from a list of [QuizTrace] records. */
class QuizReportBuilder(private val traces: List<QuizTrace>) {

    fun build(): QuizReport {
        val total = traces.size
        val attempted = traces.count { it.answeredAt != 0L }
        val correct = traces.count { it.isCorrect }
        val wrong = attempted - correct

        val perTopic = traces.groupBy { it.topicId }.map { (topicId, list) ->
            val attempts = list.count { it.answeredAt != 0L }
            val corrects = list.count { it.isCorrect }
            val avgTime = list.filter { it.answeredAt != 0L }
                .map { it.answeredAt - it.startedAt }
                .average()
            val acc = if (attempts == 0) 0.0 else corrects * 100.0 / attempts
            TopicSummary(topicId, acc, avgTime, attempts)
        }

        val eligibleTopics = perTopic.filter { it.attempts >= AnalyticsConfig.MIN_ATTEMPTS_FOR_TOPIC }
        val strongest = eligibleTopics.maxByOrNull { it.accuracy }?.topicId
        val weakest = eligibleTopics.minByOrNull { it.accuracy }?.topicId

        val durations = traces.filter { it.answeredAt != 0L }
            .map { it.answeredAt - it.startedAt }
            .sorted()
        val p90 = if (durations.isEmpty()) 0L else durations[((durations.size - 1) * 0.9).roundToInt()]
        val bottlenecks = traces
            .filter { !it.isCorrect && (it.answeredAt - it.startedAt) >= p90 }
            .sortedByDescending { it.answeredAt - it.startedAt }
            .take(3)

        val globalAvg = durations.average()
        val suggestions = mutableListOf<String>()
        perTopic.forEach { t ->
            if (t.attempts >= AnalyticsConfig.MIN_ATTEMPTS_FOR_TOPIC && t.accuracy < 50) {
                suggestions += "Revise ${t.topicId} – only ${t.accuracy.roundToInt()} % correct"
            }
            if (globalAvg > 0 && t.avgTime > AnalyticsConfig.SPEED_THR_RATIO * globalAvg) {
                suggestions += "Speed up ${t.topicId} – avg ${(t.avgTime / 1000.0).roundToInt()} s"
            }
        }
        val unattempted = total - attempted
        if (unattempted > 0) {
            suggestions += "Attempt every question – $unattempted left blank"
        }
        if (bottlenecks.size >= 3) {
            suggestions += "Re-attempt flagged slow questions"
        }

        return QuizReport(
            total = total,
            attempted = attempted,
            correct = correct,
            wrong = wrong,
            strongestTopic = strongest,
            weakestTopic = weakest,
            timePerSection = perTopic,
            bottlenecks = bottlenecks,
            suggestions = suggestions
        )
    }
}
