package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.concepts_and_quizzes.cds.domain.english.PyqpQuestion

@Composable
fun QuizScreen(
    paperId: String,
    nav: NavController,
    vm: QuizViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    when (val state = ui) {
        is QuizViewModel.QuizUi.Loading -> CircularProgressIndicator()
        is QuizViewModel.QuizUi.SectionIntro -> SectionIntroView(state, vm::continueFromIntro)
        is QuizViewModel.QuizUi.Question -> QuestionView(state, vm)
        is QuizViewModel.QuizUi.Result -> ResultView(state) {
            vm.saveProgress()
            nav.popBackStack()
        }
    }
}

@Composable
private fun QuestionView(q: QuizViewModel.QuizUi.Question, vm: QuizViewModel) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        CollapsibleHeader(q.question)
        Text("Q${q.index + 1}. ${q.question.text}")
        q.question.options.forEachIndexed { idx, opt ->
            RadioButtonRow(
                selected = q.userAnswerIndex == idx,
                onSelect = { vm.select(idx) },
                label = opt
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = vm::next) { Text("Next") }
    }
}

@Composable
private fun SectionIntroView(intro: QuizViewModel.QuizUi.SectionIntro, onContinue: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                intro.direction?.let {
                    Text("Directions", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(it)
                    if (intro.passage != null) Spacer(Modifier.height(16.dp))
                }
                intro.passage?.let {
                    Text(intro.passageTitle ?: "Passage", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(it)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onContinue, modifier = Modifier.align(Alignment.End)) { Text("Continue") }
    }
}

@Composable
private fun CollapsibleHeader(q: PyqpQuestion) {
    val hasText = q.direction != null || q.passage != null
    if (!hasText) return
    var expanded by remember(q.id) { mutableStateOf(false) }
    val label = if (q.passage != null) "Show passage" else "Show direction"
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Text(
            if (expanded) "Hide ${if (q.passage != null) "passage" else "direction"}" else label,
            modifier = Modifier.padding(8.dp)
        )
    }
    if (expanded) {
        val maxHeight = LocalConfiguration.current.screenHeightDp.dp * 0.7f
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeight)
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                q.direction?.let {
                    Text("Directions", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(it)
                    if (q.passage != null) Spacer(Modifier.height(16.dp))
                }
                q.passage?.let {
                    Text(q.passageTitle ?: "Passage", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(it)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ResultView(r: QuizViewModel.QuizUi.Result, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onClose) { Text("OK") }
        },
        title = { Text("Result") },
        text = { Text("Score: ${r.correct}/${r.total}") }
    )
}

@Composable
private fun RadioButtonRow(selected: Boolean, onSelect: () -> Unit, label: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onSelect)
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}
