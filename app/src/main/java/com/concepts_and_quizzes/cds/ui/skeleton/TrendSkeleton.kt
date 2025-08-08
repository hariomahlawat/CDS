package com.concepts_and_quizzes.cds.ui.skeleton

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

/**
 * Skeleton placeholder for trend line charts.
 */
@Composable
fun TrendSkeleton(modifier: Modifier = Modifier) {
    Canvas(
        modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.8f)
            lineTo(size.width * 0.2f, size.height * 0.6f)
            lineTo(size.width * 0.4f, size.height * 0.7f)
            lineTo(size.width * 0.6f, size.height * 0.4f)
            lineTo(size.width * 0.8f, size.height * 0.5f)
            lineTo(size.width, size.height * 0.3f)
        }
        drawPath(
            path = path,
            color = SkeletonDefaults.strokeColor,
            style = Stroke(width = 4.dp.toPx())
        )
    }
}

