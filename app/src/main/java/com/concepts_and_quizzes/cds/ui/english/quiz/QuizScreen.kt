package com.concepts_and_quizzes.cds.ui.english.quiz

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun QuizScreen(topicId: String, nav: NavHostController) {
    Text("Quiz for $topicId")
}
