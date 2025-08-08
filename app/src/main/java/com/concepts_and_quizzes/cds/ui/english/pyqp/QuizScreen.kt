package com.concepts_and_quizzes.cds.ui.english.pyqp

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import android.app.Activity
import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.concepts_and_quizzes.cds.domain.english.PyqpQuestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.concepts_and_quizzes.cds.ui.quiz.PaletteBottomSheet
import com.concepts_and_quizzes.cds.ui.nav.navigateToTop

@Composable
fun QuizScreen(
    paperId: String,
    nav: NavController,
    vm: QuizViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    val showResult by vm.showResult.collectAsState()
    val result by vm.result.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var lastBack by remember { mutableStateOf(0L) }
    var navigatingToAnalysis by remember { mutableStateOf(false) }

    BackHandler(enabled = !showResult) {
        vm.pause()
        val now = SystemClock.elapsedRealtime()
        if (now - lastBack < 2000) {
            (context as? Activity)?.finish()
        } else {
            lastBack = now
            scope.launch { snackbarHost.showSnackbar("Paused • tap again to exit") }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHost) }) { padding ->
        Box(Modifier.padding(padding)) {
            when (val state = ui) {
                is QuizViewModel.QuizUi.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is QuizViewModel.QuizUi.Page -> QuizPager(vm, state) {
                    navigatingToAnalysis = true
                }
            }
        }
    }

    if (showResult && !navigatingToAnalysis) {
        result?.let { r ->
            ResultView(
                r,
                onDone = {
                    vm.saveProgress()
                    vm.dismissResult()
                    nav.navigateToTop("quizHub")
                },
                onViewAnalytics = {
                    vm.saveProgress()
                    vm.dismissResult()
                    navigatingToAnalysis = true
                }
            )
        }
    }

    if (navigatingToAnalysis) {
        LaunchedEffect(Unit) {
            delay(250)
            vm.onSubmitSuccess(nav)
        }
        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QuizPager(
    vm: QuizViewModel,
    state: QuizViewModel.QuizUi.Page,
    onNavigateToAnalysis: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = state.pageIndex) { state.pageCount }
    LaunchedEffect(state.pageIndex) {
        if (pagerState.currentPage != state.pageIndex) pagerState.scrollToPage(state.pageIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != state.pageIndex) vm.goTo(pagerState.currentPage)
    }

    val remaining by vm.timer.collectAsState()
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, e ->
            when (e) {
                Lifecycle.Event.ON_START,
                Lifecycle.Event.ON_RESUME -> vm.resume()
                Lifecycle.Event.ON_PAUSE -> vm.flush()
                Lifecycle.Event.ON_STOP -> vm.pause()
                else -> Unit
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }
    val view = LocalView.current
    var announcedTen by remember { mutableStateOf(false) }
    var announcedFive by remember { mutableStateOf(false) }
    LaunchedEffect(remaining) {
        if (!announcedTen && remaining == 10 * 60) {
            view.announceForAccessibility("10 minutes remaining")
            announcedTen = true
        }
        if (!announcedFive && remaining == 5 * 60) {
            view.announceForAccessibility("5 minutes remaining")
            announcedFive = true
        }
    }
    var showSubmit by remember { mutableStateOf(false) }
    var showPalette by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            when (val page = state.page) {
                is QuizViewModel.QuizPage.Question -> {
                    Text("${page.questionIndex + 1} / ${state.questionCount}")
                    Spacer(Modifier.weight(1f))
                    val color = when {
                        remaining <= 5 * 60 -> MaterialTheme.colorScheme.error
                        remaining <= 10 * 60 -> MaterialTheme.colorScheme.tertiary
                        else -> LocalContentColor.current
                    }
                    Text(String.format("%02d:%02d", remaining / 60, remaining % 60), color = color)
                    IconButton(onClick = { showPalette = true }) {
                        Icon(Icons.Filled.GridOn, contentDescription = "Question palette")
                    }
                    IconButton(onClick = vm::toggleFlag) {
                        if (page.flagged) Icon(Icons.Filled.Flag, contentDescription = "Flagged")
                        else Icon(Icons.Outlined.Flag, contentDescription = "Flag question")
                    }
                }
                is QuizViewModel.QuizPage.Intro -> {
                    Spacer(Modifier.weight(1f))
                    val color = when {
                        remaining <= 5 * 60 -> MaterialTheme.colorScheme.error
                        remaining <= 10 * 60 -> MaterialTheme.colorScheme.tertiary
                        else -> LocalContentColor.current
                    }
                    Text(String.format("%02d:%02d", remaining / 60, remaining % 60), color = color)
                    IconButton(onClick = { showPalette = true }) {
                        Icon(Icons.Filled.GridOn, contentDescription = "Question palette")
                    }
                }
            }
        }
        val progress = when (val p = state.page) {
            is QuizViewModel.QuizPage.Question -> (p.questionIndex + 1) / state.questionCount.toFloat()
            is QuizViewModel.QuizPage.Intro -> {
                val next = if (state.pageIndex < state.pageCount - 1) vm.pageContent(state.pageIndex + 1) else null
                if (next is QuizViewModel.QuizPage.Question) next.questionIndex / state.questionCount.toFloat() else 0f
            }
        }
        LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        color = ProgressIndicatorDefaults.linearColor,
        trackColor = ProgressIndicatorDefaults.linearTrackColor,
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
        Spacer(Modifier.height(16.dp))
        HorizontalPager(state = pagerState, key = { it }, modifier = Modifier.weight(1f)) { page ->
            when (val p = vm.pageContent(page)) {
                is QuizViewModel.QuizPage.Intro -> IntroPage(p)
                is QuizViewModel.QuizPage.Question -> QuestionPage(
                    p.questionIndex + 1,
                    p.question,
                    p.userAnswerIndex,
                    pagerState.currentPage,
                    onSelect = { vm.select(it) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            when (val page = state.page) {
                is QuizViewModel.QuizPage.Question -> {
                    TextButton(onClick = vm::prev, enabled = page.questionIndex > 0) { Text("Previous") }
                    if (page.questionIndex == state.questionCount - 1) {
                        Button(onClick = { showSubmit = true }) { Text("Submit") }
                    } else {
                        Button(onClick = vm::next) { Text("Next") }
                    }
                }
                is QuizViewModel.QuizPage.Intro -> {
                    Spacer(Modifier.weight(1f))
                    Button(onClick = vm::next) { Text("Continue") }
                }
            }
        }
    }

    if (showSubmit) {
        AlertDialog(
            onDismissRequest = { showSubmit = false },
            confirmButton = {
                TextButton(onClick = {
                    showSubmit = false
                    vm.submitQuiz()
                    vm.saveProgress()
                    vm.dismissResult()
                    onNavigateToAnalysis()
                }) { Text("Submit") }
            },
            dismissButton = {
                TextButton(onClick = { showSubmit = false }) { Text("Cancel") }
            },
            title = { Text("Submit quiz?") },
            text = { Text("Are you sure you want to submit?") }
        )
    }

    if (showPalette) {
        PaletteBottomSheet(
            entries = vm.questionPalette(),
            onSelect = {
                vm.goToQuestion(it)
                showPalette = false
            },
            onDismiss = { showPalette = false }
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun QuestionPage(
    number: Int,
    question: PyqpQuestion,
    selected: Int?,
    currentPage: Int,
    onSelect: (Int) -> Unit
) {
    var show by remember { mutableStateOf(false) }
    LaunchedEffect(currentPage) { show = false }
    val view = LocalView.current
    LaunchedEffect(number) { view.announceForAccessibility("Question $number") }

    val hasInfo = question.direction != null || question.passage != null
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (hasInfo) {
            val label = if (question.passage != null) {
                if (show) "Hide" else "Show passage"
            } else {
                if (show) "Hide" else "Show direction"
            }
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { show = !show },
                color = MaterialTheme.colorScheme.secondaryContainer
            ) { Text(label, Modifier.padding(8.dp)) }
            AnimatedVisibility(show) {
                val config = LocalConfiguration.current
                val maxHeight = (config.screenHeightDp * 0.7f).dp
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = maxHeight)
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                ) {
                    Column(
                        Modifier
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(8.dp)
                    ) {
                        question.direction?.let {
                            Text(it)
                            if (question.passage != null) Spacer(Modifier.height(8.dp))
                        }
                        if (question.passage != null) {
                            question.passageTitle?.let {
                                Text(it, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                            }
                            Text(question.passage)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Text("Q$number. ${question.text}")
        Spacer(Modifier.height(8.dp))
        question.options.forEachIndexed { idx, opt ->
            OptionCard(idx, selected == idx, opt.text) { onSelect(idx) }
        }
    }
}

@Composable
private fun IntroPage(intro: QuizViewModel.QuizPage.Intro) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        intro.direction?.let {
            Text(it)
            Spacer(Modifier.height(8.dp))
        }
        intro.passageTitle?.let {
            Text(it, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
        }
        intro.passage?.let { Text(it) }
    }
}

@Composable
private fun OptionCard(index: Int, selected: Boolean, text: String, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val letter = ('A' + index)
    val firstWords = text.split(" ").take(3).joinToString(" ")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics {
                contentDescription = "Option $letter. $firstWords"
                this.selected = selected
                role = Role.RadioButton
            },
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
            view.announceForAccessibility("Option $letter selected")
        },
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        )
    ) {
        Box(
            Modifier.background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            )
        ) {
            Text(text, Modifier.padding(16.dp))
        }
    }
}


@Composable
private fun ResultView(
    r: QuizViewModel.QuizResult,
    onDone: () -> Unit,
    onViewAnalytics: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDone,
        confirmButton = { TextButton(onClick = onDone) { Text("Done") } },
        title = { Text("Result") },
        text = {
            Column {
                Text("Score: ${r.correct}/${r.total}")
                Spacer(Modifier.height(12.dp))
                Button(onClick = onViewAnalytics) {
                    Text("View analytics")
                }
            }
        }
    )
}
