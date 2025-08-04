package com.concepts_and_quizzes.cds.ui.english.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.core.components.CdsCard

@Composable
fun EnglishDashboardScreen(nav: NavHostController, vm: EnglishDashboardViewModel = hiltViewModel()) {
    val summary by vm.summary.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Text("English Dashboard")
        CdsCard {
            Column(
                Modifier
                    .clickable { nav.navigate("english/pyqp") }
                    .padding(16.dp)
            ) {
                Text("PYQ Summary")
                Text("• Papers: ${summary.papers}")
                Text("• Best: ${summary.best}%")
                Text("• Last: ${summary.last}%")
                Text("View All")
            }
        }
    }
}
