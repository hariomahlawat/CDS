package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.concepts_and_quizzes.cds.domain.english.PyqpQuestion
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(
    paperId: String,
    nav: NavController,
    vm: QuizViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    when (val state = ui) {
        is QuizViewModel.QuizUi.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is QuizViewModel.QuizUi.Question -> QuestionPager(vm, state)
        is QuizViewModel.QuizUi.Result -> ResultView(state) {
            vm.saveProgress()
            nav.popBackStack()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QuestionPager(vm: QuizViewModel, state: QuizViewModel.QuizUi.Question) {
    val pagerState = rememberPagerState(initialPage = state.index) { state.total }
    LaunchedEffect(state.index) {
        if (pagerState.currentPage != state.index) pagerState.scrollToPage(state.index)
    }
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != state.index) vm.goTo(pagerState.currentPage)
    }

    var remaining by remember { mutableStateOf(15 * 60) }
    LaunchedEffect(Unit) {
        while (remaining > 0) {
            delay(1000)
            remaining--
        }
        vm.submit()
    }
    var showSubmit by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("${state.index + 1} / ${state.total}")
            Spacer(Modifier.weight(1f))
            Text(String.format("%02d:%02d", remaining / 60, remaining % 60))
            IconButton(onClick = vm::toggleFlag) {
                if (state.flagged) Icon(Icons.Filled.Flag, contentDescription = "Flagged")
                else Icon(Icons.Outlined.Flag, contentDescription = "Flag question")
            }
        }
        LinearProgressIndicator(
            progress = (state.index + 1) / state.total.toFloat(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Spacer(Modifier.height(16.dp))
        HorizontalPager(state.total, state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val q = vm.questionAt(page)
            val sel = vm.answerFor(page)
            QuestionPage(page, q, sel) { vm.select(it) }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = vm::prev, enabled = state.index > 0) { Text("Previous") }
            if (state.index == state.total - 1) {
                Button(onClick = { showSubmit = true }) { Text("Submit") }
            } else {
                Button(onClick = vm::next) { Text("Next") }
            }
        }
    }

    if (showSubmit) {
        AlertDialog(
            onDismissRequest = { showSubmit = false },
            confirmButton = {
                TextButton(onClick = { showSubmit = false; vm.submit() }) { Text("Submit") }
            },
            dismissButton = {
                TextButton(onClick = { showSubmit = false }) { Text("Cancel") }
            },
            title = { Text("Submit quiz?") },
            text = { Text("Are you sure you want to submit?") }
        )
    }
}

@Composable
private fun QuestionPage(
    index: Int,
    question: PyqpQuestion,
    selected: Int?,
    onSelect: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Text("Q${index + 1}. ${question.text}")
        Spacer(Modifier.height(8.dp))
        question.options.forEachIndexed { idx, opt ->
            OptionCard(selected == idx, opt) { onSelect(idx) }
        }
    }
}

@Composable
private fun OptionCard(selected: Boolean, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(text, Modifier.padding(16.dp))
    }
}

@Composable
private fun ResultView(r: QuizViewModel.QuizUi.Result, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = { TextButton(onClick = onClose) { Text("OK") } },
        title = { Text("Result") },
        text = { Text("Score: ${r.correct}/${r.total}") }
    )
}
