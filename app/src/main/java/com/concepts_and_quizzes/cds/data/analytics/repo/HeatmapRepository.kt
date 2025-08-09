package com.concepts_and_quizzes.cds.data.analytics.repo

import com.concepts_and_quizzes.cds.data.analytics.db.DailyHeatDb
import com.concepts_and_quizzes.cds.data.analytics.db.HeatmapDao
import com.concepts_and_quizzes.cds.data.analytics.db.HourlyHeatDb
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeatmapRepository @Inject constructor(
    private val dao: HeatmapDao
) {
    fun dailySince(cutoff: Long): Flow<List<DailyHeatDb>> = dao.dailyHeatmapSince(cutoff)
    fun hourlySince(cutoff: Long): Flow<List<HourlyHeatDb>> = dao.hourlyHeatmapSince(cutoff)
}
