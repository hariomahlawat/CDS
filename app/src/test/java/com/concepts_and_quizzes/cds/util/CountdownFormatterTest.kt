package com.concepts_and_quizzes.cds.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CountdownFormatterTest {
    @Test
    fun `formats zero seconds`() {
        assertEquals("0s", Duration.ZERO.toPretty())
    }

    @Test
    fun `formats minutes and seconds`() {
        assertEquals("1m 5s", (1.minutes + 5.seconds).toPretty())
    }

    @Test
    fun `formats hours and minutes`() {
        assertEquals("1h 1m", (1.hours + 1.minutes).toPretty())
    }

    @Test
    fun `formats days`() {
        assertEquals("1d 1h", 25.hours.toPretty())
    }
}

