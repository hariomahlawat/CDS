package com.concepts_and_quizzes.cds.data.analytics.availability

import javax.inject.Inject
import com.concepts_and_quizzes.cds.data.analytics.db.AttemptLogDao


data class ModeAvailability(
    val wrongOnlyAvailable: Boolean,
    val timed20Available: Boolean,
    val mixedAvailable: Boolean
)

class ModeAvailabilityRepository @Inject constructor(
    private val attemptLogDao: AttemptLogDao
) {
    suspend fun fetch(): ModeAvailability {
        val wrongCount = attemptLogDao.countWrongAnswers()
        return ModeAvailability(
            wrongOnlyAvailable = wrongCount > 0,
            timed20Available = false,
            mixedAvailable = false
        )
    }
}
