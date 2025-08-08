package com.concepts_and_quizzes.cds.data.analytics.telemetry

import android.util.Log

private const val TAG = "Telemetry"

/**
 * Simple analytics helper used for logging funnel events.
 * Currently logs to [Log] but can be wired to Firebase in the future.
 */
object Telemetry {

    private interface Logger {
        fun log(event: String, params: Map<String, Any> = emptyMap())
    }

    private object LogcatLogger : Logger {
        override fun log(event: String, params: Map<String, Any>) {
            Log.d(TAG, "$event: $params")
            // TODO: integrate Firebase Analytics once available
        }
    }

    private val logger: Logger = LogcatLogger

    fun logAppOpen() = logger.log("app_open")

    fun logQuizStart(paperId: String) =
        logger.log("quiz_start", mapOf("paperId" to paperId))

    fun logQuestionAnswered(qid: String, correct: Boolean) =
        logger.log(
            "question_answered",
            mapOf("questionId" to qid, "correct" to correct)
        )

    fun logQuizSubmit(paperId: String, score: Int, total: Int) =
        logger.log(
            "quiz_submit",
            mapOf("paperId" to paperId, "score" to score, "total" to total)
        )

    fun logAnalysisCta(sessionId: String) =
        logger.log("analysis_cta", mapOf("sessionId" to sessionId))

    fun logReportShared() = logger.log("report_shared")

    fun logUnlockView(module: String, remaining: Int, progress: Float) {
        logger.log(
            "unlock_view",
            mapOf(
                "module" to module,
                "remaining" to remaining,
                "progress" to progress
            )
        )
    }

    fun logUnlockSuccess(module: String) {
        logger.log("unlock_success", mapOf("module" to module))
    }
}

