package com.concepts_and_quizzes.cds.core.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.concepts_and_quizzes.cds.core.config.RemoteConfig
import com.concepts_and_quizzes.cds.core.navigation.BottomBarDestination

@Composable
fun CdsNavigationRail(
    navController: NavHostController,
    remoteConfig: RemoteConfig
) {
    val items = BottomBarDestination.entries.filter {
        it != BottomBarDestination.Reports || remoteConfig.getBoolean("reports_tab_enabled")
    }
    NavigationRail {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            val selected = when (item) {
                BottomBarDestination.Dashboard -> currentRoute == "english/dashboard"
                BottomBarDestination.Concepts -> currentRoute?.startsWith("english/concepts") == true
                BottomBarDestination.Quiz -> currentRoute == "quizHub" ||
                    currentRoute?.startsWith("english/pyqp") == true ||
                    currentRoute?.startsWith("analytics") == true
                BottomBarDestination.Reports -> currentRoute == "reports"
            }
            NavigationRailItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = stringResource(id = item.label)) },
                label = { Text(text = stringResource(id = item.label)) }
            )
        }
    }
}
