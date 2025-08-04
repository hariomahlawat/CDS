package com.concepts_and_quizzes.cds.data.repository

import com.concepts_and_quizzes.cds.core.model.Subject
import com.concepts_and_quizzes.cds.core.model.SubjectProgress
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Repository that provides progress information for all subjects.
 * Currently returns placeholder progress values.
 */
class ProgressRepository @Inject constructor() {
    fun getAllSubjectProgress(): Flow<List<SubjectProgress>> =
        flowOf(Subject.values().map { SubjectProgress(it, 0f) })
}
