package com.concepts_and_quizzes.cds.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ModeCard(
    title: String,
    subtitle: String? = null,
    enabled: Boolean,
    disabledCaption: String? = null,
    onClick: () -> Unit
) {
    val colours = if (enabled) CardDefaults.cardColors() else
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    Card(
        onClick = { if (enabled) onClick() },
        enabled = enabled,
        colors = colours,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall)
            if (!enabled && disabledCaption != null) {
                Spacer(Modifier.height(8.dp))
                Text(disabledCaption, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
