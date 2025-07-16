package com.server.zepsonconnect.modules.events

import org.koin.dsl.module

val eventBusModule = module {
    single { EventBus() }
}