package com.concepts_and_quizzes.cds.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.core.model.Subject

@Composable
fun SubjectChooserScreen(
    navController: NavHostController,
    viewModel: SubjectChooserViewModel = hiltViewModel()
) {
    val subs by viewModel.subscriptions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subjects") },
                actions = {
                    TextButton(onClick = { navController.navigate("dashboard") }) {
                        Text("Go to Dashboard")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(Subject.values()) { subject ->
                val subscribed = subs[subject.skuId] ?: false
                SubjectCard(
                    subject = subject,
                    isSubscribed = subscribed,
                    onClick = { navController.navigate("subject/${subject.id}") }
                )
            }
        }
    }
}
