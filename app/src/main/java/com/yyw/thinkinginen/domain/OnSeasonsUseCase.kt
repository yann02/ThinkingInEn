package com.yyw.thinkinginen.domain

import com.yyw.thinkinginen.data.db.AppDatabase
import com.yyw.thinkinginen.di.IoDispatcher
import com.yyw.thinkinginen.entities.SeasonWithEpisodeAndMessages
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OnSeasonsUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<SeasonWithEpisodeAndMessages>>(dispatcher) {
    override fun execute(parameters: Unit): Flow<Result<List<SeasonWithEpisodeAndMessages>>> =
        appDatabase.seasonDao().loadAll().map { Result.Success(it) }
}