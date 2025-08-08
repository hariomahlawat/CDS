package com.concepts_and_quizzes.cds.ui.skeleton

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Skeleton placeholder for peer comparison charts.
 */
@Composable
fun PeerSkeleton(modifier: Modifier = Modifier) {
    Canvas(
        modifier
            .size(120.dp)
    ) {
        val strokeWidth = 4.dp.toPx()
        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
        drawArc(
            color = SkeletonDefaults.strokeColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth),
            topLeft = topLeft,
            size = arcSize
        )
        drawLine(
            color = SkeletonDefaults.strokeColor,
            start = center,
            end = Offset(size.width, center.y),
            strokeWidth = strokeWidth
        )
    }
}

