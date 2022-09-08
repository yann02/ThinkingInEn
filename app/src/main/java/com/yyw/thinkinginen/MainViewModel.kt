package com.yyw.thinkinginen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yyw.thinkinginen.domain.*
import com.yyw.thinkinginen.domain.ds.OnCurrentEpisodeUseCase
import com.yyw.thinkinginen.domain.ds.OnCurrentSeasonUseCase
import com.yyw.thinkinginen.domain.ds.SettingCurrentEpisodeUseCase
import com.yyw.thinkinginen.domain.ds.SettingCurrentSeasonUseCase
import com.yyw.thinkinginen.entities.Episode
import com.yyw.thinkinginen.entities.Message
import com.yyw.thinkinginen.entities.Season
import com.yyw.thinkinginen.utils.WhileViewSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mOnSeasonsUseCase: OnSeasonsUseCase,
    val mInsertMessagesUseCase: InsertMessagesUseCase,
    val mDeleteAllMessagesUseCase: DeleteAllMessagesUseCase,
    val mInsertSeasonsUseCase: InsertSeasonsUseCase,
    val mDeleteAllSeasonsUseCase: DeleteAllSeasonsUseCase,
    val mInsertEpisodesUseCase: InsertEpisodesUseCase,
    val mDeleteAllEpisodesUseCase: DeleteAllEpisodesUseCase,
    mOnScrollPositionUseCase: OnScrollPositionUseCase,
    val mSettingScrollPositionUseCase: SettingScrollPositionUseCase,
    mOnCurrentSeasonUseCase: OnCurrentSeasonUseCase,
    val mSettingCurrentSeasonUseCase: SettingCurrentSeasonUseCase,
    mOnCurrentEpisodeUseCase: OnCurrentEpisodeUseCase,
    val mSettingCurrentEpisodeUseCase: SettingCurrentEpisodeUseCase
) : ViewModel() {
    val mScrollPosition =
        mOnScrollPositionUseCase(Unit).stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    fun settingScrollPosition() {
        viewModelScope.launch {
            mSettingScrollPositionUseCase(_mLastScrollPosition)
        }
    }

    val mCurrentSeason =
        mOnCurrentSeasonUseCase(Unit).stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    fun settingCurrentSeason(season: Int) {
        viewModelScope.launch {
            mSettingCurrentSeasonUseCase(season)
        }
    }

    val mCurrentEpisode =
        mOnCurrentEpisodeUseCase(Unit).stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    fun settingCurrentEpisode(episode: Int) {
        viewModelScope.launch {
            mSettingCurrentEpisodeUseCase(episode)
        }
    }

    fun insertData(seasons: List<Season>, episodes: List<Episode>, messages: List<Message>) {
        viewModelScope.launch {
            insertSeasons(seasons)
            insertEpisodes(episodes)
            insertMessages(messages)
        }
    }

    private suspend fun insertSeasons(seasons: List<Season>) {
        mDeleteAllSeasonsUseCase(Unit)
        val rowIds = mInsertSeasonsUseCase(seasons)
        if (rowIds is Result.Success) {
            Log.d("wyy", "插入了${rowIds.data.size}季")
        }
    }

    private suspend fun insertEpisodes(episodes: List<Episode>) {
        mDeleteAllEpisodesUseCase(Unit)
        val rowIds = mInsertEpisodesUseCase(episodes)
        if (rowIds is Result.Success) {
            Log.d("wyy", "插入了${rowIds.data.size}回")
        }
    }

    private suspend fun insertMessages(messages: List<Message>) {
        mDeleteAllMessagesUseCase(Unit)
        val rowIds = mInsertMessagesUseCase(messages)
        if (rowIds is Result.Success) {
            Log.d("wyy", "插入了${rowIds.data.size}条消息")
        }
    }

    val mSeasons = mOnSeasonsUseCase(Unit).stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    val mMessages = mSeasons.map {
        if (it is Result.Success) {
            val temp = it.data.map { wrapEntity ->
                wrapEntity.episodes.map { wrapEpisode ->
                    wrapEpisode.messages
                }.flatten()
            }.flatten()
            Result.Success(temp)
        } else {
            Result.Loading
        }
    }.stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    private var _mLastScrollPosition = 0
    fun updateLastScrollPosition(position: Int) {
        _mLastScrollPosition = position
        try {
            val message = mMessages.value.data?.get(position)
            message?.let {
                if (mCurrentSeason.value.data != it.sId - 1) {
                    settingCurrentSeason(it.sId - 1)
                }
                if (mCurrentEpisode.value.data != it.eId - 1) {
                    settingCurrentEpisode(it.eId - 1)
                }
            }
        } catch (e: Exception) {

        }

    }
}