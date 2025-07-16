package com.server.zepsonconnect.modules.webhooks

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val webhooksModule = module {
    singleOf(::WebHooksService)
}

val NAME = "webhooks"