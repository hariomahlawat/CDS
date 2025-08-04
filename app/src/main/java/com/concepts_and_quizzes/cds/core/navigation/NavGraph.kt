package com.concepts_and_quizzes.cds.core.navigation

import androidx.compose.runtime.Composable

sealed class Destination(val route: String) {
    data object Dashboard : Destination("dashboard")
    data object Concepts : Destination("concepts")
    data object English : Destination("english")
}

@Composable
fun AppNavGraph() {
    // NavHost setup will be added here
}
