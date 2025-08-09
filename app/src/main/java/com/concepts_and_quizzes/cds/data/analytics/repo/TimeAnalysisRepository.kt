package com.concepts_and_quizzes.cds.data.analytics.repo

import com.concepts_and_quizzes.cds.data.analytics.db.DailyMinutesDb
import com.concepts_and_quizzes.cds.data.analytics.db.EfficiencyDb
import com.concepts_and_quizzes.cds.data.analytics.db.SessionDao
import com.concepts_and_quizzes.cds.data.analytics.db.TimeAnalysisDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeAnalysisRepository @Inject constructor(
    sessionDao: SessionDao,
    private val dao: TimeAnalysisDao
) {
    fun dailyMinutes(days: Int): Flow<List<DailyMinutesDb>> = dao.dailyMinutes(days)
    fun totalMinutesSince(cutoff: Long): Flow<Double?> = dao.totalMinutesSince(cutoff)
    fun activeDaysSince(cutoff: Long): Flow<Int> = dao.activeDaysSince(cutoff)
    fun bestDaySince(cutoff: Long): Flow<DailyMinutesDb?> = dao.bestDaySince(cutoff)
    fun efficiencySince(cutoff: Long): Flow<EfficiencyDb?> = dao.efficiencySince(cutoff)
}
