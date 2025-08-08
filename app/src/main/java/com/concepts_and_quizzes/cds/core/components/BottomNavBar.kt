package com.concepts_and_quizzes.cds.core.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.concepts_and_quizzes.cds.core.config.RemoteConfig
import com.concepts_and_quizzes.cds.core.navigation.BottomBarDestination
import com.concepts_and_quizzes.cds.ui.nav.navigateToTop
import com.concepts_and_quizzes.cds.ui.nav.isAnalytics
import com.concepts_and_quizzes.cds.ui.nav.isConcepts
import com.concepts_and_quizzes.cds.ui.nav.isPyqp
import com.concepts_and_quizzes.cds.ui.nav.isReports

@Composable
fun CdsBottomNavBar(
    navController: NavHostController,
    remoteConfig: RemoteConfig
) {
    val items = BottomBarDestination.entries.filter {
        it != BottomBarDestination.Reports || remoteConfig.getBoolean("reports_tab_enabled")
    }
    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            val selected = when (item) {
                BottomBarDestination.Dashboard -> currentRoute == "english/dashboard"
                BottomBarDestination.Concepts -> isConcepts(currentRoute)
                BottomBarDestination.Quiz -> currentRoute == "quizHub" ||
                    isPyqp(currentRoute) ||
                    isAnalytics(currentRoute)
                BottomBarDestination.Reports -> isReports(currentRoute)
            }
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigateToTop(item.route)
                },
                icon = { Icon(imageVector = item.icon, contentDescription = stringResource(id = item.label)) },
                label = { Text(text = stringResource(id = item.label)) }
            )
        }
    }
}
