package com.yyw.thinkinginen.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yyw.thinkinginen.entities.vo.ViewMessage

/**
 * @param showCn 是否显示中文 0:false , 1:true
 */
@Entity
data class Message(
    @PrimaryKey(autoGenerate = true) val messageId: Int,
    val sId: Int,
    val eId: Int,
    val topic: String,
    val role: String,
    val content: String,
    val cn: String,
    val showCn: Int = 0
)

fun Message.toViewMessage() =
    ViewMessage(
        messageId = messageId,
        sId = sId,
        eId = eId,
        topic = topic,
        role = role,
        content = content,
        cn = cn
    ).apply {
        vShowCn = showCn == 1
    }
