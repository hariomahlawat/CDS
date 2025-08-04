package com.concepts_and_quizzes.cds.core.util

import java.security.MessageDigest

fun ByteArray.sha256(): String =
    MessageDigest.getInstance("SHA-256").digest(this).joinToString("") { "%02x".format(it) }
