package com.concepts_and_quizzes.cds.ui.nav

import android.content.Context
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import org.junit.Assert.assertEquals
import org.junit.Test

class NavExtTest {
    @Test
    fun navigateToTopKeepsSingleInstance() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val controller = TestNavHostController(context)
        controller.navigatorProvider.addNavigator(ComposeNavigator())
        controller.setGraph(
            controller.navigatorProvider.createGraph(startDestination = "home") {
                composable("home") {}
                composable("target") {}
            }
        )

        controller.navigateToTop("target")
        controller.navigateToTop("target")

        val count = controller.backQueue.count { it.destination.route == "target" }
        assertEquals(1, count)
    }
}
