package com.yyw.thinkinginen.domain

import com.yyw.thinkinginen.data.db.AppDatabase
import com.yyw.thinkinginen.di.IoDispatcher
import com.yyw.thinkinginen.entities.Episode
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class InsertEpisodesUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<List<Episode>, List<Long>>(dispatcher) {
    override suspend fun execute(parameters: List<Episode>): List<Long> {
        return appDatabase.episodeDao().insertAll(parameters)
    }
}