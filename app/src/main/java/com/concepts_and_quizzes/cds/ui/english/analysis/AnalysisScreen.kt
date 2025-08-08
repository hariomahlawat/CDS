package com.concepts_and_quizzes.cds.ui.english.analysis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.concepts_and_quizzes.cds.data.analytics.shouldCelebrate
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReport
import com.concepts_and_quizzes.cds.data.analytics.repo.accuracy
import com.concepts_and_quizzes.cds.data.settings.UserPreferences
import com.concepts_and_quizzes.cds.R
import androidx.navigation.NavController
import android.net.Uri

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalysisScreen(
    report: QuizReport,
    prefs: UserPreferences,
    weakestTopic: String?,
    nav: NavController? = null,
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
        Column(verticalArrangement = spacedBy(16.dp)) {
            FlowRow(horizontalArrangement = spacedBy(8.dp)) {
                report.suggestions.forEach {
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                it,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
            weakestTopic?.let { topic ->
                Column(verticalArrangement = spacedBy(8.dp)) {
                    Button(onClick = {
                        val encoded = Uri.encode(topic)
                        nav?.navigate("english/pyqp?mode=TOPIC&topic=$encoded")
                    }) {
                        Text("Retake weakest topic")
                    }
                    OutlinedButton(onClick = {
                        nav?.navigate("comingSoon/mixed")
                    }) {
                        Text("10 from weak areas")
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = play,
            enter = fadeIn() + scaleIn(initialScale = .8f),
            exit = fadeOut(animationSpec = tween(800))
        ) {
            val anim = rememberLottieAnimatable()
            LaunchedEffect(play, composition) {
                if (play && composition != null) {
                    anim.animate(
                        composition = composition,
                        iterations = 1,
                    )
                    play = false
                }
            }
            LottieAnimation(
                composition = composition,
                progress = { anim.progress },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
