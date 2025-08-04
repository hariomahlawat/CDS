package com.concepts_and_quizzes.cds.data.english.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity

@Dao
interface PyqpQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PyqpQuestionEntity>)

    @Query("SELECT COUNT(*) FROM pyqp_questions")
    suspend fun count(): Int

    @Query("SELECT * FROM pyqp_questions WHERE paperId = :paperId")
    suspend fun getByPaper(paperId: String): List<PyqpQuestionEntity>
}
