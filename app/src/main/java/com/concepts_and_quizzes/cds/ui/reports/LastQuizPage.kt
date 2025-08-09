package com.concepts_and_quizzes.cds.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.concepts_and_quizzes.cds.ui.components.EmptyState
import com.concepts_and_quizzes.cds.ui.components.ErrorState
import com.concepts_and_quizzes.cds.ui.components.LoadingSkeleton
import com.concepts_and_quizzes.cds.ui.components.UiState
import com.concepts_and_quizzes.cds.ui.reports.LastQuizViewModel.LastUi
import com.concepts_and_quizzes.cds.data.analytics.repo.LastUiOption
import com.concepts_and_quizzes.cds.data.analytics.repo.LastUiQuestion
import kotlin.math.roundToInt

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
        // Title + segmented filter in one line
        item {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Review your last quiz",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                SegmentedFilter(
                    wrongOnly = wrongOnly,
                    onChange = { wrongOnly = it }
                )
            }
        }

        // Professional summary card (scrolls with content)
        item {
            SummaryCard(
                total = ui.total,
                attempted = ui.attempted,
                correct = ui.correct,
                scoreOn100 = ui.scoreOn100
            )
        }

        if (list.isEmpty()) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
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
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        IconTextChip(
            label = "All",
            icon = Icons.Outlined.DoneAll,
            selected = !wrongOnly,
            onClick = { onChange(false) }
        )
        IconTextChip(
            label = "Wrong only",
            icon = Icons.Outlined.ErrorOutline,
            selected = wrongOnly,
            onClick = { onChange(true) }
        )
    }
}

@Composable
private fun IconTextChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(onClick = onClick, color = bg, shape = MaterialTheme.shapes.large) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, color = fg)
        }
    }
}

/* ----------------------------- Summary card (PRO) ----------------------------- */

@Composable
private fun SummaryCard(
    total: Int,
    attempted: Int,
    correct: Int,
    scoreOn100: Int
) {
    val wrong = (attempted - correct).coerceAtLeast(0)
    val unattempted = (total - attempted).coerceAtLeast(0)
    val accuracy = if (attempted > 0) correct * 100f / attempted else 0f
    val attemptRate = if (total > 0) attempted * 100f / total else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {

            // Header
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Quiz Performance",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                PillBadge(
                    icon = Icons.Outlined.EmojiEvents,
                    text = "CDS score"
                )
            }

            Spacer(Modifier.height(12.dp))

            // Main row: big ring + 2x2 metric grid
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreDonut(
                    score = scoreOn100.coerceIn(0, 100),
                    modifier = Modifier.size(92.dp)
                )

                Spacer(Modifier.width(16.dp))

                Column(Modifier.weight(1f)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricTile(
                            label = "Questions",
                            value = total.toString(),
                            sub = null,
                            modifier = Modifier.weight(1f)
                        )
                        MetricTile(
                            label = "Attempted",
                            value = attempted.toString(),
                            sub = "${attemptRate.roundToInt()}% of total",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricTile(
                            label = "Correct",
                            value = correct.toString(),
                            sub = "${accuracy.roundToInt()}% accuracy",
                            modifier = Modifier.weight(1f)
                        )
                        MetricTile(
                            label = "Incorrect",
                            value = wrong.toString(),
                            sub = null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            // Distribution bar + legend
            TriProgressBar(
                correct = correct,
                wrong = wrong,
                unattempted = unattempted
            )
            Spacer(Modifier.height(8.dp))
            LegendRow(
                correct = correct,
                wrong = wrong,
                unattempted = unattempted
            )
        }
    }
}

/* ------------------------------ Building blocks ------------------------------ */

@Composable
private fun PillBadge(icon: ImageVector, text: String) {
    val bg = MaterialTheme.colorScheme.secondaryContainer
    val fg = MaterialTheme.colorScheme.onSecondaryContainer
    Surface(color = bg, shape = MaterialTheme.shapes.large) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, color = fg, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ScoreDonut(score: Int, modifier: Modifier = Modifier) {
    val tint = when {
        score >= 75 -> MaterialTheme.colorScheme.primary
        score >= 40 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    Box(modifier, contentAlignment = Alignment.Center) {
        val colorDrawArc = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            val inset = stroke.width / 2f

            // Track
            drawArc(
                color = colorDrawArc,
                startAngle = -90f, sweepAngle = 360f,
                useCenter = false,
                style = stroke,
                topLeft = Offset(inset, inset),
                size = Size(size.width - stroke.width, size.height - stroke.width)
            )
            // Progress
            drawArc(
                color = tint,
                startAngle = -90f,
                sweepAngle = 360f * (score / 100f),
                useCenter = false,
                style = stroke,
                topLeft = Offset(inset, inset),
                size = Size(size.width - stroke.width, size.height - stroke.width)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$score", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("of 100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    sub: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (sub != null) {
            Text(
                sub,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TriProgressBar(correct: Int, wrong: Int, unattempted: Int) {
    val total = (correct + wrong + unattempted).coerceAtLeast(1)
    val pc = correct.toFloat() / total
    val pw = wrong.toFloat() / total
    val pu = unattempted.toFloat() / total

    Row(
        Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(6.dp))
    ) {
        Box(
            Modifier
                .weight(pc.takeIf { it > 0f } ?: 0.0001f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
        )
        Box(
            Modifier
                .weight(pw.takeIf { it > 0f } ?: 0.0001f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.error)
        )
        Box(
            Modifier
                .weight(pu.takeIf { it > 0f } ?: 0.0001f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}

@Composable
private fun LegendRow(correct: Int, wrong: Int, unattempted: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendDot(text = "Correct $correct", color = MaterialTheme.colorScheme.primary)
        LegendDot(text = "Wrong $wrong", color = MaterialTheme.colorScheme.error)
        LegendDot(text = "Unattempted $unattempted", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LegendDot(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/* ------------------------------ Question cards ----------------------------- */

@Composable
private fun QuestionCardCompact(number: Int, q: LastUiQuestion, wrongOnly: Boolean) {
    var expanded by rememberSaveable(q.questionId) { mutableStateOf(false) }
    val optionsToShow = remember(wrongOnly, expanded, q) {
        if (!wrongOnly || expanded) q.options else optionsWrongOnly(q.options)
    }

    Card {
        Column(Modifier.fillMaxWidth().padding(10.dp)) {
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
        color = Color.Transparent,
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
