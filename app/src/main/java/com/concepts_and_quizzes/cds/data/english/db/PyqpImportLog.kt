package com.concepts_and_quizzes.cds.data.english.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pyqp_import_log")
data class PyqpImportLog(
    @PrimaryKey val hash: String,
    val fileName: String,
    val importedAt: Long = System.currentTimeMillis()
)
