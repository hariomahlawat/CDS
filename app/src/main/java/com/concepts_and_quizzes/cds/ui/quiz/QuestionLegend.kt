package com.concepts_and_quizzes.cds.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.concepts_and_quizzes.cds.core.theme.flaggedContainer
import com.concepts_and_quizzes.cds.core.theme.flaggedOnContainer

@Composable
fun QuestionLegend() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendChip(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            label = "Answered"
        )
        LegendChip(
            containerColor = MaterialTheme.colorScheme.flaggedContainer,
            contentColor = MaterialTheme.colorScheme.flaggedOnContainer,
            label = "Flagged"
        )
        LegendChip(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            label = "Not Answered"
        )
    }
}

@Composable
private fun LegendChip(containerColor: Color, contentColor: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(containerColor, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(4.dp))
        Text(label, color = contentColor)
    }
}
