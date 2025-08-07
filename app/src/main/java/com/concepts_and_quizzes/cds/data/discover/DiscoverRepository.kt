package com.concepts_and_quizzes.cds.data.discover

import com.concepts_and_quizzes.cds.data.discover.db.ConceptDao
import com.concepts_and_quizzes.cds.data.discover.model.BookmarkEntity
import com.concepts_and_quizzes.cds.data.discover.model.ConceptEntity
import com.concepts_and_quizzes.cds.data.discover.model.DailyTipEntity
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@Singleton
class DiscoverRepository @Inject constructor(
    private val dao: ConceptDao
) {
    private val tipsPerDay = 3

    val todaysTips: Flow<List<ConceptEntity>> = flow {
        emit(getOrGenerate().map { dao.getConcept(it.conceptId) })
    }.flowOn(Dispatchers.IO)

    private suspend fun getOrGenerate(): List<DailyTipEntity> {
        val today = LocalDate.now()
        val existing = dao.todaysTips(today)
        if (existing.size == tipsPerDay) return existing

        var pool = dao.unservedConceptIds()
        if (pool.size < tipsPerDay) {
            dao.clearDailyTipHistory()
            pool = dao.unservedConceptIds()
        }
        val draw = pool.shuffled().take(tipsPerDay).map { DailyTipEntity(today, it) }
        dao.insertDailyTips(draw)
        return draw
    }

    suspend fun toggleBookmark(id: Int) {
        if (dao.isBookmarkedNow(id)) dao.removeBookmark(id)
        else dao.addBookmark(BookmarkEntity(id))
    }

    fun isBookmarked(id: Int): Flow<Boolean> = dao.isBookmarked(id)

    suspend fun getConcept(id: Int): ConceptEntity = dao.getConcept(id)

    fun bookmarkedConcepts(): Flow<List<ConceptEntity>> = dao.bookmarkedConcepts()
}
