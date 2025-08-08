package com.concepts_and_quizzes.cds.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Migration from version 8 to 9 adding selectedIndex to attempt_log and
// creating mapping table for review features.
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE attempt_log ADD COLUMN selectedIndex INTEGER")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS session_question_map(
                sessionId TEXT NOT NULL,
                questionIndex INTEGER NOT NULL,
                questionId TEXT NOT NULL,
                PRIMARY KEY(sessionId, questionIndex)
            )
            """.trimIndent()
        )
    }
}

// Migration adding question_stats table for tracking per-question performance.
val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS question_stats(
                qid TEXT NOT NULL PRIMARY KEY,
                correctCount INTEGER NOT NULL,
                wrongCount INTEGER NOT NULL,
                lastCorrect INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

// Migration adding sessions table and extended timing columns.
val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS sessions(
              sessionId TEXT PRIMARY KEY,
              source TEXT NOT NULL,
              mode TEXT NOT NULL,
              paperId TEXT,
              topicId TEXT,
              subTopic TEXT,
              questionCount INTEGER NOT NULL DEFAULT 0,
              startedAt INTEGER NOT NULL,
              endedAt INTEGER,
              timeLimitSec INTEGER,
              correct INTEGER NOT NULL DEFAULT 0,
              wrong INTEGER NOT NULL DEFAULT 0,
              unattempted INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
        db.execSQL("ALTER TABLE attempt_log ADD COLUMN startedAt INTEGER")
        db.execSQL("ALTER TABLE attempt_log ADD COLUMN answeredAt INTEGER")
        db.execSQL("ALTER TABLE attempt_log ADD COLUMN isSkipped INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE attempt_log ADD COLUMN isTimeout INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE attempt_log ADD COLUMN changeCount INTEGER NOT NULL DEFAULT 0")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_attempt_session_q ON attempt_log(sessionId, questionIndex)")
        db.execSQL("ALTER TABLE english_questions ADD COLUMN subTopic TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE english_questions ADD COLUMN difficulty INTEGER NOT NULL DEFAULT 0")
    }
}
