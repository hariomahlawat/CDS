package com.concepts_and_quizzes.cds.ui.reports

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import org.junit.Rule
import org.junit.Test

class ReportsSwipeTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun swipeUpAndDownChangesPage() {
        composeTestRule.setContent { ReportsPagerScreen() }

        composeTestRule.onNodeWithText("Last Quiz").assertIsDisplayed()

        composeTestRule.onNodeWithTag("reportsPager")
            .performTouchInput { swipeUp() }
        composeTestRule.onNodeWithText("Trend").assertIsDisplayed()

        composeTestRule.onNodeWithTag("reportsPager")
            .performTouchInput { swipeDown() }
        composeTestRule.onNodeWithText("Last Quiz").assertIsDisplayed()
    }
}

