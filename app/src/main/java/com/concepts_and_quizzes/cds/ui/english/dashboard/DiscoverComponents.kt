package com.concepts_and_quizzes.cds.ui.english.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.concepts_and_quizzes.cds.data.discover.model.ConceptEntity

@Composable
fun DiscoverCard(
    concept: ConceptEntity,
    bookmarked: Boolean,
    onBookmark: () -> Unit,
    onOpen: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp,
        modifier = Modifier
            .width(280.dp)
            .clickable { onOpen() }
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(concept.title, style = MaterialTheme.typography.titleMedium)
                IconToggleButton(
                    checked = bookmarked,
                    onCheckedChange = { onBookmark() },
                    modifier = Modifier.semantics {
                        contentDescription = if (bookmarked) {
                            "Remove bookmark"
                        } else {
                            "Add bookmark"
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (bookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = null
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(concept.blurb, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun DiscoverCarousel(
    tips: List<ConceptEntity>,
    vm: EnglishDashboardViewModel,
    nav: NavController
) {
    LazyRow(
        modifier = Modifier.focusable(),
        reverseLayout = false,
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tips, key = { it.id }) { concept ->
            val bookmarked by vm.isBookmarked(concept.id).collectAsState(initial = false)
            DiscoverCard(
                concept = concept,
                bookmarked = bookmarked,
                onBookmark = { vm.onBookmarkToggle(concept.id) },
                onOpen = { nav.navigate("discover/${concept.id}") }
            )
        }
    }
}
