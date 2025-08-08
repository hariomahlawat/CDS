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
