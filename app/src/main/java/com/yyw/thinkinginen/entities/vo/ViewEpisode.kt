package com.yyw.thinkinginen.entities.vo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ViewEpisode(
    val episodeId: Int,
    val name: String,
    val seasonId: Int,
    val messages: List<ViewMessage>
){
    var current by mutableStateOf(false)
}
