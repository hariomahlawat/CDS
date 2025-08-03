package com.concepts_and_quizzes.cds.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.concepts_and_quizzes.cds.data.local.dao.DirectionDao
import com.concepts_and_quizzes.cds.data.local.dao.ExamDao
import com.concepts_and_quizzes.cds.data.local.dao.PassageDao
import com.concepts_and_quizzes.cds.data.local.dao.QuestionDao
import com.concepts_and_quizzes.cds.data.local.entities.DirectionEntity
import com.concepts_and_quizzes.cds.data.local.entities.ExamEntity
import com.concepts_and_quizzes.cds.data.local.entities.PassageEntity
import com.concepts_and_quizzes.cds.data.local.entities.QuestionEntity

@Database(
    entities = [
        ExamEntity::class,
        DirectionEntity::class,
        PassageEntity::class,
        QuestionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun examDao(): ExamDao
    abstract fun directionDao(): DirectionDao
    abstract fun passageDao(): PassageDao
    abstract fun questionDao(): QuestionDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "concepts_quizzes.db"
            ).fallbackToDestructiveMigration().build()
    }
}
