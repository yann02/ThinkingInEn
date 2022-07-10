package com.yyw.thinkinginen.domain

import com.yyw.thinkinginen.data.PreferenceStorage
import com.yyw.thinkinginen.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OnScrollPositionUseCase @Inject constructor(
    private val prefs: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Int>(dispatcher) {
    override fun execute(parameters: Unit): Flow<Result<Int>> = prefs.onScrollPosition.map { Result.Success(it) }
}