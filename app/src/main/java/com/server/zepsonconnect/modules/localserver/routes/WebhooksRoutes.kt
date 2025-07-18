package com.server.zepsonconnect.modules.localserver.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import com.server.zepsonconnect.domain.EntitySource
import com.server.zepsonconnect.modules.localserver.LocalServerSettings
import com.server.zepsonconnect.modules.webhooks.WebHooksService
import com.server.zepsonconnect.modules.webhooks.domain.WebHookDTO

class WebhooksRoutes(
    private val webHooksService: WebHooksService,
    private val localServerSettings: LocalServerSettings,
) {
    fun register(routing: Route) {
        routing.apply {
            webhooksRoutes()
        }
    }

    private fun Route.webhooksRoutes() {
        get {
            call.respond(webHooksService.select(EntitySource.Local))
        }
        post {
            val webhook = call.receive<WebHookDTO>()
            if (webhook.deviceId != null && webhook.deviceId != localServerSettings.deviceId) {
                throw IllegalArgumentException(
                    "Device ID mismatch"
                )
            }

            val updated = webHooksService.replace(EntitySource.Local, webhook)

            call.respond(HttpStatusCode.Created, updated)
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            webHooksService.delete(EntitySource.Local, id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}