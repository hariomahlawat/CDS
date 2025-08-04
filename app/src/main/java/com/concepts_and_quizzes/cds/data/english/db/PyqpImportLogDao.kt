package com.concepts_and_quizzes.cds.data.english.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PyqpImportLogDao {
    @Query("SELECT hash FROM pyqp_import_log")
    suspend fun getAllHashes(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: PyqpImportLog)
}
