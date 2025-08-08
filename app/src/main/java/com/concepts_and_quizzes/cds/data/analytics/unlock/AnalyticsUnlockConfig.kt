package com.concepts_and_quizzes.cds.data.analytics.unlock

import com.concepts_and_quizzes.cds.core.config.RemoteConfig
import javax.inject.Inject
import org.json.JSONObject

/** Configuration controlling unlock thresholds. */
class AnalyticsUnlockConfig @Inject constructor(remoteConfig: RemoteConfig) {
    val trendQuizzes: Int
    val heatmapQuestions: Int
    val timeGateHours: Int

    init {
        val defaults = mapOf(
            TREND to DEFAULT_TREND,
            HEATMAP to DEFAULT_HEATMAP,
            TIME to DEFAULT_TIME
        )
        val overrides = remoteConfig.getString(RC_KEY)?.let { parseJson(it) } ?: emptyMap()
        val merged = defaults + overrides
        trendQuizzes = merged[TREND] ?: DEFAULT_TREND
        heatmapQuestions = merged[HEATMAP] ?: DEFAULT_HEATMAP
        timeGateHours = merged[TIME] ?: DEFAULT_TIME
    }

    companion object {
        private const val RC_KEY = "unlock_thresholds_v1"
        private const val TREND = "trendQuizzes"
        private const val HEATMAP = "heatmapQuestions"
        private const val TIME = "timeGateHours"
        private const val DEFAULT_TREND = 3
        private const val DEFAULT_HEATMAP = 50
        private const val DEFAULT_TIME = 24

        private fun parseJson(json: String): Map<String, Int> = try {
            val obj = JSONObject(json)
            buildMap {
                obj.keys().forEach { key ->
                    put(key, obj.optInt(key))
                }
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
