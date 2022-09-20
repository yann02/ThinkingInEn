package com.yyw.thinkinginen.entities.vo

data class ViewSeason(
    val id: Int,
    var name: String,
    var isOpen: Boolean = false,
    var selected: Boolean = false,
    var episodes: List<ViewEpisode>
)
