package com.concepts_and_quizzes.cds.ui.english.discover

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.concepts_and_quizzes.cds.core.theme.Dimens

@Composable
fun StyledParagraph(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start
    )
    Spacer(Modifier.height(Dimens.ParagraphSpacing))
}

@Composable
fun Bullet(text: String) = Row(
    modifier = Modifier.padding(start = Dimens.BulletIndent)
) {
    Text("•", style = MaterialTheme.typography.labelLarge)
    Spacer(Modifier.width(8.dp))
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.weight(1f)
    )
}
