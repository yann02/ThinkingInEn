package com.yyw.thinkinginen.entities.vo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ViewSeason(
    val id: Int,
    var name: String,
    var selected: Boolean = false,
    var episodes: List<ViewEpisode>
) {
    var isOpen by mutableStateOf(false)
}
