package com.yyw.thinkinginen.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class EpisodeWithMessages(
    @Embedded
    val episode: Episode,
    @Relation(parentColumn = "episodeId", entityColumn = "eId")
    val messages: List<Message>
)
