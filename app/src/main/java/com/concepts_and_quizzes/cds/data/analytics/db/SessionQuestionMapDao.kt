package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SessionQuestionMapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rows: List<SessionQuestionMapEntity>)

    @Query("SELECT * FROM session_question_map WHERE sessionId = :sid ORDER BY questionIndex")
    suspend fun forSession(sid: String): List<SessionQuestionMapEntity>
}
