package com.concepts_and_quizzes.cds.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.concepts_and_quizzes.cds.data.local.entities.PassageEntity

@Dao
interface PassageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassages(passages: List<PassageEntity>)

    @Query("SELECT * FROM passages WHERE examId = :examId")
    suspend fun getPassagesForExam(examId: String): List<PassageEntity>
}
