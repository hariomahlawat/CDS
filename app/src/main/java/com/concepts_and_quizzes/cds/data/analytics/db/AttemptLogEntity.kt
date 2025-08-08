package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a single question attempt. One row per question per quiz attempt.
 */
@Entity(
    tableName = "attempt_log",
    indices = [
        Index(value = ["qid"], name = "idx_attempt_qid"),
        Index(value = ["timestamp"], name = "idx_attempt_time"),
        Index(value = ["sessionId", "questionIndex"], name = "idx_attempt_session_q")
    ]
)
data class AttemptLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val qid: String,
    val quizId: String,
    val correct: Boolean,
    val flagged: Boolean,
    val durationMs: Int,
    val timestamp: Long,
    // --- fields for detailed review ---
    val sessionId: String? = null,
    val questionIndex: Int = 0,
    val selectedIndex: Int? = null,
    val startedAt: Long? = null,
    val answeredAt: Long? = null,
    val isSkipped: Boolean = false,
    val isTimeout: Boolean = false,
    val changeCount: Int = 0,
)
