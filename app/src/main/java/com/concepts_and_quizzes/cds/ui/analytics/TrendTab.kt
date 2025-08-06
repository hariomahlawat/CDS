package com.concepts_and_quizzes.cds.ui.analytics

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.concepts_and_quizzes.cds.data.analytics.db.TrendPoint

@Composable
fun TrendTab(points: List<TrendPoint>, highContrast: Boolean) {
    val last = points.takeLast(10)
    if (last.isEmpty()) {
        Text("Attempt at least one paper to see your trend.", Modifier.padding(24.dp))
        return
    }

    SparkLineChart(last, highContrast)
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        last.forEach {
            val pct = "%.0f".format(it.percent)
            Text(pct, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun SparkLineChart(points: List<TrendPoint>, highContrast: Boolean) {
    val max = points.maxOf { it.percent }
    val anim = remember { Animatable(0f) }
    val context = LocalContext.current
    val animationsDisabled = remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    }
    LaunchedEffect(points, animationsDisabled) {
        if (animationsDisabled) {
            anim.snapTo(1f)
        } else {
            anim.animateTo(1f, tween(600))
        }
    }

    val desc = points.joinToString {
        val pct = "%.0f".format(it.percent)
        "${it.week} : $pct percent"
    }

    val baseColor = if (highContrast) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary
    val effect = if (highContrast) PathEffect.dashPathEffect(floatArrayOf(10f, 10f)) else null

    Canvas(
        Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(8.dp)
            .semantics { contentDescription = desc }
    ) {
        val stepX = if (points.size == 1) 0f else size.width / (points.size - 1)
        val path = Path()
        points.forEachIndexed { i, p ->
            val x = i * stepX
            val y = size.height * (1 - p.percent / max.coerceAtLeast(1f))
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        clipRect(right = size.width * anim.value) {
            drawPath(
                path = path,
                color = baseColor,
                style = Stroke(width = 4.dp.toPx(), pathEffect = effect)
            )
        }
    }
}

