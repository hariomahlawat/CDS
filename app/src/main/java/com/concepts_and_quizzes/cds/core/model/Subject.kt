package com.concepts_and_quizzes.cds.core.model

import androidx.annotation.DrawableRes
import com.concepts_and_quizzes.cds.R

enum class Subject(
    val id: String,
    val displayName: String,
    @DrawableRes val iconRes: Int,
    val skuId: String
) {
    ENGLISH("english", "English", R.drawable.ic_launcher_foreground, "premium_english"),
    MATHEMATICS("mathematics", "Mathematics", R.drawable.ic_launcher_foreground, "premium_math"),
    GENERAL_KNOWLEDGE("gk", "General Knowledge", R.drawable.ic_launcher_foreground, "premium_gk")
}
