package com.yyw.thinkinginen.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true) val messageId: Int,
    val sId: Int,
    val eId: Int,
    val topic: String,
    val role: String,
    val content: String,
    val cn: String
)
