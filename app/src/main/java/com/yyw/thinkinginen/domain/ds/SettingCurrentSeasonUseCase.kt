package com.yyw.thinkinginen.domain.ds

import com.yyw.thinkinginen.data.PreferenceStorage
import com.yyw.thinkinginen.di.IoDispatcher
import com.yyw.thinkinginen.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SettingCurrentSeasonUseCase @Inject constructor(
    private val prefs: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Int, Unit>(dispatcher) {
    override suspend fun execute(parameters: Int) {
        prefs.currentSeason(parameters)
    }
}