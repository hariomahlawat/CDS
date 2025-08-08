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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.concepts_and_quizzes.cds.ui.components.EmptyState
import com.concepts_and_quizzes.cds.ui.components.ErrorState
import com.concepts_and_quizzes.cds.ui.components.LoadingSkeleton
import com.concepts_and_quizzes.cds.ui.components.UiState

@Composable
fun PyqpPaperListScreen(nav: NavController, vm: PyqpListViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    Scaffold { padd ->
        when (val s = state) {
            is UiState.Data -> {
                LazyColumn(contentPadding = padd) {
                    items(s.value, key = { it.id }) { paper ->
                        ListItem(
                            headlineContent = { Text("CDS ${paper.year}  ${paper.id.takeLast(5)}") },
                            trailingContent = {
                                Icon(
                                    Icons.Filled.ChevronRight,
                                    contentDescription = "Open paper",
                                )
                            },
                            modifier = Modifier.clickable {
                                nav.navigate("english/pyqp/${paper.id}")
                            },
                        )
                        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    }
                }
            }
            UiState.Loading -> LoadingSkeleton()
            is UiState.Error -> ErrorState(s.message) { vm.refresh() }
            is UiState.Empty -> EmptyState(s.title, s.actionLabel) { vm.refresh() }
        }
    }
}
