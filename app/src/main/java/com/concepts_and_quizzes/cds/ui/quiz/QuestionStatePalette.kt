package com.concepts_and_quizzes.cds.ui.quiz

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Dialog showing a grid of question numbers with state colours.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionStatePalette(
    entries: List<QuestionStatePaletteEntry>,
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
                    modifier = Modifier.height(200.dp)
                ) {
                    items(entries, key = { it.questionIndex }) { e ->
                        val (container, content) = when {
                            e.flagged && !e.answered ->
                                MaterialTheme.colorScheme.flaggedContainer to MaterialTheme.colorScheme.flaggedOnContainer
                            e.flagged ->
                                MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
                            e.answered ->
                                MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
                            else ->
                                MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(4.dp)
                                .background(container, RoundedCornerShape(8.dp))
                                .clickable { onSelect(e.questionIndex) }
                                .height(48.dp)
                                .fillMaxWidth()
                        ) {
                            Text("${e.questionIndex + 1}", color = content)
                        }
                    }
                }
            }
        }
    )
}

data class QuestionStatePaletteEntry(
    val questionIndex: Int,
    val answered: Boolean,
    val flagged: Boolean
)
