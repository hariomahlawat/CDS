package com.concepts_and_quizzes.cds.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/** Additional color tokens used across the app. */
val ColorScheme.flaggedContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) {
        primaryContainer.copy(alpha = 0.24f)
    } else {
        primary.copy(alpha = 0.20f)
    }
