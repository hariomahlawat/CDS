package com.concepts_and_quizzes.cds.data.english.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.concepts_and_quizzes.cds.data.english.model.PyqpQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PyqpDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<PyqpQuestionEntity>)

    @Query("SELECT DISTINCT paperId FROM pyqp_questions")
    fun getDistinctPaperIds(): Flow<List<String>>

    @Query("SELECT * FROM pyqp_questions WHERE paperId = :paperId")
    fun getQuestionsByPaper(paperId: String): Flow<List<PyqpQuestionEntity>>

    @Query("SELECT COUNT(*) FROM pyqp_questions")
    suspend fun count(): Int
}
