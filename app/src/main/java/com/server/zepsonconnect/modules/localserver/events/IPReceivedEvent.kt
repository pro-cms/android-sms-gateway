package com.server.zepsonconnect.modules.localserver.events

import com.server.zepsonconnect.modules.events.AppEvent

class IPReceivedEvent(
    val localIP: String?,
    val publicIP: String?,
): AppEvent(NAME) {
    companion object {
        const val NAME = "IPReceivedEvent"
    }
}