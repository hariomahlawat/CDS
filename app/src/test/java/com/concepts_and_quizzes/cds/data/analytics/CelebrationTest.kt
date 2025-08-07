package com.concepts_and_quizzes.cds.data.analytics

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CelebrationTest {
    @Test
    fun ShouldCelebrate() {
        assertTrue(shouldCelebrate(0.8f))
        assertFalse(shouldCelebrate(0.7f))
    }
}
