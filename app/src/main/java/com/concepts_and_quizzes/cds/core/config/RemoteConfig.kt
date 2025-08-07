package com.concepts_and_quizzes.cds.core.config

import javax.inject.Inject

/** Simple interface for remote configuration flags. */
interface RemoteConfig {
    fun getBoolean(key: String): Boolean
}

/** Default implementation always returns true for all flags. */
class DefaultRemoteConfig @Inject constructor() : RemoteConfig {
    override fun getBoolean(key: String): Boolean = true
}
