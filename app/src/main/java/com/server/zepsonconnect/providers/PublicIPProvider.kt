package com.server.zepsonconnect.providers

import com.server.zepsonconnect.modules.gateway.GatewayService
import org.koin.java.KoinJavaComponent.inject

class PublicIPProvider: IPProvider {
    private val gatewaySvc by inject<GatewayService>(GatewayService::class.java)

    override suspend fun getIP(): String? {
        return try {
            gatewaySvc.getPublicIP()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}