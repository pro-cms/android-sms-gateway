package com.server.zepsonconnect

import android.app.Application
import healthModule
import com.server.zepsonconnect.data.dbModule
import com.server.zepsonconnect.modules.connection.connectionModule
import com.server.zepsonconnect.modules.encryption.encryptionModule
import com.server.zepsonconnect.modules.events.eventBusModule
import com.server.zepsonconnect.modules.gateway.GatewayService
import com.server.zepsonconnect.modules.localserver.localserverModule
import com.server.zepsonconnect.modules.logs.logsModule
import com.server.zepsonconnect.modules.messages.messagesModule
import com.server.zepsonconnect.modules.notifications.notificationsModule
import com.server.zepsonconnect.modules.orchestrator.OrchestratorService
import com.server.zepsonconnect.modules.orchestrator.orchestratorModule
import com.server.zepsonconnect.modules.ping.pingModule
import com.server.zepsonconnect.modules.receiver.receiverModule
import com.server.zepsonconnect.modules.settings.settingsModule
import com.server.zepsonconnect.modules.webhooks.webhooksModule
import com.server.zepsonconnect.receivers.EventsReceiver
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                eventBusModule,
                settingsModule,
                dbModule,
                logsModule,
                notificationsModule,
                messagesModule,
                receiverModule,
                encryptionModule,
                com.server.zepsonconnect.modules.gateway.gatewayModule,
                healthModule,
                webhooksModule,
                localserverModule,
                pingModule,
                connectionModule,
                orchestratorModule,
            )
        }

        Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(
                Thread.getDefaultUncaughtExceptionHandler()!!,
                get()
            )
        )

        instance = this

        EventsReceiver.register(this)

        get<OrchestratorService>().start(this, true)
    }

    val gatewayService: GatewayService by inject()

    companion object {
        lateinit var instance: App
            private set
    }
}
