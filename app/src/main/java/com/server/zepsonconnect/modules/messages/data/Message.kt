package com.server.zepsonconnect.modules.messages.data

import com.server.zepsonconnect.domain.MessageContent
import java.util.Date

data class Message(
    val id: String,
    val content: MessageContent,
    val phoneNumbers: List<String>,

    val isEncrypted: Boolean,

    val createdAt: Date,
)