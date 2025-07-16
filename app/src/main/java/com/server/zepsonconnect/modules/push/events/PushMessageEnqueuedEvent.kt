package com.server.zepsonconnect.modules.push.events

import com.server.zepsonconnect.modules.events.AppEvent

class PushMessageEnqueuedEvent : AppEvent(NAME) {
    companion object {
        private const val NAME = "MessageEnqueuedEvent"
    }
}