package com.concepts_and_quizzes.cds.data.analytics.repo

import com.concepts_and_quizzes.cds.data.analytics.db.QuizTrace
import com.concepts_and_quizzes.cds.data.analytics.db.QuizTraceDao
import javax.inject.Inject

/** Repository that aggregates quiz traces into reports. */
class QuizReportRepository @Inject constructor(
    private val dao: QuizTraceDao
) {
    suspend fun insertTrace(trace: QuizTrace) = dao.insertTrace(trace)

    suspend fun analyse(sessionId: String): QuizReport {
        val traces = dao.tracesForSession(sessionId)
        return QuizReportBuilder(traces).build()
    }

    suspend fun latestSessionId(): String? = dao.latestSessionId()
}
