package com.yyw.thinkinginen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yyw.thinkinginen.domain.*
import com.yyw.thinkinginen.entities.toViewSeason
import com.yyw.thinkinginen.entities.vo.ViewMessage
import com.yyw.thinkinginen.entities.vo.ViewSeason
import com.yyw.thinkinginen.entities.vo.toMessage
import com.yyw.thinkinginen.entities.vo.flatten2ViewMessages
import com.yyw.thinkinginen.utils.WhileViewSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mOnSeasonsUseCase: OnSeasonsUseCase,
    val mUpdateMessageUseCase: UpdateMessageUseCase,
    mOnScrollPositionUseCase: OnScrollPositionUseCase,
    val mSettingScrollPositionUseCase: SettingScrollPositionUseCase
) : ViewModel() {
    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened: StateFlow<Boolean> = _drawerShouldBeOpened

    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }

    val mScrollPosition =
        mOnScrollPositionUseCase(Unit).stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    fun settingScrollPosition() {
        Log.d(TAG, "=====settingScrollPosition:$_mLastScrollPosition")
        viewModelScope.launch {
            mSettingScrollPositionUseCase(_mLastScrollPosition)
        }
    }

    private val _mSeasons =
        mOnSeasonsUseCase(Unit).stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    var mSeasonIdExpand by mutableStateOf(1)  //-1，when all of seasons has been collapse.
        private set

    var mViewSeasonId by mutableStateOf(1)
        private set

    var mViewEpisodeId by mutableStateOf(1)
        private set

    var mViewEpisodeSort by mutableStateOf(1)
        private set

    var mViewEpisodeName by mutableStateOf("")
        private set

    private val _mViewSeasons = _mSeasons.map {
        if (it is Result.Success) {
            val seasons = it.data.map { season ->
                season.toViewSeason()
            }
            mViewSeasons.update { seasons }
            Result.Success(seasons)
        } else {
            Result.Loading
        }
    }.stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    val mViewSeasons = MutableStateFlow<List<ViewSeason>>(emptyList())

    val mViewMessages = _mViewSeasons.combine(mScrollPosition) { stateViewSeasons, position ->
        if (stateViewSeasons is Result.Success && position is Result.Success) {
            val viewMessages = stateViewSeasons.data.flatten2ViewMessages()
            Log.d(TAG, "viewMessages size:${viewMessages.size}")
            //  只需要在首次加载数据时更新目录的选择状态
            viewMessages.getOrNull(position.data)?.let { firstVisibleMsg ->
                Log.d(TAG, "position:$position")
                //  更新目录的选择状态
                val nullableEpisode =
                    stateViewSeasons.data[firstVisibleMsg.sId - 1].episodes.find { episode ->
                        episode.episodeId == firstVisibleMsg.eId
                    }
                nullableEpisode?.let { episode ->
                    mViewSeasonId = episode.seasonId
                    mViewEpisodeId = episode.episodeId
                    mViewEpisodeName = episode.name
                    mViewEpisodeSort = episode.sort
                }
            }
            Result.Success(viewMessages)
        } else {
            Result.Loading
        }
    }.stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    private var _mSearchText = MutableStateFlow("")

    fun updatedSearchText(text: String) {
        _mSearchText.update { text }
    }

    val mMatchViewMessages: StateFlow<List<ViewMessage>> = _mSearchText.map {
        val data = mViewMessages.value.data
        data.takeUnless { dIt ->
            dIt.isNullOrEmpty()
        }?.filter { fIt ->
            it.isNotEmpty() && fIt.content.contains(it)
        } ?: emptyList()
    }.stateIn(viewModelScope, WhileViewSubscribed, emptyList())

    private var _mLastScrollPosition = 0

    fun updateLastScrollPosition(position: Int) {
        Log.d(TAG,"updateLastScrollPosition position:$position")
        _mLastScrollPosition = position
        val message = mViewMessages.value.data?.getOrNull(position)
        message?.let { msg ->
            if (msg.sId != mViewSeasonId || msg.eId != mViewEpisodeId) {
                val nullableEpisode =
                    _mViewSeasons.value.data?.get(msg.sId - 1)?.episodes?.find { episode ->
                        episode.episodeId == msg.eId
                    }
                nullableEpisode?.let { episode ->
                    mViewSeasonId = episode.seasonId
                    mSeasonIdExpand = episode.seasonId
                    mViewEpisodeId = episode.episodeId
                    mViewEpisodeName = episode.name
                    mViewEpisodeSort = episode.sort
                }
            }
        }
    }

    fun onEpisodeClick(sId: Int, eId: Int): Int =
        mViewMessages.value.data?.find { it.sId == sId && it.eId == eId }?.let {
            mViewMessages.value.data?.indexOf(it)
        } ?: -1

    fun onSeasonClick(seasonId: Int) {
        mSeasonIdExpand = if (mSeasonIdExpand == seasonId) {
            -1
        } else {
            seasonId
        }
    }

    fun onClickMessageById(msg: ViewMessage) {
        val message = msg.apply { vShowCn = !msg.vShowCn }.toMessage()
        viewModelScope.launch {
            mUpdateMessageUseCase(message)
        }
    }
}