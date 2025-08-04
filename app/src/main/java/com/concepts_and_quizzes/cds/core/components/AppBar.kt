package com.concepts_and_quizzes.cds.core.components

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun CdsAppBar(title: String) {
    TopAppBar(title = { Text(text = title) })
}
