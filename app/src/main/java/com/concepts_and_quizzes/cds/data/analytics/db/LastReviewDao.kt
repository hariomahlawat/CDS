package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LastReviewDao {

    /* ---------- Latest completed session ---------- */

    @Query(
        """
        SELECT sessionId
        FROM sessions
        WHERE endedAt IS NOT NULL
        ORDER BY endedAt DESC
        LIMIT 1
        """
    )
    suspend fun lastCompletedSessionFromSessions(): String?

    @Query(
        """
        SELECT sessionId
        FROM attempt_log
        WHERE sessionId IS NOT NULL
        GROUP BY sessionId
        ORDER BY MAX(timestamp) DESC
        LIMIT 1
        """
    )
    suspend fun lastSessionFromAttempts(): String?

    /* ---------- Session → quiz roster (preserve order) ---------- */

    @Query(
        """
        SELECT sessionId,
               questionId AS qid,
               questionIndex AS ordinal
        FROM session_question_map
        WHERE sessionId = :sessionId
        ORDER BY questionIndex ASC
        """
    )
    suspend fun mappedQuestions(sessionId: String): List<MappedQuestionRow>

    /* ---------- Latest user selections per question ---------- */

    @Query(
        """
        SELECT a.qid            AS qid,
               a.selectedIndex  AS selectedIndex,
               MAX(a.timestamp) AS lastTs
        FROM attempt_log a
        WHERE a.sessionId = :sessionId
        GROUP BY a.qid
        """
    )
    suspend fun latestSelections(sessionId: String): List<SelectionRowDb>

    /* ---------- Question bank (Pyqp) ---------- */

    @Query(
        """
        SELECT qid          AS qid,
               question     AS stem,
               optionA      AS optionA,
               optionB      AS optionB,
               optionC      AS optionC,
               optionD      AS optionD,
               correctIndex AS correctIndex
        FROM pyqp_questions
        WHERE qid IN (:ids)
        """
    )
    suspend fun pyqpQuestionsByIds(ids: List<String>): List<PyqpQuestionRow>
}

/* ---------- DB DTOs ---------- */

data class MappedQuestionRow(
    val sessionId: String,
    val qid: String,
    val ordinal: Int
)

data class SelectionRowDb(
    val qid: String,
    val selectedIndex: Int?
)

data class PyqpQuestionRow(
    val qid: String,
    val stem: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int
)
