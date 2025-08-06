package com.concepts_and_quizzes.cds.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CdsCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            Color.Transparent
        )
    )
    val colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    )
    val elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    val shape = RoundedCornerShape(24.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            colors = colors,
            elevation = elevation,
            shape = shape
        ) {
            Box(Modifier.background(gradient)) { content() }
        }
    } else {
        Card(
            modifier = modifier,
            colors = colors,
            elevation = elevation,
            shape = shape
        ) {
            Box(Modifier.background(gradient)) { content() }
        }
    }
}
