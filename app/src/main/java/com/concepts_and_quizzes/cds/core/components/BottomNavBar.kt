package com.concepts_and_quizzes.cds.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

private data class NavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun CdsBottomNavBar(navController: NavHostController) {
    val items = listOf(
        NavItem("Dashboard", Icons.Filled.Home, "english/dashboard"),
        NavItem("Concepts", Icons.AutoMirrored.Filled.MenuBook, "english/concepts"),
        NavItem("Quiz", Icons.Filled.Edit, "quizHub")
    )
    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            val selected = when (item.route) {
                "english/dashboard" -> currentRoute == "english/dashboard"
                "english/concepts" -> currentRoute?.startsWith("english/concepts") == true
                "quizHub" -> currentRoute == "quizHub" ||
                    currentRoute?.startsWith("english/pyqp") == true ||
                    currentRoute == "analytics/pyq"
                else -> false
            }
            NavigationBarItem(
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
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) }
            )
        }
    }
}
