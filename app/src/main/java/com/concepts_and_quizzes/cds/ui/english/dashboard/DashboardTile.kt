package com.concepts_and_quizzes.cds.ui.english.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardTile(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,           // modifier first (optional)
    enabled: Boolean = true,
    content: (@Composable ColumnScope.() -> Unit)? = null,
    onClick: () -> Unit
) {
    val colors = if (enabled) CardDefaults.cardColors() else CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val elevation = CardDefaults.cardElevation(
        defaultElevation = if (enabled) 2.dp else 0.dp,
        pressedElevation = if (enabled) 4.dp else 0.dp
    )

    Card(
        onClick = { if (enabled) onClick() },
        enabled = enabled,
        colors = colors,
        elevation = elevation,                // correct param
        shape = MaterialTheme.shapes.large,
        modifier = modifier.aspectRatio(1.15f)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content?.let {
                Spacer(Modifier.height(8.dp))
                it()
            }
        }
    }
}
