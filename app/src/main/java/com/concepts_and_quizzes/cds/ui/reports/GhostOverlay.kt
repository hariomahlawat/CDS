package com.concepts_and_quizzes.cds.ui.reports

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.concepts_and_quizzes.cds.data.analytics.unlock.LockedReason
import com.concepts_and_quizzes.cds.data.analytics.unlock.ModuleStatus
import com.concepts_and_quizzes.cds.data.analytics.telemetry.Telemetry
import com.concepts_and_quizzes.cds.util.toPretty

/**
 * Wraps [content] with a ghost overlay displayed when the analytics module is locked.
 * Shows a skeleton preview, lock icon and call to action which fades away once unlocked.
 */
@Composable
fun GhostOverlay(
    status: ModuleStatus,
    modifier: Modifier = Modifier,
    skeleton: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val unlocked = status.unlocked
    val reason = status.reason

    var loggedView by remember { mutableStateOf(false) }
    LaunchedEffect(unlocked) {
        val moduleName = status.module.name
        val remaining = (reason as? LockedReason.MoreQuizzes)?.remaining ?: 0
        if (!unlocked && !loggedView) {
            Telemetry.logUnlockView(moduleName, remaining, status.progress)
            loggedView = true
        } else if (unlocked && loggedView) {
            Telemetry.logUnlockSuccess(moduleName)
            loggedView = false
        }
    }

    Box(modifier) {
        Box(Modifier.fillMaxSize(), content = content)
        AnimatedContent(
            targetState = unlocked,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ghostOverlay",
        ) { isUnlocked ->
            if (!isUnlocked) {
                Box(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.3f),
                        contentAlignment = Alignment.Center,
                        content = skeleton,
                    )
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = "Locked")
                        reason?.let {
                            val msg = when (it) {
                                is LockedReason.MoreQuizzes -> "Complete ${it.remaining} more quizzes"
                                is LockedReason.TimeGate -> "Unlock in ${it.duration.toPretty()}"
                            }
                            Text(msg, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
