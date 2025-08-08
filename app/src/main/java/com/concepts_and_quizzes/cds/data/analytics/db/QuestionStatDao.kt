package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogEntity

@Dao
interface QuestionStatDao {
    @Query("SELECT * FROM question_stats WHERE qid = :qid")
    suspend fun get(qid: String): QuestionStatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stat: QuestionStatEntity)

    @Transaction
    suspend fun upsert(qid: String, correct: Boolean) {
        val current = get(qid)
        if (current == null) {
            val stat = QuestionStatEntity(
                qid = qid,
                correctCount = if (correct) 1 else 0,
                wrongCount = if (correct) 0 else 1,
                lastCorrect = correct
            )
            insert(stat)
        } else {
            val stat = current.copy(
                correctCount = current.correctCount + if (correct) 1 else 0,
                wrongCount = current.wrongCount + if (correct) 0 else 1,
                lastCorrect = correct
            )
            insert(stat)
        }
    }

    @Transaction
    suspend fun updateFromAttempts(attempts: List<AttemptLogEntity>) {
        attempts.forEach { upsert(it.qid, it.correct) }
    }

    @Query("SELECT COUNT(*) FROM question_stats WHERE lastCorrect = 0 OR wrongCount >= correctCount")
    suspend fun countWrong(): Int

    @Query("SELECT qid FROM question_stats WHERE lastCorrect = 0 OR wrongCount >= correctCount LIMIT :limit")
    suspend fun wrongQids(limit: Int): List<String>

    @Query(
        """
        SELECT qs.qid
        FROM question_stats qs
        JOIN pyqp_questions pq ON pq.qid = qs.qid
        WHERE (qs.lastCorrect = 0 OR qs.wrongCount >= qs.correctCount)
          AND pq.topic = :topicId
        LIMIT :limit
        """
    )
    suspend fun wrongQidsForTopic(topicId: String, limit: Int): List<String>
}

