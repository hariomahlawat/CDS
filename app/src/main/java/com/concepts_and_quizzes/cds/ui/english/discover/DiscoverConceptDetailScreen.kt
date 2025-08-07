package com.concepts_and_quizzes.cds.ui.english.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverConceptDetailScreen(nav: NavHostController, vm: DiscoverConceptViewModel = hiltViewModel()) {
    val concept by vm.concept.collectAsState()
    val bookmarked by vm.bookmarked.collectAsState()

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text(concept?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { vm.toggleBookmark() }) {
                        Icon(
                            imageVector = if (bookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        concept?.let { c ->
            Column(Modifier.padding(padding).padding(16.dp)) {
                Text(c.detail, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
