package com.server.zepsonconnect.data

import org.koin.dsl.module

val dbModule = module {
    single { AppDatabase.getDatabase(get()) }
    single { get<AppDatabase>().messagesDao() }
    single { get<AppDatabase>().webhooksDao() }
    single { get<AppDatabase>().logDao() }
}