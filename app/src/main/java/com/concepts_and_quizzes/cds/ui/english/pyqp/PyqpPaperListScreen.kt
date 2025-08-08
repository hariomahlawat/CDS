package com.concepts_and_quizzes.cds.ui.english.pyqp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.concepts_and_quizzes.cds.ui.components.EmptyState

@Composable
fun PyqpPaperListScreen(nav: NavController, vm: PyqpListViewModel = hiltViewModel()) {
    val papers by vm.papers.collectAsState()
    Scaffold { padd ->
        if (papers.isEmpty()) {
            EmptyState(title = "No papers")
        } else {
            LazyColumn(contentPadding = padd) {
                items(papers) { paper ->
                    ListItem(
                        headlineContent = { Text("CDS ${paper.year}  ${paper.id.takeLast(5)}") },
                        trailingContent = {
                            Icon(
                                Icons.Filled.ChevronRight,
                                contentDescription = "Open paper"
                            )
                        },
                        modifier = Modifier.clickable {
                            nav.navigate("english/pyqp/${paper.id}")
                        }
                    )
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
            }
        }
    }
}
