package com.server.zepsonconnect.modules.notifications

import org.koin.dsl.module

val notificationsModule = module {
    single { NotificationsService(get()) }
}