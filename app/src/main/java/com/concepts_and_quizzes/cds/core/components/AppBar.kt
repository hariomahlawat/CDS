package com.concepts_and_quizzes.cds.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.concepts_and_quizzes.cds.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CdsAppBar(title: String) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(32.dp)
                    .padding(start = 8.dp)
            )
        }
    )
}
