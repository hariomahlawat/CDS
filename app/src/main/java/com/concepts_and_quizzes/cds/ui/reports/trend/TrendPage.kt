package com.concepts_and_quizzes.cds.ui.reports.trend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrendViewModel @Inject constructor() : ViewModel()

@Composable
fun TrendPage(vm: TrendViewModel = hiltViewModel()) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Trend")
    }
}

