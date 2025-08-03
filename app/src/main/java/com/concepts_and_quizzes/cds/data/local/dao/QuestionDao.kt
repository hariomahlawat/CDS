package com.concepts_and_quizzes.cds.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.concepts_and_quizzes.cds.data.local.entities.QuestionEntity
import com.concepts_and_quizzes.cds.data.local.entities.QuestionWithDirectionAndPassage
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Transaction
    @Query(
        """
        SELECT * FROM questions
        WHERE examId = :examId
        ORDER BY questionNumber ASC
        """
    )
    fun getQuestionsWithDetails(examId: String): Flow<List<QuestionWithDirectionAndPassage>>
}
