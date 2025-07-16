package com.server.zepsonconnect.modules.settings

import androidx.preference.PreferenceManager
import com.server.zepsonconnect.helpers.SettingsHelper
import com.server.zepsonconnect.modules.encryption.EncryptionSettings
import com.server.zepsonconnect.modules.gateway.GatewaySettings
import com.server.zepsonconnect.modules.localserver.LocalServerSettings
import com.server.zepsonconnect.modules.logs.LogsSettings
import com.server.zepsonconnect.modules.messages.MessagesSettings
import com.server.zepsonconnect.modules.ping.PingSettings
import com.server.zepsonconnect.modules.webhooks.TemporaryStorage
import com.server.zepsonconnect.modules.webhooks.WebhooksSettings
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val settingsModule = module {
    singleOf(::SettingsService)
    factory { PreferenceManager.getDefaultSharedPreferences(get()) }
    factory { SettingsHelper(get()) }

    factory {
        EncryptionSettings(
            PreferencesStorage(get(), "encryption")
        )
    }
    factory {
        GatewaySettings(
            PreferencesStorage(get(), "gateway")
        )
    }
    factory {
        MessagesSettings(
            PreferencesStorage(get(), "messages")
        )
    }
    factory {
        LocalServerSettings(
            PreferencesStorage(get(), "localserver")
        )
    }
    factory {
        PingSettings(
            PreferencesStorage(get(), "ping")
        )
    }
    factory {
        LogsSettings(
            PreferencesStorage(get(), "logs")
        )
    }
    factory {
        WebhooksSettings(
            PreferencesStorage(get(), "webhooks")
        )
    }
    factory {
        TemporaryStorage(
            PreferencesStorage(get(), "webhooks")
        )
    }
}