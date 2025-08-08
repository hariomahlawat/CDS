package com.concepts_and_quizzes.cds.ui.english.analysis

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.test.core.app.ApplicationProvider
import com.concepts_and_quizzes.cds.data.analytics.repo.QuizReport
import com.concepts_and_quizzes.cds.data.analytics.repo.TopicSummary
import com.concepts_and_quizzes.cds.data.settings.UserPreferences
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AnalysisScreenNavigationTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun retakeWeakestNavigatesToTopicPractice() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = UserPreferences(context)
        val report = QuizReport(
            total = 10,
            attempted = 10,
            correct = 5,
            wrong = 5,
            strongestTopic = null,
            weakestTopic = null,
            timePerSection = listOf(
                TopicSummary(topicId = 1, accuracy = 50.0, avgTime = 0.0, attempts = 6)
            ),
            bottlenecks = emptyList(),
            suggestions = emptyList()
        )
        val nav = object : NavHostController(context) {
            var route: String? = null
            override fun navigate(route: String, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?) {
                this.route = route
            }
        }
        composeRule.setContent { AnalysisScreen(report, prefs, nav) }
        composeRule.onNodeWithText("Retake weakest topic").performClick()
        assertTrue(nav.route!!.startsWith("english/pyqp?mode=TOPIC"))
    }
}
