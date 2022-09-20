package com.yyw.thinkinginen.entities.vo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yyw.thinkinginen.entities.Message

class ViewMessage(
    val messageId: Int,
    val sId: Int,
    val eId: Int,
    val topic: String,
    val role: String,
    val content: String,
    val cn: String
) {
    var vShowCn by mutableStateOf(false)
}

fun ViewMessage.toMessage() = Message(messageId, sId, eId, topic, role, content, cn, if (vShowCn) 1 else 0)