package com.concepts_and_quizzes.cds.core.components

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable

@Composable
fun CdsCard(content: @Composable () -> Unit) {
    Card { content() }
}
