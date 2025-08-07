package com.concepts_and_quizzes.cds.ui.english.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.concepts_and_quizzes.cds.core.components.CdsCard
import java.time.LocalTime
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.ProgressIndicatorDefaults

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun EnglishDashboardScreen(nav: NavHostController, vm: EnglishDashboardViewModel = hiltViewModel()) {
    val summary by vm.summary.collectAsState()
    val questionsToday by vm.questionsToday.collectAsState()
    val concepts by vm.concepts.collectAsState()
    val count by animateIntAsState(targetValue = questionsToday, label = "count")

    val greeting = remember {
        val hour = LocalTime.now().hour
        val part = when (hour) {
            in 5..11 -> "Morning"
            in 12..17 -> "Afternoon"
            else -> "Evening"
        }
        "$part revision, Magnus!"
    }

    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(90.dp)
                .graphicsLayer {
                    clip = true
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    shadowElevation = 8f
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    greeting,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                CircularProgressIndicator(
                progress = { summary.best / 100f },
                modifier = Modifier.size(48.dp),
                color = ProgressIndicatorDefaults.circularColor,
                strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )
            }
        }

        FlowRow(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            ActionChip(Icons.Filled.AutoStories, "Concepts") { nav.navigate("english/concepts") }
            ActionChip(Icons.Filled.School, "Mock Tests") { nav.navigate("quizHub") }
            ActionChip(Icons.AutoMirrored.Filled.MenuBook, "Past Papers") { nav.navigate("english/pyqp") }
        }

        Row(
            Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.QuestionAnswer, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("$count Questions practised today")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Discover",
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        val listState = rememberLazyListState()
        val fling = rememberSnapFlingBehavior(listState)
        LazyRow(
            state = listState,
            flingBehavior = fling,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(concepts) { concept ->
                Box(
                    Modifier
                        .width(200.dp)
                        .height(120.dp)
                ) {
                    CdsCard(Modifier.matchParentSize()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                concept.title,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                concept.overview,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 12.dp, y = (-12).dp)
                            .size(24.dp)
                            .graphicsLayer { rotationZ = 45f }
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionChip(icon: ImageVector, label: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.95f else 1f, label = "scale")

    Surface(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(label)
        }
    }
}

