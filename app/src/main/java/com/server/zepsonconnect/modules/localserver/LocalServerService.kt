package com.server.zepsonconnect.modules.localserver

import android.content.Context
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.server.zepsonconnect.modules.events.EventBus
import com.server.zepsonconnect.modules.localserver.events.IPReceivedEvent
import com.server.zepsonconnect.providers.LocalIPProvider
import com.server.zepsonconnect.providers.PublicIPProvider

class LocalServerService(
    private val settings: LocalServerSettings,
    private val events: EventBus,
) {

    private fun getDeviceId(context: Context): String {
        val firstInstallTime = context.packageManager.getPackageInfo(
            context.packageName,
            0
        ).firstInstallTime
        val deviceName = "${Build.MANUFACTURER}/${Build.PRODUCT}"

        return deviceName.hashCode().toULong()
            .toString(16).padStart(16, '0') + firstInstallTime.toULong()
            .toString(16).padStart(16, '0')
    }

    fun start(context: Context) {
        if (!settings.enabled) return
        settings.deviceId = settings.deviceId ?: getDeviceId(context)

        WebService.start(context)

        scope.launch(Dispatchers.IO) {
            val localIP = LocalIPProvider(context).getIP()
            val remoteIP = PublicIPProvider().getIP()

            events.emit(IPReceivedEvent(localIP, remoteIP))
        }
    }

    fun stop(context: Context) {
        WebService.stop(context)
    }

    fun isActiveLiveData(context: Context) = WebService.STATUS

    companion object {
        private val job = SupervisorJob()
        private val scope = CoroutineScope(job)
    }
}