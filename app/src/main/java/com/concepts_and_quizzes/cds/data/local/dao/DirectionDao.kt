package com.concepts_and_quizzes.cds.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.concepts_and_quizzes.cds.data.local.entities.DirectionEntity

@Dao
interface DirectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirections(directions: List<DirectionEntity>)

    @Query("SELECT * FROM directions WHERE examId = :examId")
    suspend fun getDirectionsForExam(examId: String): List<DirectionEntity>
}
