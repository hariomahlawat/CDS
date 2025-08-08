package com.concepts_and_quizzes.cds.data.analytics.repo

import com.concepts_and_quizzes.cds.data.analytics.db.SessionDao
import com.concepts_and_quizzes.cds.data.analytics.db.SessionEntity
import com.concepts_and_quizzes.cds.data.analytics.db.TimeAnalysisDao
import javax.inject.Inject

class TimeAnalysisRepository @Inject constructor(
    private val sessionDao: SessionDao,
    private val timeDao: TimeAnalysisDao
) {
    suspend fun upsertSession(session: SessionEntity) {
        sessionDao.upsert(session)
    }

    fun dailyMinutes(days: Int) = timeDao.dailyMinutes(days)

    fun sessionSpeedAccuracy() = timeDao.sessionSpeedAccuracy()
}
