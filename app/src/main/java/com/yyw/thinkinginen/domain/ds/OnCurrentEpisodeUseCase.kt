package com.yyw.thinkinginen.domain.ds

import com.yyw.thinkinginen.data.PreferenceStorage
import com.yyw.thinkinginen.di.IoDispatcher
import com.yyw.thinkinginen.domain.FlowUseCase
import com.yyw.thinkinginen.domain.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OnCurrentEpisodeUseCase @Inject constructor(
    private val prefs: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Int>(dispatcher) {
    override fun execute(parameters: Unit): Flow<Result<Int>> = prefs.onCurrentEpisode.map { Result.Success(it) }
}