package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun QuizScreen(
    paperId: String,
    nav: NavController,
    vm: QuizViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    when (val state = ui) {
        is QuizViewModel.QuizUi.Loading -> CircularProgressIndicator()
        is QuizViewModel.QuizUi.Question -> QuestionView(state, vm)
        is QuizViewModel.QuizUi.Result -> ResultView(state) {
            vm.saveProgress()
            nav.popBackStack()
        }
    }
}

@Composable
private fun QuestionView(q: QuizViewModel.QuizUi.Question, vm: QuizViewModel) {
    Column(Modifier.padding(16.dp)) {
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
