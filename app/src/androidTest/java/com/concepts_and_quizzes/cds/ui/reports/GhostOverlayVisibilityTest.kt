package com.concepts_and_quizzes.cds.ui.reports

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test
import com.concepts_and_quizzes.cds.data.analytics.unlock.LockedReason

class GhostOverlayVisibilityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun overlayHidesWhenUnlocked() {
        val unlocked = mutableStateOf(false)
        composeTestRule.setContent {
            GhostOverlay(
                unlocked = unlocked.value,
                reason = LockedReason.MoreQuizzes(1),
                skeleton = {},
            ) {
                // empty content
            }
        }

        composeTestRule.onNodeWithContentDescription("Locked").assertIsDisplayed()

        composeTestRule.runOnUiThread { unlocked.value = true }
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithContentDescription("Locked").fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onAllNodesWithContentDescription("Locked").assertCountEquals(0)
    }
}
