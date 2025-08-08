package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Metadata for a practice or paper solving session.
 */
@Entity(
    tableName = "sessions",
    indices = [
        Index(value = ["startedAt"], name = "idx_session_started"),
        Index(value = ["source", "startedAt"], name = "idx_session_source_started")
    ]
)
data class SessionEntity(
    @PrimaryKey val sessionId: String,
    val source: String,
    val mode: String,
    val paperId: String? = null,
    val topicId: String? = null,
    val subTopic: String? = null,
    val questionCount: Int = 0,
    val startedAt: Long,
    val endedAt: Long? = null,
    val timeLimitSec: Int? = null,
    val correct: Int = 0,
    val wrong: Int = 0,
    val unattempted: Int = 0
)
