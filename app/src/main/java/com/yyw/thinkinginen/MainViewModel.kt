package com.yyw.thinkinginen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yyw.thinkinginen.domain.*
import com.yyw.thinkinginen.entities.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mOnScrollPositionUseCase: OnScrollPositionUseCase,
    mOnMessagesUseCase: OnMessagesUseCase,
    val mInsertMessagesUseCase: InsertMessagesUseCase,
    val mSettingScrollPositionUseCase: SettingScrollPositionUseCase
) : ViewModel() {
    val mScrollPosition =
        mOnScrollPositionUseCase(Unit).stateIn(viewModelScope, SharingStarted.WhileSubscribed(300), Result.Loading)

    fun settingScrollPosition() {
        viewModelScope.launch {
            mSettingScrollPositionUseCase(_mLastScrollPosition)
        }
    }

    fun insertMessages(messages: List<Message>) {
        viewModelScope.launch {
            val rowIds = mInsertMessagesUseCase(messages)
            if (rowIds is Result.Success) {
                Log.d("wyy", "插入了${rowIds.data.size}条消息")
            }
        }
    }

    val mMessages = mOnMessagesUseCase(Unit).stateIn(viewModelScope, SharingStarted.WhileSubscribed(300), Result.Loading)

    private var _mLastScrollPosition = 0
    fun updateLastScrollPosition(position: Int) {
        _mLastScrollPosition = position
    }
}