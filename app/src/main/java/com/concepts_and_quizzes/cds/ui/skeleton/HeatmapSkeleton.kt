package com.concepts_and_quizzes.cds.ui.skeleton

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Skeleton placeholder for heat map charts.
 */
@Composable
fun HeatmapSkeleton(modifier: Modifier = Modifier) {
    Canvas(
        modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val rows = 5
        val columns = 7
        val cellWidth = size.width / columns
        val cellHeight = size.height / rows
        for (r in 0 until rows) {
            for (c in 0 until columns) {
                drawRect(
                    color = SkeletonDefaults.strokeColor,
                    topLeft = Offset(c * cellWidth, r * cellHeight),
                    size = Size(cellWidth, cellHeight),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

