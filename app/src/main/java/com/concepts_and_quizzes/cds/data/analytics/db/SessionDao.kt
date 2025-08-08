package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: SessionEntity)

    @Query(
        """
        UPDATE sessions
        SET endedAt = :endedAt,
            correct = :correct,
            wrong = :wrong,
            unattempted = :unattempted,
            questionCount = :questionCount
        WHERE sessionId = :sessionId
        """
    )
    suspend fun updateSummary(
        sessionId: String,
        endedAt: Long,
        correct: Int,
        wrong: Int,
        unattempted: Int,
        questionCount: Int
    )

    @Query("SELECT * FROM sessions ORDER BY startedAt DESC LIMIT :limit")
    fun latest(limit: Int): Flow<List<SessionEntity>>
}
