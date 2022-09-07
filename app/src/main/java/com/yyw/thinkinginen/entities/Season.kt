package com.yyw.thinkinginen.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 表示第几季
 */
@Entity
data class Season(
    @PrimaryKey
    val id: Int,
    val name: String
)
