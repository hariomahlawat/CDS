package com.concepts_and_quizzes.cds.data.analytics

fun shouldCelebrate(accuracy: Float): Boolean =
    accuracy >= AnalyticsConfig.PERCENT_GOOD_SCORE
