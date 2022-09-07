package com.yyw.thinkinginen.domain

import com.yyw.thinkinginen.data.db.AppDatabase
import com.yyw.thinkinginen.di.IoDispatcher
import com.yyw.thinkinginen.entities.Season
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class InsertSeasonsUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<List<Season>, List<Long>>(dispatcher) {
    override suspend fun execute(parameters: List<Season>): List<Long> {
        return appDatabase.seasonDao().insertAll(parameters)
    }
}