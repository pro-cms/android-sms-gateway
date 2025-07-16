package com.server.zepsonconnect.modules.messages.data

import com.server.zepsonconnect.domain.EntitySource

open class SendRequest(
    val source: EntitySource,
    val message: Message,
    val params: SendParams,
)