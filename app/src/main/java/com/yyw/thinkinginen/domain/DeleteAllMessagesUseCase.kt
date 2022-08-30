package com.yyw.thinkinginen.domain

import com.yyw.thinkinginen.data.db.AppDatabase
import com.yyw.thinkinginen.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteAllMessagesUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, Unit>(dispatcher) {
    override suspend fun execute(parameters: Unit) {
        return appDatabase.messageDao().deleteAll()
    }
}