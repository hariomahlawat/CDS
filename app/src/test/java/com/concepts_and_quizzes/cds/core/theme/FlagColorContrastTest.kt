package com.concepts_and_quizzes.cds.core.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min
import org.junit.Assert.assertTrue
import org.junit.Test

class FlagColorContrastTest {
    @Test
    fun flaggedColorsMeetContrastGuidelines() {
        val lightScheme = lightColorScheme()
        val darkScheme = darkColorScheme()

        val lightBg = lightScheme.surface
        val darkBg = darkScheme.surface

        val lightFlaggedContainer = lightScheme.primary.copy(alpha = 0.20f).compositeOver(lightBg)
        val darkFlaggedContainer = darkScheme.primaryContainer.copy(alpha = 0.24f).compositeOver(darkBg)

        val lightFlaggedOnContainer = Color(0xFF00315D)
        val darkFlaggedOnContainer = Color(0xFFCAE5FF)

        assertContrast(lightFlaggedContainer, lightFlaggedOnContainer)
        assertContrast(darkFlaggedContainer, darkFlaggedOnContainer)
    }

    private fun assertContrast(a: Color, b: Color) {
        val ratio = contrastRatio(a, b)
        assertTrue("Contrast ratio $ratio is below 4.5", ratio >= 4.5f)
    }

    private fun contrastRatio(a: Color, b: Color): Float {
        val l1 = a.luminance()
        val l2 = b.luminance()
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return ((lighter + 0.05f) / (darker + 0.05f)).toFloat()
    }
}
