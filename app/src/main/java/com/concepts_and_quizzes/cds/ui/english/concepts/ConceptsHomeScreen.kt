package com.concepts_and_quizzes.cds.ui.english.concepts

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.core.components.CdsCard
import com.concepts_and_quizzes.cds.domain.english.EnglishTopic

@Composable
fun ConceptsHomeScreen(nav: NavHostController, viewModel: ConceptsHomeViewModel = hiltViewModel()) {
    val topics by viewModel.topics.collectAsState()
    LazyColumn {
        items(topics) { topic ->
            TopicCard(topic) { nav.navigate("english/concepts/${topic.id}") }
        }
    }
}

@Composable
private fun TopicCard(topic: EnglishTopic, onClick: () -> Unit) {
    CdsCard(modifier = Modifier.padding(8.dp), onClick = onClick) {
        Text(topic.name, Modifier.padding(16.dp))
    }
}
