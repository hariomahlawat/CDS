package com.concepts_and_quizzes.cds.core.model

import com.concepts_and_quizzes.cds.core.model.Subject

/**
 * Represents a user's progress for a particular subject.
 *
 * @param subject the subject for which progress is tracked
 * @param percentComplete value between 0f and 1f representing completion percentage
 */
data class SubjectProgress(
    val subject: Subject,
    val percentComplete: Float
)
