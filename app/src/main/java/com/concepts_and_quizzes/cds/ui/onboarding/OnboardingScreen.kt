package com.concepts_and_quizzes.cds.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = remember {
        listOf(
            "Dashboard helps track progress",
            "Concepts contain study material",
            "PYQP lets you practice exams"
        )
    }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage by remember { derivedStateOf { pagerState.currentPage == pages.lastIndex } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(state = pagerState, key = { pages[it] }, modifier = Modifier.weight(1f)) { page ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(pages[page])
            }
        }
        Button(
            onClick = {
                if (!isLastPage) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinish()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (isLastPage) "Start" else "Next")
        }
    }
}
