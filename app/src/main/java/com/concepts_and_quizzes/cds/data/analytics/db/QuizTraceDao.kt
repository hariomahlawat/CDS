package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/** Data access for [QuizTrace] records. */
@Dao
interface QuizTraceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrace(trace: QuizTrace)

    @Query("SELECT * FROM quiz_trace WHERE sessionId = :sid")
    suspend fun tracesForSession(sid: String): List<QuizTrace>

    @Query("SELECT sessionId FROM quiz_trace ORDER BY answeredAt DESC LIMIT 1")
    suspend fun latestSessionId(): String?
}
