package com.concepts_and_quizzes.cds.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.concepts_and_quizzes.cds.ui.components.EmptyState
import com.concepts_and_quizzes.cds.ui.components.ErrorState
import com.concepts_and_quizzes.cds.ui.components.LoadingSkeleton
import com.concepts_and_quizzes.cds.ui.components.UiState
import com.concepts_and_quizzes.cds.ui.reports.LastQuizViewModel.LastUi
import com.concepts_and_quizzes.cds.data.analytics.repo.LastUiQuestion
import com.concepts_and_quizzes.cds.data.analytics.repo.LastUiOption

@Composable
fun LastQuizPage(sessionId: String?) {
    val vm: LastQuizViewModel = hiltViewModel()
    LaunchedEffect(sessionId) { vm.load(sessionId) }
    val state by vm.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val s = state) {
            UiState.Loading -> LoadingSkeleton()
            is UiState.Error -> ErrorState(s.message) { vm.refresh() }
            is UiState.Empty -> EmptyState(s.title, s.actionLabel) { vm.refresh() }
            is UiState.Data -> LastContent(s.value)
        }
    }
}

@Composable
private fun LastContent(ui: LastUi) {
    var showWrongOnly by rememberSaveable { mutableStateOf(false) }
    val list = remember(ui, showWrongOnly) {
        if (!showWrongOnly) ui.questions
        else ui.questions.filter { q -> q.options.any { it.isSelected && !it.isCorrect } }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Last quiz", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        // Summary row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile("Questions", "${ui.total}", Modifier.weight(1f))
            StatTile("Attempted", "${ui.attempted}", Modifier.weight(1f))
            StatTile("Correct", "${ui.correct}", Modifier.weight(1f))
            StatTile("Score", "${ui.scoreOn100}/100", Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        // Toggle
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                label = "All",
                selected = !showWrongOnly,
                onClick = { showWrongOnly = false }
            )
            FilterChip(
                label = "Wrong only",
                selected = showWrongOnly,
                onClick = { showWrongOnly = true }
            )
        }

        Spacer(Modifier.height(12.dp))

        // Answer sheet
        if (list.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nothing to review here.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(list, key = { _, q -> q.questionId }) { index, q ->
                    QuestionCard(index + 1, q)
                }
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    val bg = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(onClick = onClick, shape = shape, color = bg, tonalElevation = if (selected) 2.dp else 0.dp) {
        Text(label, color = fg, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
    }
}

@Composable
private fun StatTile(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun QuestionCard(number: Int, q: LastUiQuestion) {
    Card {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text("Q$number", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(q.text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                q.options.forEach { opt -> OptionRow(opt) }
            }
        }
    }
}

@Composable
private fun OptionRow(opt: LastUiOption) {
    val bg = when {
        opt.isCorrect && opt.isSelected -> MaterialTheme.colorScheme.secondaryContainer
        opt.isCorrect -> MaterialTheme.colorScheme.primaryContainer
        opt.isSelected -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
    }
    val border = when {
        opt.isCorrect && opt.isSelected -> MaterialTheme.colorScheme.secondary
        opt.isCorrect -> MaterialTheme.colorScheme.primary
        opt.isSelected -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        color = bg,
        border = BorderStroke(1.dp, border),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = opt.text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
