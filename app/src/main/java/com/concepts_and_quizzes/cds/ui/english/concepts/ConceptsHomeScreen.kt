package com.concepts_and_quizzes.cds.ui.english.concepts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.core.components.CdsCard
import com.concepts_and_quizzes.cds.domain.english.EnglishTopic
import com.concepts_and_quizzes.cds.data.discover.model.ConceptEntity

@Composable
fun ConceptsHomeScreen(nav: NavHostController, viewModel: ConceptsHomeViewModel = hiltViewModel()) {
    val topics by viewModel.topics.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val selected = remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selected.value) {
            Tab(selected.value == 0, onClick = { selected.value = 0 }) { Text("All") }
            Tab(selected.value == 1, onClick = { selected.value = 1 }) { Text("Bookmarks") }
        }
        if (selected.value == 0) {
            LazyColumn {
                items(topics, key = { it.id }) { topic ->
                    TopicCard(topic) { nav.navigate("english/concepts/${topic.id}") }
                }
            }
        } else {
            LazyColumn {
                items(bookmarks, key = { it.id }) { concept ->
                    BookmarkCard(concept) { nav.navigate("discover/${concept.id}") }
                }
            }
        }
    }
}

@Composable
private fun TopicCard(topic: EnglishTopic, onClick: () -> Unit) {
    CdsCard(modifier = Modifier.padding(8.dp), onClick = onClick) {
        Text(topic.name, Modifier.padding(16.dp))
    }
}

@Composable
private fun BookmarkCard(concept: ConceptEntity, onClick: () -> Unit) {
    CdsCard(modifier = Modifier.padding(8.dp), onClick = onClick) {
        Text(concept.title, Modifier.padding(16.dp))
    }
}
