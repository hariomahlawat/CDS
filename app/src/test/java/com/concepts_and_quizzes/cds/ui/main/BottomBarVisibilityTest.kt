package com.concepts_and_quizzes.cds.ui.main

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.concepts_and_quizzes.cds.core.components.CdsBottomNavBar
import com.concepts_and_quizzes.cds.core.config.RemoteConfig
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BottomBarVisibilityTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun reportsTabVisibleWhenFlagTrue() {
        val rc = object : RemoteConfig {
            override fun getBoolean(key: String) = true
        }
        composeRule.setContent {
            CdsBottomNavBar(rememberNavController(), rc)
        }
        composeRule.onNodeWithText("Reports").assertExists()
    }

    @Test
    fun reportsTabHiddenWhenFlagFalse() {
        val rc = object : RemoteConfig {
            override fun getBoolean(key: String) = false
        }
        composeRule.setContent {
            CdsBottomNavBar(rememberNavController(), rc)
        }
        composeRule.onNodeWithText("Reports").assertDoesNotExist()
    }
}
