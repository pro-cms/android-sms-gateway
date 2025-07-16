package com.server.zepsonconnect.modules.ping

import com.server.zepsonconnect.modules.settings.Exporter
import com.server.zepsonconnect.modules.settings.Importer
import com.server.zepsonconnect.modules.settings.KeyValueStorage
import com.server.zepsonconnect.modules.settings.get

class PingSettings(
    private val storage: KeyValueStorage,
) : Exporter, Importer {
    val enabled: Boolean
        get() = intervalSeconds != null

    var intervalSeconds: Int?
        get() = storage.get<Int>(INTERVAL_SECONDS)?.takeIf { it > 0 }
        set(value) = storage.set(INTERVAL_SECONDS, value)

    companion object {
        private const val INTERVAL_SECONDS = "interval_seconds"
    }

    override fun export(): Map<String, *> {
        return mapOf(
            INTERVAL_SECONDS to intervalSeconds,
        )
    }

    override fun import(data: Map<String, *>) {
        data.forEach { (key, value) ->
            when (key) {
                INTERVAL_SECONDS -> storage.set(
                    key,
                    value?.toString()?.toFloat()?.toInt()?.toString()
                )
            }
        }
    }
}