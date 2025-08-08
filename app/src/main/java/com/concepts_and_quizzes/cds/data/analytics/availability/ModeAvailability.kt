package com.concepts_and_quizzes.cds.data.analytics.availability

import javax.inject.Inject
import com.concepts_and_quizzes.cds.data.analytics.db.QuestionStatDao


data class ModeAvailability(
    val wrongOnlyAvailable: Boolean,
    val timed20Available: Boolean,
    val mixedAvailable: Boolean
)

class ModeAvailabilityRepository @Inject constructor(
    private val questionStatDao: QuestionStatDao
) {
    suspend fun fetch(): ModeAvailability {
        val wrongCount = questionStatDao.countWrong()
        return ModeAvailability(
            wrongOnlyAvailable = wrongCount > 0,
            timed20Available = false,
            mixedAvailable = false
        )
    }
}
