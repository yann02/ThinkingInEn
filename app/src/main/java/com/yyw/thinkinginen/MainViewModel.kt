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

    private var _hasInitData = false
    val hasInitData = _hasInitData

    fun upDataHasInit() {
        _hasInitData = true
    }

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

    private val _mCurrentViewSeasonId = MutableStateFlow(1)
    val mCurrentViewSeasonId: StateFlow<Int> = _mCurrentViewSeasonId


    private val _mCurrentViewEpisodeId = MutableStateFlow(1)

    val mCurrentViewEpisodeSort = _mCurrentViewSeasonId.combine(_mCurrentViewEpisodeId) { seasonId, episodeId ->
        (episodeId - (seasonId - 1) * 1000).coerceAtLeast(1)
    }.stateIn(viewModelScope, WhileViewSubscribed, 1)

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
            Log.d(TAG, "temp size:${temp.size},hasInit:$hasInit")
            if (!hasInit) {
                //  只需要在首次加载数据时更新目录的选择状态
                temp.getOrNull(position.data)?.let { firstVisibleMsg ->
                    Log.d(TAG, "position:$position")
                    //  更新目录的选择状态
//                    updateSelectedStateOfSeasons(firstVisibleMsg.sId - 1, firstVisibleMsg.eId - 1)
                    updateSelectedStateOfSeasons(firstVisibleMsg.sId, firstVisibleMsg.eId)
                    hasInit = true
                }
            }
            Result.Success(temp)
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


    private fun updateSelectedStateOfSeasons(newSeasonId: Int, newEpisodeId: Int) {
        Log.d(
            TAG,
            "updateSelectedStateOfSeasons newSeasonId:$newSeasonId,newEpisodeId:$newEpisodeId,_hasInitData:$_hasInitData"
        )
        val tempSeasons = _mViewSeasons.value.data?.toMutableList() ?: mutableListOf()
//        val tempSeasons = if (!_hasInitData) {
//            _mViewSeasons.value.data?.toMutableList() ?: mutableListOf()
//        } else {
//            mViewSeasons.value.toMutableList()
//        }

        if (tempSeasons.isEmpty()) {
            return
        }

        //  上一个选择的season和episode
        val lastSeasonId = _mCurrentViewSeasonId.value
        val lastEpisodeId = _mCurrentViewEpisodeId.value

        //  设置新的选择项的状态
        val newSeasonIndex = newSeasonId - 1
        val curEpisodes = tempSeasons[newSeasonIndex].episodes
        val curEpisodeIndex = curEpisodes.indexOf(curEpisodes.find { it.episodeId == newEpisodeId })
        curEpisodes[curEpisodeIndex].current = true
        val curEpisodeName = curEpisodes[curEpisodeIndex].name
        tempSeasons[newSeasonIndex].apply {
            selected = true
            episodes = curEpisodes
            if (lastSeasonId != newSeasonId) {
                isOpen = true
            } else {
                if (!_hasInitData) {
                    isOpen = true
                }
            }
        }


        val lastSeasonIndex = lastSeasonId - 1
        if (lastSeasonId != newSeasonId) {
            val lastEpisodes = tempSeasons[lastSeasonIndex].episodes
            val lastEpisodeIndex = lastEpisodes.indexOf(lastEpisodes.find { it.episodeId == lastEpisodeId })
            lastEpisodes[lastEpisodeIndex].current = false
            tempSeasons[lastSeasonIndex].apply {
                selected = false
                episodes = lastEpisodes
                isOpen = false
            }
            _mCurrentViewSeasonId.value = newSeasonId
            if (lastEpisodeId != newEpisodeId) {
                _mCurrentViewEpisodeId.value = newEpisodeId
            }
        } else {
            if (lastEpisodeId != newEpisodeId) {
                val lastEpisodes = tempSeasons[lastSeasonIndex].episodes
                val lastEpisodeIndex = lastEpisodes.indexOf(lastEpisodes.find { it.episodeId == lastEpisodeId })
                lastEpisodes[lastEpisodeIndex].current = false
                tempSeasons[lastSeasonIndex].apply {
                    episodes = lastEpisodes
                }
                _mCurrentViewEpisodeId.value = newEpisodeId
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
            val curSeasonId = msg.sId
            val curEpisodeId = msg.eId
            val lastSeasonId = _mCurrentViewSeasonId.value
            val lastEpisodeId = _mCurrentViewEpisodeId.value
            Log.d(
                TAG,
                "curSeasonIndex:$curSeasonId,curEpisodeIndex:$curEpisodeId,lastSeasonIndex:$lastSeasonId,lastEpisodeIndex:$lastEpisodeId"
            )
            if (curSeasonId != lastSeasonId || curEpisodeId != lastEpisodeId) {
                Log.d(TAG, "updateSelectedStateOfSeasons(curSeasonIndex, curEpisodeIndex)")
                updateSelectedStateOfSeasons(curSeasonId, curEpisodeId)
            }
        }
    }

    fun onEpisodeClick(sId: Int, eId: Int): Int =
        mViewMessages.value.data?.find { it.sId == sId && it.eId == eId }?.let {
            mViewMessages.value.data?.indexOf(it)
        } ?: -1

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