package com.concepts_and_quizzes.cds.ui.english.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.core.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverConceptDetailScreen(nav: NavHostController, vm: DiscoverConceptViewModel = hiltViewModel()) {
    val concept by vm.concept.collectAsState()
    val bookmarked by vm.bookmarked.collectAsState()

    concept?.let { c ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(c.title)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { nav.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconToggleButton(
                            checked = bookmarked,
                            onCheckedChange = { vm.toggleBookmark() }
                        ) {
                            Icon(
                                imageVector = if (bookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = null,
                                tint = if (bookmarked) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = Dimens.SheetPaddingX, vertical = Dimens.SheetPaddingTop)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.ParagraphSpacing)
            ) {
                val paragraphs = c.detail.split("\n\n")
                paragraphs.forEach { StyledParagraph(it) }
                val bullets = emptyList<String>()
                bullets.forEach { Bullet(it) }
                Spacer(Modifier.height(32.dp))
//                Button(
//                    onClick = { nav.navigate("concept/${c.id}") },
//                    modifier = Modifier.fillMaxWidth()
//                ) { Text("Full Lesson") }
            }
        }
    }
}
