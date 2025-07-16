package com.server.zepsonconnect.modules.messages.events

import com.server.zepsonconnect.domain.EntitySource
import com.server.zepsonconnect.domain.ProcessingState
import com.server.zepsonconnect.modules.events.AppEvent

class MessageStateChangedEvent(
    val id: String,
    val source: EntitySource,
    val phoneNumbers: Set<String>,
    val state: ProcessingState,
    val simNumber: Int?,
    val error: String?
): AppEvent(NAME) {

    companion object {
        const val NAME = "MessageStateChangedEvent"
    }
}