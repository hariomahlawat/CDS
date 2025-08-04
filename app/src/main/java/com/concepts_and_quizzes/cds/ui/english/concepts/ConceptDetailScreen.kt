package com.concepts_and_quizzes.cds.ui.english.concepts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun ConceptDetailScreen(id: String, nav: NavHostController, viewModel: ConceptDetailViewModel = hiltViewModel()) {
    val topic by viewModel.topic.collectAsState()
    topic?.let {
        Column(Modifier.padding(16.dp)) {
            Text(it.name, style = MaterialTheme.typography.headlineSmall)
            Text(it.overview, Modifier.padding(top = 8.dp))
        }
    }
}
