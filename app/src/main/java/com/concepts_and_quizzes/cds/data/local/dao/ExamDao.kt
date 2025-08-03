package com.concepts_and_quizzes.cds.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.concepts_and_quizzes.cds.data.local.entities.ExamEntity

@Dao
interface ExamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity)

    @Query("SELECT * FROM exams WHERE examId = :id")
    suspend fun getExam(id: String): ExamEntity?
}
