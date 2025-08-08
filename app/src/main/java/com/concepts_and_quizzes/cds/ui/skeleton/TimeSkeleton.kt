package com.concepts_and_quizzes.cds.ui.skeleton

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

/**
 * Skeleton placeholder for time distribution charts.
 */
@Composable
fun TimeSkeleton(modifier: Modifier = Modifier) {
    Canvas(
        modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val bars = 7
        val barSpacing = size.width / (bars * 2f)
        for (i in 0 until bars) {
            val x = barSpacing * (i * 2 + 1)
            val barHeight = size.height * (0.3f + (i % 3) * 0.2f)
            drawLine(
                color = SkeletonDefaults.strokeColor,
                start = Offset(x, size.height),
                end = Offset(x, size.height - barHeight),
                strokeWidth = barSpacing,
                cap = StrokeCap.Round
            )
        }
    }
}

