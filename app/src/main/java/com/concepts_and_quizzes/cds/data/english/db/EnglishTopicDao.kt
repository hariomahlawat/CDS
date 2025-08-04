package com.concepts_and_quizzes.cds.data.english.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.concepts_and_quizzes.cds.data.english.model.EnglishTopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnglishTopicDao {
    @Query("SELECT * FROM english_topics")
    fun getAll(): Flow<List<EnglishTopicEntity>>

    @Query("SELECT * FROM english_topics WHERE id = :id")
    fun getById(id: String): Flow<EnglishTopicEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<EnglishTopicEntity>)

    @Query("SELECT COUNT(*) FROM english_topics")
    suspend fun count(): Int
}
