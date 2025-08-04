package com.concepts_and_quizzes.cds.data.english.repo

import com.concepts_and_quizzes.cds.data.english.db.EnglishQuestionDao
import com.concepts_and_quizzes.cds.data.english.db.EnglishTopicDao
import com.concepts_and_quizzes.cds.data.english.model.toDomain
import com.concepts_and_quizzes.cds.domain.english.EnglishQuestion
import com.concepts_and_quizzes.cds.domain.english.EnglishTopic
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EnglishRepository @Inject constructor(
    private val topicDao: EnglishTopicDao,
    private val questionDao: EnglishQuestionDao
) {
    fun getTopics(): Flow<List<EnglishTopic>> =
        topicDao.getAll().map { list -> list.map { it.toDomain() } }

    fun getTopic(id: String): Flow<EnglishTopic?> =
        topicDao.getById(id).map { it?.toDomain() }

    fun getQuestions(topicId: String): Flow<List<EnglishQuestion>> =
        questionDao.getByTopic(topicId).map { list -> list.map { it.toDomain() } }
}
