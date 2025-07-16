package com.server.zepsonconnect.modules.messages.data

import com.server.zepsonconnect.data.entities.MessageRecipient
import com.server.zepsonconnect.domain.EntitySource
import com.server.zepsonconnect.domain.ProcessingState

class StoredSendRequest(
    val id: Long,
    val state: ProcessingState,
    val recipients: List<MessageRecipient>,
    source: EntitySource,
    message: Message,
    params: SendParams
) :
    SendRequest(source, message, params)