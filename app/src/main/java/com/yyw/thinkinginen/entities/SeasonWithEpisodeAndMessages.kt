package com.yyw.thinkinginen.entities

import androidx.room.Embedded
import androidx.room.Relation

data class SeasonWithEpisodeAndMessages(
    @Embedded
    val season: Season,
    @Relation(parentColumn = "id", entityColumn = "seasonId", entity = Episode::class)
    val episodes: List<EpisodeWithMessages>
)
