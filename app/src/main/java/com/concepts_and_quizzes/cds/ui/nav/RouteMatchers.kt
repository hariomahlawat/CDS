package com.concepts_and_quizzes.cds.ui.nav

fun isReports(route: String?) = route?.startsWith("reports") == true
fun isPyqp(route: String?) = route?.startsWith("english/pyqp") == true
fun isAnalytics(route: String?) = route?.startsWith("analytics") == true
fun isConcepts(route: String?) = route?.startsWith("english/concepts") == true
