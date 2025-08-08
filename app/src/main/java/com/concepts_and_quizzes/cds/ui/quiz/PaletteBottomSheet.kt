package com.concepts_and_quizzes.cds.ui.quiz

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.concepts_and_quizzes.cds.core.theme.flaggedContainer
import com.concepts_and_quizzes.cds.core.theme.flaggedOnContainer
import com.concepts_and_quizzes.cds.ui.english.pyqp.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PaletteBottomSheet(
    entries: List<QuizViewModel.PaletteEntry>,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Jump to question", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            QuestionLegend()
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.flaggedOnContainer)
                Spacer(Modifier.width(4.dp))
                Text("Flagged question")
            }
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.heightIn(max = 200.dp)
            ) {
                items(entries, key = { it.questionIndex }) { e ->
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
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${e.questionIndex + 1}", color = content)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
