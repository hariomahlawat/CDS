package com.concepts_and_quizzes.cds.ui.reports.trend

import android.os.Build
import androidx.annotation.RequiresApi
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao
import com.concepts_and_quizzes.cds.data.analytics.db.TopicTrendPointDb
import com.concepts_and_quizzes.cds.data.analytics.repo.TimeAnalysisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Domain layer for Trend: fetch + aggregate raw data using existing DAOs/repos.
 * No UI dependencies here.
 */
class TrendUseCases @Inject constructor(
    private val attemptsDao: AttemptLogDao,
    private val timeRepo: TimeAnalysisRepository
) {

    /**
     * Streams a combined TrendRaw for the given window.
     *
     * @param windowArg "D7" | "D30" | "LIFETIME"
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun stream(windowArg: String, nowMillis: Long): Flow<TrendRaw> {
        val (startCurrent, startPrev) = cutoffs(windowArg, nowMillis)

        // We assume AttemptLogDao returns per-topic-per-day points since startPrev (prev + current window)
        val trendFlow: Flow<List<TopicTrendPointDb>> = attemptsDao.getTrend(startPrev)
        val effFlow = timeRepo.efficiencySince(startCurrent)  // contains avg sec/Q (and optionally accuracy)
        val bestDayFlow = timeRepo.bestDaySince(startCurrent) // contains best day string label

        return combine(trendFlow, effFlow, bestDayFlow) { topicDaily, effDb, bestDb ->
            val currentStartDay = dayString(startCurrent)
            val (prevPoints, curPoints) = topicDaily.partition { it.day < currentStartDay }

            // Aggregate to daily totals within current window
            val dailyAgg = curPoints.groupBy { it.day }.map { (day, rows) ->
                val attempts = rows.sumOf { it.total }
                val correct = rows.sumOf { it.correct }
                DayAgg(day = day, attempts = attempts, correct = correct)
            }.sortedBy { it.day }

            // Per-topic accuracy for current and previous windows
            val nowByTopic = accuracyByTopic(curPoints)
            val prevByTopic = accuracyByTopic(prevPoints)

            TrendRaw(
                windowArg = windowArg,
                daily = dailyAgg,
                topicAccNow = nowByTopic,
                topicAccPrev = prevByTopic,
                avgSecPerQ = (effDb?.secPerQ ?: 0.0).roundToInt(),
                bestDayLabel = bestDb?.day
            )
        }
    }

    /* ----------------------------- helpers ----------------------------- */

    private fun accuracyByTopic(rows: List<TopicTrendPointDb>): Map<String, Int> =
        rows.groupBy { it.topicId }.mapValues { (_, list) ->
            val t = list.sumOf { it.total }
            val c = list.sumOf { it.correct }
            if (t == 0) 0 else ((c.toFloat() / t) * 100).roundToInt()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cutoffs(arg: String, nowMillis: Long): Pair<Long, Long> {
        val zone = ZoneId.systemDefault()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        val startCurrent = when (arg) {
            "D7" -> today.minusDays(6)
            "D30" -> today.minusDays(29)
            else -> LocalDate.of(1970,1,1)
        }.atStartOfDay(zone).toInstant().toEpochMilli()
        val startPrev = when (arg) {
            "D7" -> today.minusDays(13)
            "D30" -> today.minusDays(59)
            else -> LocalDate.of(1970,1,1)
        }.atStartOfDay(zone).toInstant().toEpochMilli()
        return startCurrent to startPrev
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayString(epochMs: Long): String =
        Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault()).toLocalDate().toString()
}

/** Raw domain output for Trend (UI-agnostic). */
data class TrendRaw(
    val windowArg: String,                       // "D7" | "D30" | "LIFETIME"
    val daily: List<DayAgg>,                     // aggregated by day (current window)
    val topicAccNow: Map<String, Int>,           // topicId -> accuracy %
    val topicAccPrev: Map<String, Int>,          // topicId -> accuracy % (prev window)
    val avgSecPerQ: Int,
    val bestDayLabel: String?
)

data class DayAgg(
    val day: String,     // "YYYY-MM-DD"
    val attempts: Int,
    val correct: Int
)
