package com.concepts_and_quizzes.cds.ui.english.analysis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.spacedBy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.concepts_and_quizzes.cds.data.analytics.shouldCelebrate
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReport
import com.concepts_and_quizzes.cds.data.analytics.repo.accuracy
import com.concepts_and_quizzes.cds.data.settings.UserPreferences
import com.concepts_and_quizzes.cds.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalysisScreen(
    report: QuizReport,
    prefs: UserPreferences,
) {
    val showCelebrations by prefs.showCelebrations.collectAsState(initial = true)
    val haptic = LocalHapticFeedback.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti_success))
    var play by remember { mutableStateOf(false) }
    if (shouldCelebrate(report.accuracy) && showCelebrations) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        play = true
    }

    Box {
        FlowRow(horizontalArrangement = spacedBy(8.dp)) {
            report.suggestions.forEach {
                AssistChip(onClick = { }, label = { Text(it) })
            }
        }

        AnimatedVisibility(
            visible = play,
            enter = fadeIn() + scaleIn(initialScale = .8f),
            exit = fadeOut(animationSpec = tween(800))
        ) {
            LottieAnimation(
                composition,
                iterations = 1,
                modifier = Modifier.fillMaxSize()
            ) { _, _ -> play = false }
        }
    }
}
