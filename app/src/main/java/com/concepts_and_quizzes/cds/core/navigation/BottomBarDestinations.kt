package com.concepts_and_quizzes.cds.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.concepts_and_quizzes.cds.R

/** Destinations displayed in the bottom navigation and navigation rail. */
enum class BottomBarDestination(
    val route: String,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Dashboard("english/dashboard", Icons.Filled.Home, R.string.dashboard),
    Concepts("english/concepts", Icons.AutoMirrored.Filled.MenuBook, R.string.concepts),
    Quiz("quizHub", Icons.Filled.Edit, R.string.quiz),
    Reports("reports", Icons.Outlined.BarChart, R.string.reports)
}
