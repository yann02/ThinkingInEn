package com.yyw.thinkinginen.domain

import com.yyw.thinkinginen.data.db.AppDatabase
import com.yyw.thinkinginen.di.IoDispatcher
import com.yyw.thinkinginen.entities.Message
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateMessageUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Message, Unit>(dispatcher) {
    override suspend fun execute(parameters: Message) {
        return appDatabase.messageDao().updateMessages(parameters)
    }
}