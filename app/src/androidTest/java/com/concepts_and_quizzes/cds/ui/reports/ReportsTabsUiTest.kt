package com.concepts_and_quizzes.cds.ui.reports

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ReportsTabsUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun tabClickChangesPage() {
        composeRule.setContent { ReportsPagerScreen() }

        composeRule.onNodeWithText("No reports").assertIsDisplayed()

        composeRule.onNodeWithText("Trend").performClick()

        composeRule.onNodeWithText("No trend data").assertIsDisplayed()
    }
}

