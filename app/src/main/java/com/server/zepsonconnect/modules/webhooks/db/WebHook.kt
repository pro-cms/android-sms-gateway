package com.server.zepsonconnect.modules.webhooks.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.server.zepsonconnect.domain.EntitySource
import com.server.zepsonconnect.modules.webhooks.domain.WebHookEvent

@Entity
data class WebHook(
    @PrimaryKey
    val id: String,
    val url: String,
    val event: WebHookEvent,
    val source: EntitySource,
)