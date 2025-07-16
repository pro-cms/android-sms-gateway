package com.server.zepsonconnect.modules.localserver.domain

import com.google.gson.JsonElement
import com.server.zepsonconnect.modules.logs.db.LogEntry
import java.util.Date

data class GetLogsResponse(
    val priority: LogEntry.Priority,
    val module: String,
    val message: String,
    val id: Long = 0,
    val context: JsonElement? = null,
    val createdAt: Date,
) {
    companion object {
        fun from(log: LogEntry) = GetLogsResponse(
            priority = log.priority,
            module = log.module,
            message = log.message,
            id = log.id,
            context = log.context,
            createdAt = Date(log.createdAt)
        )
    }
}
