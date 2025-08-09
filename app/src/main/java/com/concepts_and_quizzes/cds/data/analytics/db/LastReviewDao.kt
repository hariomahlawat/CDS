package com.concepts_and_quizzes.cds.data.analytics.db


import androidx.room.Dao
import androidx.room.Query

@Dao
interface LastReviewDao {

    /* ---------- Latest completed session ---------- */

    // Preferred: if you store completion in sessions table
    @Query("""
        SELECT id 
        FROM sessions 
        WHERE endedAt IS NOT NULL 
        ORDER BY endedAt DESC 
        LIMIT 1
    """)
    suspend fun lastCompletedSessionFromSessions(): String?

    // Fallback: infer latest by most recent activity in attempt_log
    @Query("""
        SELECT sessionId 
        FROM attempt_log
        WHERE sessionId IS NOT NULL
        GROUP BY sessionId
        ORDER BY MAX(timestamp) DESC
        LIMIT 1
    """)
    suspend fun lastSessionFromAttempts(): String?

    /* ---------- Session → questions map (full quiz roster) ---------- */

    // All questions shown in the quiz (preserves quiz order if you keep ordinal)
    @Query("""
        SELECT sessionId, qid, ordinal
        FROM session_question_map
        WHERE sessionId = :sessionId
        ORDER BY ordinal ASC
    """)
    suspend fun mappedQuestions(sessionId: String): List<MappedQuestionRow>

    /* ---------- User selections (latest per question) ---------- */

    // Latest selectedIndex for each question (null if never selected)
    @Query("""
        SELECT a.qid            AS qid,
               a.selectedIndex  AS selectedIndex,
               MAX(a.timestamp) AS lastTs
        FROM attempt_log a
        WHERE a.sessionId = :sessionId
        GROUP BY a.qid
    """)
    suspend fun latestSelections(sessionId: String): List<SelectionRowDb>

    /* ---------- Question bank (Pyqp) ---------- */

    // Get question text + options + correct index in one shot
    @Query("""
        SELECT id              AS qid,
               question        AS stem,       -- rename if your column is 'stem'
               optionA         AS optionA,
               optionB         AS optionB,
               optionC         AS optionC,
               optionD         AS optionD,
               correctIndex    AS correctIndex
        FROM pyqp_question      -- rename if your table name differs
        WHERE id IN (:ids)
    """)
    suspend fun pyqpQuestionsByIds(ids: List<Long>): List<PyqpQuestionRow>
}

/* ---------- DB DTOs ---------- */

data class MappedQuestionRow(
    val sessionId: String,
    val qid: Long,
    val ordinal: Int
)

data class SelectionRowDb(
    val qid: Long,
    val selectedIndex: Int?
)

data class PyqpQuestionRow(
    val qid: Long,
    val stem: String,
    val optionA: String?,
    val optionB: String?,
    val optionC: String?,
    val optionD: String?,
    val correctIndex: Int
)
