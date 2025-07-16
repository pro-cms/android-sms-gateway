package com.server.zepsonconnect.modules.orchestrator

import android.app.ForegroundServiceStartNotAllowedException
import android.content.Context
import android.os.Build
import com.server.zepsonconnect.helpers.SettingsHelper
import com.server.zepsonconnect.modules.gateway.GatewayService
import com.server.zepsonconnect.modules.localserver.LocalServerService
import com.server.zepsonconnect.modules.logs.LogsService
import com.server.zepsonconnect.modules.logs.db.LogEntry
import com.server.zepsonconnect.modules.messages.MessagesService
import com.server.zepsonconnect.modules.ping.PingService
import com.server.zepsonconnect.modules.webhooks.WebHooksService

class OrchestratorService(
    private val messagesSvc: MessagesService,
    private val gatewaySvc: GatewayService,
    private val localServerSvc: LocalServerService,
    private val webHooksSvc: WebHooksService,
    private val pingSvc: PingService,
    private val logsSvc: LogsService,
    private val settings: SettingsHelper,
) {
    fun start(context: Context, autostart: Boolean) {
        if (autostart && !settings.autostart) {
            return
        }

        logsSvc.start(context)
        messagesSvc.start(context)
        gatewaySvc.start(context)
        webHooksSvc.start(context)

        try {
            localServerSvc.start(context)
            pingSvc.start(context)
        } catch (e: Throwable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                logsSvc.insert(
                    LogEntry.Priority.WARN,
                    MODULE_NAME,
                    "Can't start foreground services while the app is running in the background"
                )
                return
            }

            throw e;
        }
    }

    fun stop(context: Context) {
        pingSvc.stop(context)
        webHooksSvc.stop(context)
        localServerSvc.stop(context)
        gatewaySvc.stop(context)
        messagesSvc.stop(context)
        logsSvc.stop(context)
    }
}