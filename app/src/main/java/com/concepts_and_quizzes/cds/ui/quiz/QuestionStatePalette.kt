package com.concepts_and_quizzes.cds.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.concepts_and_quizzes.cds.ui.english.pyqp.QuizViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionStatePalette(
    entries: List<QuizViewModel.PaletteEntry>,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Jump to question") },
        text = {
            Column {
                QuestionLegend()
                Spacer(Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(entries.size) { idx ->
                        val e = entries[idx]
                        val container = when {
                            e.flagged -> MaterialTheme.colorScheme.flaggedContainer
                            e.answered -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        val content = when {
                            e.flagged -> MaterialTheme.colorScheme.flaggedOnContainer
                            e.answered -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(48.dp)
                                .background(container, RoundedCornerShape(8.dp))
                                .clickable { onSelect(e.questionIndex) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${e.questionIndex + 1}", color = content)
                        }
                    }
                }
            }
        }
    )
}
