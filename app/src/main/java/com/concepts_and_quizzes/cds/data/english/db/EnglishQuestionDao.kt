package com.concepts_and_quizzes.cds.data.english.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.concepts_and_quizzes.cds.data.english.model.EnglishQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnglishQuestionDao {
    @Query("SELECT * FROM english_questions WHERE topicId = :topicId")
    fun getByTopic(topicId: String): Flow<List<EnglishQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<EnglishQuestionEntity>)
}
