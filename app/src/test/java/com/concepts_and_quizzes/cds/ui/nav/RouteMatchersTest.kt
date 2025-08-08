package com.concepts_and_quizzes.cds.ui.nav

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteMatchersTest {
    @Test
    fun reportsMatcher() {
        assertTrue(isReports("reports"))
        assertTrue(isReports("reports?startPage=1"))
        assertFalse(isReports("english/dashboard"))
    }

    @Test
    fun pyqpMatcher() {
        assertTrue(isPyqp("english/pyqp"))
        assertTrue(isPyqp("english/pyqp/42"))
        assertFalse(isPyqp("english/concepts"))
    }

    @Test
    fun analyticsMatcher() {
        assertTrue(isAnalytics("analytics"))
        assertTrue(isAnalytics("analytics/trend"))
        assertFalse(isAnalytics("analysis/123"))
    }

    @Test
    fun conceptsMatcher() {
        assertTrue(isConcepts("english/concepts"))
        assertTrue(isConcepts("english/concepts/2"))
        assertFalse(isConcepts("quizHub"))
    }
}

