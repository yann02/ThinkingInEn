package com.yyw.thinkinginen.domain

import com.yyw.thinkinginen.data.PreferenceStorage
import com.yyw.thinkinginen.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SettingScrollPositionUseCase @Inject constructor(
    private val prefs: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Int, Unit>(dispatcher) {
    override suspend fun execute(parameters: Int) {
        prefs.scrollPosition(parameters)
    }
}