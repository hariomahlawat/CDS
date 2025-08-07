package com.concepts_and_quizzes.cds.data.discover.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.concepts_and_quizzes.cds.data.discover.model.BookmarkEntity
import com.concepts_and_quizzes.cds.data.discover.model.ConceptEntity
import com.concepts_and_quizzes.cds.data.discover.model.DailyTipEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface ConceptDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(concepts: List<ConceptEntity>)

    @Query("SELECT * FROM concept WHERE id = :id")
    suspend fun getConcept(id: Int): ConceptEntity

    @Query("SELECT COUNT(*) FROM concept")
    suspend fun countConcepts(): Int

    @Query("SELECT * FROM daily_tip WHERE date = :today")
    suspend fun todaysTips(today: LocalDate): List<DailyTipEntity>

    @Insert
    suspend fun insertDailyTips(tips: List<DailyTipEntity>)

    @Query("DELETE FROM daily_tip")
    suspend fun clearDailyTipHistory()

    @Query("SELECT id FROM concept WHERE id NOT IN (SELECT conceptId FROM daily_tip)")
    suspend fun unservedConceptIds(): List<Int>

    @Query("SELECT id FROM concept")
    suspend fun allConceptIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmark WHERE conceptId = :id")
    suspend fun removeBookmark(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmark WHERE conceptId = :id)")
    fun isBookmarked(id: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmark WHERE conceptId = :id)")
    suspend fun isBookmarkedNow(id: Int): Boolean

    @Transaction
    @Query(
        """
        SELECT concept.* FROM concept 
        INNER JOIN bookmark ON concept.id = bookmark.conceptId 
        ORDER BY bookmarkedAt DESC
        """
    )
    fun bookmarkedConcepts(): Flow<List<ConceptEntity>>
}
