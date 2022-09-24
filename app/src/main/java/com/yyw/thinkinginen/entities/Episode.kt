package com.yyw.thinkinginen.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 第几回
 */
@Entity
data class Episode(
    @PrimaryKey
    val episodeId: Int,
    val sort: Int,
    val name: String,
    val seasonId: Int
)
