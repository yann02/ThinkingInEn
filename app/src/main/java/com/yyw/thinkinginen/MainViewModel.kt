package com.yyw.thinkinginen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yyw.thinkinginen.domain.OnScrollPositionUseCase
import com.yyw.thinkinginen.domain.Result
import com.yyw.thinkinginen.domain.SettingScrollPositionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mOnScrollPositionUseCase: OnScrollPositionUseCase,
    val mSettingScrollPositionUseCase: SettingScrollPositionUseCase
) : ViewModel() {
    val mScrollPosition =
        mOnScrollPositionUseCase(Unit).stateIn(viewModelScope, SharingStarted.WhileSubscribed(300), Result.Loading)

    fun settingScrollPosition(position: Int) {
        viewModelScope.launch {
            mSettingScrollPositionUseCase(position)
        }
    }
}