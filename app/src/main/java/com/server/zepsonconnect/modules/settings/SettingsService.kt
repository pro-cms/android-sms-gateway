package com.server.zepsonconnect.modules.settings

import android.content.Context
import com.server.zepsonconnect.R
import com.server.zepsonconnect.modules.encryption.EncryptionSettings
import com.server.zepsonconnect.modules.gateway.GatewaySettings
import com.server.zepsonconnect.modules.logs.LogsSettings
import com.server.zepsonconnect.modules.messages.MessagesSettings
import com.server.zepsonconnect.modules.notifications.NotificationsService
import com.server.zepsonconnect.modules.ping.PingSettings
import com.server.zepsonconnect.modules.webhooks.WebhooksSettings

class SettingsService(
    private val context: Context,
    private val notificationsService: NotificationsService,
    encryptionSettings: EncryptionSettings,
    gatewaySettings: GatewaySettings,
    messagesSettings: MessagesSettings,
    pingSettings: PingSettings,
    logsSettings: LogsSettings,
    webhooksSettings: WebhooksSettings
) {
    private val settings = mapOf(
        "encryption" to encryptionSettings,
        "gateway" to gatewaySettings,
        "messages" to messagesSettings,
        "ping" to pingSettings,
        "logs" to logsSettings,
        "webhooks" to webhooksSettings
    )

    fun getAll(): Map<String, *> {
        return settings.mapValues { (it.value as? Exporter)?.export() }
    }

    fun update(data: Map<String, *>) {
        if (data.isEmpty()) {
            return
        }

        data.forEach { (key, value) ->
            try {
                settings[key]?.let {
                    (it as? Importer)?.import(value as Map<String, *>)
                }
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Failed to import $key: ${e.message}", e)
            }
        }

        notificationsService.notify(
            context,
            NotificationsService.NOTIFICATION_ID_SETTINGS_CHANGED,
            context.getString(R.string.settings_changed_via_api_restart_the_app_to_apply_changes)
        )
    }
}