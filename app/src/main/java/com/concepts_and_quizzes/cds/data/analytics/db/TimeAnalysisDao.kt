package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeAnalysisDao {

    @Query(
        """
        SELECT date(timestamp/1000,'unixepoch','localtime') AS day,
               SUM(durationMs)/60000.0 AS minutes
        FROM attempt_log
        GROUP BY day
        ORDER BY day DESC
        LIMIT :days
        """
    )
    fun dailyMinutes(days: Int): Flow<List<DailyMinutesDb>>

    @Query(
        """
        SELECT SUM(durationMs)/60000.0
        FROM attempt_log
        WHERE timestamp >= :cutoff
        """
    )
    fun totalMinutesSince(cutoff: Long): Flow<Double?>

    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT date(timestamp/1000,'unixepoch','localtime') d
            FROM attempt_log
            WHERE timestamp >= :cutoff
            GROUP BY d
        )
        """
    )
    fun activeDaysSince(cutoff: Long): Flow<Int>

    @Query(
        """
        SELECT date(timestamp/1000,'unixepoch','localtime') AS day,
               SUM(durationMs)/60000.0 AS minutes
        FROM attempt_log
        WHERE timestamp >= :cutoff
        GROUP BY day
        ORDER BY minutes DESC
        LIMIT 1
        """
    )
    fun bestDaySince(cutoff: Long): Flow<DailyMinutesDb?>

    @Query(
        """
        SELECT AVG(durationMs)/1000.0 AS secPerQ,
               AVG(CASE WHEN correct THEN 1.0 ELSE 0.0 END) AS accuracy
        FROM attempt_log
        WHERE timestamp >= :cutoff
        """
    )
    fun efficiencySince(cutoff: Long): Flow<EfficiencyDb?>
}

/* DTOs */

data class DailyMinutesDb(
    val day: String,    // "YYYY-MM-DD"
    val minutes: Double
)

data class EfficiencyDb(
    val secPerQ: Double?,
    val accuracy: Double?
)
