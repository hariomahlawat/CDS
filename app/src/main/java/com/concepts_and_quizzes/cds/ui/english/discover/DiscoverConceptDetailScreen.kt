package com.concepts_and_quizzes.cds.ui.english.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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

@Composable
fun DiscoverConceptDetailScreen(nav: NavHostController, vm: DiscoverConceptViewModel = hiltViewModel()) {
    val concept by vm.concept.collectAsState()

    Scaffold { padding ->
        concept?.let { c ->
            Column(Modifier.padding(padding).padding(16.dp)) {
                Text(c.detail, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
