package com.server.zepsonconnect.modules.ping.events

import com.server.zepsonconnect.domain.HealthResponse
import com.server.zepsonconnect.modules.events.AppEvent

class PingEvent(
    val health: HealthResponse,
) : AppEvent(TYPE) {
    companion object {
        const val TYPE = "PingEvent"
    }
}