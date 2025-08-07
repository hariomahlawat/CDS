package com.concepts_and_quizzes.cds.ui.english.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.core.theme.Dimens

@Composable
fun DiscoverConceptDetailScreen(nav: NavHostController, vm: DiscoverConceptViewModel = hiltViewModel()) {
    val concept by vm.concept.collectAsState()
    val bookmarked by vm.bookmarked.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    concept?.let { c ->
        ModalBottomSheet(
            onDismissRequest = { nav.navigateUp() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 3.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = Dimens.SheetPaddingX, vertical = Dimens.SheetPaddingTop)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.ParagraphSpacing)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(c.title, style = MaterialTheme.typography.headlineSmall)
                    IconToggleButton(
                        checked = bookmarked,
                        onCheckedChange = { vm.toggleBookmark() }
                    ) {
                        Icon(
                            imageVector = if (bookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = if (bookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                val paragraphs = c.detail.split("\n\n")
                paragraphs.forEach { StyledParagraph(it) }
                val bullets = emptyList<String>()
                bullets.forEach { Bullet(it) }
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { nav.navigate("concept/${c.id}") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Full Lesson") }
            }
        }
    }
}
