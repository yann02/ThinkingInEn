package com.yyw.thinkinginen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yyw.thinkinginen.domain.*
import com.yyw.thinkinginen.entities.Episode
import com.yyw.thinkinginen.entities.Message
import com.yyw.thinkinginen.entities.Season
import com.yyw.thinkinginen.entities.toViewSeason
import com.yyw.thinkinginen.entities.vo.ViewMessage
import com.yyw.thinkinginen.entities.vo.ViewSeason
import com.yyw.thinkinginen.entities.vo.toMessage
import com.yyw.thinkinginen.utils.WhileViewSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mOnSeasonsUseCase: OnSeasonsUseCase,
    val mInsertMessagesUseCase: InsertMessagesUseCase,
    val mDeleteAllMessagesUseCase: DeleteAllMessagesUseCase,
    val mUpdateMessageUseCase: UpdateMessageUseCase,
    val mInsertSeasonsUseCase: InsertSeasonsUseCase,
    val mDeleteAllSeasonsUseCase: DeleteAllSeasonsUseCase,
    val mInsertEpisodesUseCase: InsertEpisodesUseCase,
    val mDeleteAllEpisodesUseCase: DeleteAllEpisodesUseCase,
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
        viewModelScope.launch {
            mSettingScrollPositionUseCase(_mLastScrollPosition)
        }
    }

    private val _mCurrentViewSeason = MutableStateFlow(0)
    val mCurrentViewSeason: StateFlow<Int> = _mCurrentViewSeason


    private val _mCurrentViewEpisode = MutableStateFlow(0)
    val mCurrentViewEpisode: StateFlow<Int> = _mCurrentViewEpisode

    private val _mCurrentViewEpisodeName = MutableStateFlow("")
    val mCurrentViewEpisodeName: StateFlow<String> = _mCurrentViewEpisodeName


    fun insertData(seasons: List<Season>, episodes: List<Episode>, messages: List<Message>) {
        viewModelScope.launch {
            clearAll()
            insertSeasons(seasons)
            insertEpisodes(episodes)
            insertMessages(messages)
        }
    }

    private suspend fun clearAll() {
        mDeleteAllSeasonsUseCase(Unit)
        mDeleteAllEpisodesUseCase(Unit)
        mDeleteAllMessagesUseCase(Unit)
    }

    private suspend fun insertSeasons(seasons: List<Season>) {
        val rowIds = mInsertSeasonsUseCase(seasons)
        if (rowIds is Result.Success) {
            Log.d("wyy", "插入了${rowIds.data.size}季")
        }
    }

    private suspend fun insertEpisodes(episodes: List<Episode>) {
        val rowIds = mInsertEpisodesUseCase(episodes)
        if (rowIds is Result.Success) {
            Log.d("wyy", "插入了${rowIds.data.size}回")
        }
    }

    private suspend fun insertMessages(messages: List<Message>) {
        val rowIds = mInsertMessagesUseCase(messages)
        if (rowIds is Result.Success) {
            Log.d("wyy", "插入了${rowIds.data.size}条消息")
        }
    }

    val mSeasons = mOnSeasonsUseCase(Unit).stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    private val _mViewSeasons = mSeasons.map {
        if (it is Result.Success) {
            val seasons = it.data.map { season ->
                season.toViewSeason()
            }
            Result.Success(seasons)
        } else {
            Result.Loading
        }
    }.stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    val mViewSeasons = MutableStateFlow<List<ViewSeason>>(emptyList())

    var hasInit = false

    val mViewMessages = _mViewSeasons.combine(mScrollPosition) { it, position ->
        if (it is Result.Success && position is Result.Success) {
            val temp = it.data.map { season ->
                season.episodes.map { episode ->
                    episode.messages
                }.flatten()
            }.flatten()
            if (!hasInit) {
                //  只需要在首次加载数据时更新目录的选择状态
                temp.getOrNull(position.data)?.let { firstVisibleMsg ->
                    Log.d(TAG, "position:$position")
                    //  更新目录的选择状态
                    updateSelectedStateOfSeasons(firstVisibleMsg.sId - 1, firstVisibleMsg.eId - 1)
                    hasInit = true
                }
            }
            Result.Success(temp)
        } else {
            Result.Loading
        }
    }.stateIn(viewModelScope, WhileViewSubscribed, Result.Loading)

    private fun updateSelectedStateOfSeasons(newSeasonIndex: Int, newEpisodeIndex: Int) {
        Log.d(TAG, "updateSelectedStateOfSeasons newSeasonIndex:$newSeasonIndex,newEpisodeIndex:$newEpisodeIndex")
        val tempSeasons = if (!hasInit) {
            _mViewSeasons.value.data?.toMutableList() ?: mutableListOf()
        } else {
            mViewSeasons.value.toMutableList()
        }

        //  上一个选择的season和episode
        val lastSeasonIndex = _mCurrentViewSeason.value
        val lastEpisodeIndex = _mCurrentViewEpisode.value

        //  设置新的选择项的状态
        val curEpisodes = tempSeasons[newSeasonIndex].episodes
        curEpisodes[newEpisodeIndex].current = true
        val curEpisodeName = curEpisodes[newEpisodeIndex].name
        tempSeasons[newSeasonIndex].apply {
            selected = true
            episodes = curEpisodes
            if (lastSeasonIndex != newSeasonIndex) {
                isOpen = true
            } else {
                if (!hasInit) {
                    isOpen = true
                }
            }
        }


        if (lastSeasonIndex != newSeasonIndex) {
            val lastEpisodes = tempSeasons[lastSeasonIndex].episodes
            curEpisodes[lastEpisodeIndex].current = false
            tempSeasons[lastSeasonIndex].apply {
                selected = false
                episodes = lastEpisodes
                isOpen = false
            }
            _mCurrentViewSeason.value = newSeasonIndex
            if (lastEpisodeIndex != newEpisodeIndex) {
                _mCurrentViewEpisode.value = newEpisodeIndex
            }
        } else {
            if (lastEpisodeIndex != newEpisodeIndex) {
                val lastEpisodes = tempSeasons[lastSeasonIndex].episodes
                curEpisodes[lastEpisodeIndex].current = false
                tempSeasons[lastSeasonIndex].apply {
                    episodes = lastEpisodes
                }
                _mCurrentViewEpisode.value = newEpisodeIndex
            }
        }
        _mCurrentViewEpisodeName.update { curEpisodeName }
        mViewSeasons.update { tempSeasons }
    }

    private var _mLastScrollPosition = 0
    fun updateLastScrollPosition(position: Int) {
        _mLastScrollPosition = position
        val message = mViewMessages.value.data?.getOrNull(position)
        message?.let { msg ->
            val curSeasonIndex = msg.sId - 1
            val curEpisodeIndex = msg.eId - 1
            val lastSeasonIndex = _mCurrentViewSeason.value
            val lastEpisodeIndex = _mCurrentViewEpisode.value
            Log.d(
                TAG,
                "curSeasonIndex:$curSeasonIndex,curEpisodeIndex:$curEpisodeIndex,lastSeasonIndex:$lastSeasonIndex,lastEpisodeIndex:$lastEpisodeIndex"
            )
            if (curSeasonIndex != lastSeasonIndex || curEpisodeIndex != lastEpisodeIndex) {
                Log.d(TAG, "updateSelectedStateOfSeasons(curSeasonIndex, curEpisodeIndex)")
                updateSelectedStateOfSeasons(curSeasonIndex, curEpisodeIndex)
            }
        }
    }

    private val _mScrollToPosition = MutableStateFlow(-1)
    val mScrollToPosition: StateFlow<Int> = _mScrollToPosition

    fun onEpisodeClick(sId: Int, eId: Int) {
        mViewMessages.value.data?.find { it.sId == sId && it.eId == eId }?.let {
            val position = mViewMessages.value.data?.indexOf(it) ?: -1
            _mScrollToPosition.value = position
            Log.d(TAG, "onEpisodeClick sId:$sId,eId:$eId,position:$position")
        }

    }

    fun onSeasonClick(season: ViewSeason) {
        val tempSeasons = _mViewSeasons.value.data?.toMutableList() ?: mutableListOf()
        val clickIndex = tempSeasons.indexOf(season)
        tempSeasons[clickIndex] = season.apply {
            isOpen = !season.isOpen
        }
        mViewSeasons.update { tempSeasons }
    }

    fun onClickMessageById(msg: ViewMessage) {
        val message = msg.apply { vShowCn = !msg.vShowCn }.toMessage()
        viewModelScope.launch {
            mUpdateMessageUseCase(message)
        }
    }
}