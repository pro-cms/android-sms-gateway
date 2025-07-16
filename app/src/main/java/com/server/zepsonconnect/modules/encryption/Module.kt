package com.server.zepsonconnect.modules.encryption

import org.koin.dsl.module

val encryptionModule = module {
    single {
        EncryptionService(get())
    }
}