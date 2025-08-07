package com.concepts_and_quizzes.cds.data.discover.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "bookmark")
data class BookmarkEntity(
    @PrimaryKey val conceptId: Int,
    val bookmarkedAt: Instant = Instant.now()
)
