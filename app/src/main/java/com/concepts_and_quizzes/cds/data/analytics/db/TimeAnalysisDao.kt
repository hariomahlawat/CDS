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
        SELECT a.sessionId AS sessionId,
               AVG(a.durationMs)/1000.0 AS secPerQ,
               AVG(CASE WHEN a.correct THEN 1.0 ELSE 0.0 END) AS accuracy
        FROM attempt_log a
        WHERE a.sessionId IS NOT NULL
        GROUP BY a.sessionId
        ORDER BY MAX(a.timestamp) DESC
        """
    )
    fun sessionSpeedAccuracy(): Flow<List<SessionSpeedAccuracyDb>>
}

data class DailyMinutesDb(
    val day: String,
    val minutes: Double
)

data class SessionSpeedAccuracyDb(
    val sessionId: String,
    val secPerQ: Double,
    val accuracy: Double
)
