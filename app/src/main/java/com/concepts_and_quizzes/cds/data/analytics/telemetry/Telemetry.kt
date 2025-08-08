package com.concepts_and_quizzes.cds.data.analytics.telemetry

import android.util.Log

private const val TAG = "Telemetry"

/**
 * Simple analytics helper used for logging funnel events.
 * Currently logs to [Log] but can be wired to Firebase in the future.
 */
object Telemetry {

    private interface Logger {
        fun log(event: String, params: Map<String, Any>)
    }

    private object FirebaseStubLogger : Logger {
        override fun log(event: String, params: Map<String, Any>) {
            Log.d(TAG, "$event: $params")
            // TODO: integrate Firebase Analytics once available
        }
    }

    private val logger: Logger = FirebaseStubLogger

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

