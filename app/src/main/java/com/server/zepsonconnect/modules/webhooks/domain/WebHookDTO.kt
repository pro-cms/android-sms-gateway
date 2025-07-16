package com.server.zepsonconnect.modules.webhooks.domain

import com.server.zepsonconnect.domain.EntitySource

data class WebHookDTO(
    val id: String?,
    val deviceId: String?,
    val url: String,
    val event: WebHookEvent,
    val source: EntitySource,
)
