package com.server.zepsonconnect.modules.gateway

import android.util.Log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import com.server.zepsonconnect.domain.EntitySource
import com.server.zepsonconnect.modules.events.EventBus
import com.server.zepsonconnect.modules.events.EventsReceiver
import com.server.zepsonconnect.modules.gateway.workers.PullMessagesWorker
import com.server.zepsonconnect.modules.gateway.workers.SendStateWorker
import com.server.zepsonconnect.modules.messages.events.MessageStateChangedEvent
import com.server.zepsonconnect.modules.ping.events.PingEvent
import com.server.zepsonconnect.modules.push.events.PushMessageEnqueuedEvent
import org.koin.core.component.get

class EventsReceiver : EventsReceiver() {

    private val settings = get<GatewaySettings>()

    override suspend fun collect(eventBus: EventBus) {
        coroutineScope {
            launch {
                Log.d("EventsReceiver", "launched PushMessageEnqueuedEvent")
                eventBus.collect<PushMessageEnqueuedEvent> { event ->
                    Log.d("EventsReceiver", "Event: $event")

                    if (!settings.enabled) return@collect

                    PullMessagesWorker.start(get())
                }
            }
            launch {
                Log.d("EventsReceiver", "launched MessageStateChangedEvent")
                val allowedSources = setOf(EntitySource.Cloud, EntitySource.Gateway)
                eventBus.collect<MessageStateChangedEvent> { event ->
                    Log.d("EventsReceiver", "Event: $event")

                    if (!settings.enabled) return@collect

                    if (event.source !in allowedSources) return@collect

                    SendStateWorker.start(get(), event.id)
                }
            }

            launch {
                Log.d("EventsReceiver", "launched PingEvent")
                eventBus.collect<PingEvent> {
                    Log.d("EventsReceiver", "Event: $it")

                    if (!settings.enabled) return@collect

                    PullMessagesWorker.start(get())
                }
            }
        }

    }
}