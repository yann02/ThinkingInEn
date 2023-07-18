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

fun List<ViewSeason>.flatten2ViewMessages(): List<ViewMessage> = this.map { season ->
    season.episodes.map { episode ->
        episode.messages
    }.flatten()
}.flatten()
