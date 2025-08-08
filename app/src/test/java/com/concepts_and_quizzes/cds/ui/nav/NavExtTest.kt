package com.concepts_and_quizzes.cds.ui.nav

import android.content.Context
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.navArgument
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

    @Test
    fun navigateToTopUsesLatestArguments() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val controller = TestNavHostController(context)
        controller.navigatorProvider.addNavigator(ComposeNavigator())
        controller.setGraph(
            controller.navigatorProvider.createGraph(startDestination = "home") {
                composable("home") {}
                composable(
                    route = "target?arg={arg}",
                    arguments = listOf(androidx.navigation.navArgument("arg") { defaultValue = "first" })
                ) {}
            }
        )

        controller.navigate("target?arg=first")
        controller.navigateToTop("home")
        controller.navigateToTop("target?arg=second")

        val arg = controller.currentBackStackEntry?.arguments?.getString("arg")
        assertEquals("second", arg)
    }
}
