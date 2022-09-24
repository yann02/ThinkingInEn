package com.yyw.thinkinginen.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.yyw.thinkinginen.entities.vo.ViewEpisode

data class EpisodeWithMessages(
    @Embedded
    val episode: Episode,
    @Relation(parentColumn = "episodeId", entityColumn = "eId")
    val messages: List<Message>
)

fun EpisodeWithMessages.toViewEpisode() = ViewEpisode(
    episodeId = episode.episodeId,
    name = episode.name,
    seasonId = episode.seasonId,
    messages = messages.map { it.toViewMessage() })