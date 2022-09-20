package com.yyw.thinkinginen.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.yyw.thinkinginen.entities.vo.ViewSeason

data class SeasonWithEpisodeAndMessages(
    @Embedded
    val season: Season,
    @Relation(parentColumn = "id", entityColumn = "seasonId", entity = Episode::class)
    val episodes: List<EpisodeWithMessages>
)

fun SeasonWithEpisodeAndMessages.toViewSeason() =
    ViewSeason(id = season.id, name = season.name, episodes = episodes.map { it.toViewEpisode() })
