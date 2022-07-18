package com.yyw.thinkinginen.domain

import com.yyw.thinkinginen.data.db.AppDatabase
import com.yyw.thinkinginen.di.IoDispatcher
import com.yyw.thinkinginen.entities.Message
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class InsertMessagesUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<List<Message>, List<Long>>(dispatcher) {
    override suspend fun execute(parameters: List<Message>): List<Long> {
        return appDatabase.messageDao().insertAll(parameters)
    }
}