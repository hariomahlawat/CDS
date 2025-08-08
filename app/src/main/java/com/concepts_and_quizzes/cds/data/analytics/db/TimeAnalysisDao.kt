package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeAnalysisDao {
    @Query(
        """
        SELECT date(startedAt/1000,'unixepoch','localtime') AS day,
               SUM((answeredAt - startedAt)/60000.0) AS minutes
        FROM attempt_log
        WHERE startedAt IS NOT NULL AND answeredAt IS NOT NULL
        GROUP BY day ORDER BY day DESC LIMIT :days
        """
    )
    fun dailyMinutes(days: Int): Flow<List<DailyMinutesDb>>

    @Query(
        """
        SELECT s.sessionId AS sessionId,
               AVG((a.answeredAt - a.startedAt)/1000.0) AS secPerQ,
               AVG(CASE WHEN a.correct THEN 1.0 ELSE 0.0 END) AS accuracy
        FROM sessions s
        JOIN attempt_log a ON a.sessionId = s.sessionId
        WHERE a.startedAt IS NOT NULL AND a.answeredAt IS NOT NULL
        GROUP BY s.sessionId
        ORDER BY s.startedAt
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
