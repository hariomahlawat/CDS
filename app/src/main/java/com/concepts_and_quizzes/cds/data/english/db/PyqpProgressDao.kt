package com.concepts_and_quizzes.cds.data.english.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.concepts_and_quizzes.cds.data.english.model.PyqpProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface PyqpProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: PyqpProgress)

    @Query("SELECT * FROM pyqp_progress")
    fun getAll(): Flow<List<PyqpProgress>>
}
