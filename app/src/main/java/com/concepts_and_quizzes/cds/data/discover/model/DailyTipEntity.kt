package com.concepts_and_quizzes.cds.data.discover.model

import androidx.room.Entity
import java.time.LocalDate

@Entity(tableName = "daily_tip", primaryKeys = ["date", "conceptId"])
data class DailyTipEntity(
    val date: LocalDate,
    val conceptId: Int
)
