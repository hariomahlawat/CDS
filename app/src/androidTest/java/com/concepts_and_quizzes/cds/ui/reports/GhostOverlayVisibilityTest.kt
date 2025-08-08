package com.concepts_and_quizzes.cds.ui.reports

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test
import com.concepts_and_quizzes.cds.data.analytics.unlock.AnalyticsModule
import com.concepts_and_quizzes.cds.data.analytics.unlock.LockedReason
import com.concepts_and_quizzes.cds.data.analytics.unlock.ModuleStatus

class GhostOverlayVisibilityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun overlayHidesWhenUnlocked() {
        val status = mutableStateOf(
            ModuleStatus(
                module = AnalyticsModule.TREND,
                unlocked = false,
                progress = 0f,
                reason = LockedReason.MoreQuizzes(1)
            )
        )
        composeTestRule.setContent {
            GhostOverlay(
                status = status.value,
                skeleton = {},
            ) {
                // empty content
            }
        }

        composeTestRule.onNodeWithContentDescription("Locked").assertIsDisplayed()

        composeTestRule.runOnUiThread { status.value = status.value.copy(unlocked = true, reason = null) }
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithContentDescription("Locked").fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onAllNodesWithContentDescription("Locked").assertCountEquals(0)
    }
}
