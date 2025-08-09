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
import com.concepts_and_quizzes.cds.data.analytics.repo.LastUiOption
import com.concepts_and_quizzes.cds.data.analytics.repo.LastUiQuestion

@Composable
fun LastQuizPage(sessionId: String?) {
    val vm: LastQuizViewModel = hiltViewModel()
    LaunchedEffect(sessionId) { vm.load(sessionId) }
    val state by vm.state.collectAsState()

    when (val s = state) {
        UiState.Loading -> LoadingSkeleton()
        is UiState.Error -> ErrorState(s.message) { vm.refresh() }
        is UiState.Empty -> EmptyState(s.title, s.actionLabel) { vm.refresh() }
        is UiState.Data  -> LastContent(ui = s.value)
    }
}

/* --------------------------------- Content --------------------------------- */

@Composable
private fun LastContent(ui: LastUi) {
    var wrongOnly by rememberSaveable { mutableStateOf(false) }

    val list = remember(ui, wrongOnly) {
        if (!wrongOnly) ui.questions
        else ui.questions.filter { q -> q.options.any { it.isSelected && !it.isCorrect } }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Title + segmented filter on a single line (saves space)
        item {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Last quiz",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                SegmentedFilter(
                    wrongOnly = wrongOnly,
                    onChange = { wrongOnly = it }
                )
            }
        }

        // Compact summary chips (one row)
        item {
            SummaryCompactBar(
                total = ui.total,
                attempted = ui.attempted,
                correct = ui.correct,
                scoreOn100 = ui.scoreOn100
            )
        }

        if (list.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nothing to review here.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            itemsIndexed(list, key = { _, q -> q.questionId }) { index, q ->
                QuestionCardCompact(
                    number = index + 1,
                    q = q,
                    wrongOnly = wrongOnly
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

/* ------------------------------ Header widgets ----------------------------- */

@Composable
private fun SegmentedFilter(wrongOnly: Boolean, onChange: (Boolean) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TinyFilterChip(label = "All", selected = !wrongOnly) { onChange(false) }
        TinyFilterChip(label = "Wrong only", selected = wrongOnly) { onChange(true) }
    }
}

@Composable
private fun TinyFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        color = bg,
        tonalElevation = if (selected) 1.dp else 0.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Text(label, color = fg, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

@Composable
private fun SummaryCompactBar(
    total: Int,
    attempted: Int,
    correct: Int,
    scoreOn100: Int
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CompactStatChip("Q", "$total", Modifier.weight(1f))
        CompactStatChip("Att.", "$attempted", Modifier.weight(1f))
        CompactStatChip("Correct", "$correct", Modifier.weight(1f))
        CompactStatChip("Score", "$scoreOn100/100", Modifier.weight(1f))
    }
}

@Composable
private fun CompactStatChip(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* ------------------------------ Question cards ----------------------------- */

@Composable
private fun QuestionCardCompact(number: Int, q: LastUiQuestion, wrongOnly: Boolean) {
    // In "Wrong only" mode, show only selected wrong + correct (expandable)
    var expanded by rememberSaveable(q.questionId) { mutableStateOf(false) }
    val optionsToShow = remember(wrongOnly, expanded, q) {
        if (!wrongOnly || expanded) q.options else optionsWrongOnly(q.options)
    }

    Card {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(
                "Q$number",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(q.text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                optionsToShow.forEach { opt -> OptionRowCompact(opt) }
            }

            if (wrongOnly && !expanded && optionsToShow.size < q.options.size) {
                Spacer(Modifier.height(8.dp))
                TinyLink("Show all options") { expanded = true }
            }
        }
    }
}

@Composable
private fun TinyLink(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent,
        shape = MaterialTheme.shapes.small
    ) {
        Text(text, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun OptionRowCompact(opt: LastUiOption) {
    val bg = when {
        opt.isCorrect && opt.isSelected -> MaterialTheme.colorScheme.secondaryContainer
        opt.isCorrect -> MaterialTheme.colorScheme.primaryContainer
        opt.isSelected -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
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
                .padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/* --------------------------------- Helpers --------------------------------- */

private fun optionsWrongOnly(all: List<LastUiOption>): List<LastUiOption> {
    val selectedWrong = all.firstOrNull { it.isSelected && !it.isCorrect }
    val correct = all.firstOrNull { it.isCorrect }
    return listOfNotNull(selectedWrong, correct).distinct()
}
