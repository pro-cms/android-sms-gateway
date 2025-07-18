package com.server.zepsonconnect.modules.localserver.routes

import android.content.Context
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import com.server.zepsonconnect.domain.EntitySource
import com.server.zepsonconnect.domain.MessageContent
import com.server.zepsonconnect.domain.ProcessingState
import com.server.zepsonconnect.modules.localserver.LocalServerSettings
import com.server.zepsonconnect.modules.localserver.domain.PostMessageRequest
import com.server.zepsonconnect.modules.localserver.domain.PostMessageResponse
import com.server.zepsonconnect.modules.localserver.domain.PostMessagesInboxExportRequest
import com.server.zepsonconnect.modules.messages.MessagesService
import com.server.zepsonconnect.modules.messages.data.Message
import com.server.zepsonconnect.modules.messages.data.SendParams
import com.server.zepsonconnect.modules.messages.data.SendRequest
import com.server.zepsonconnect.modules.receiver.ReceiverService
import java.util.Date

class MessagesRoutes(
    private val context: Context,
    private val messagesService: MessagesService,
    private val receiverService: ReceiverService,
    private val settings: LocalServerSettings,
) {
    fun register(routing: Route) {
        routing.apply {
            messagesRoutes()
            route("/inbox") {
                inboxRoutes(context)
            }
        }
    }

    private fun Route.messagesRoutes() {
        post {
            val request = call.receive<PostMessageRequest>()

            if (request.deviceId?.let { it == settings.deviceId } == false) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid device ID")
                )
                return@post
            }

            val messageTypes =
                listOfNotNull(request.textMessage, request.dataMessage, request.message)
            when {
                messageTypes.isEmpty() -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "Must specify exactly one of: textMessage, dataMessage, or message")
                    )
                    return@post
                }

                messageTypes.size > 1 -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "Cannot specify multiple message types simultaneously")
                    )
                    return@post
                }
            }

            // Validate message parameters
            request.message?.let { msg ->
                // Text validation
                if (msg.isEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "Text message is empty")
                    )
                    return@post
                }
            }

            // Validate data message parameters
            request.dataMessage?.let { dataMsg ->
                // Port validation
                if (dataMsg.port < 0 || dataMsg.port > 65535) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "Port must be between 0 and 65535")
                    )
                    return@post
                }

                // Data validation (only for non-empty check)
                if (dataMsg.data.isEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "Data message cannot be empty")
                    )
                    return@post
                }
            }

            // Validate text message parameters
            request.textMessage?.let { textMsg ->
                // Text validation
                if (textMsg.text.isEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "Text message is empty")
                    )
                    return@post
                }
            }

            // Existing validation
            if (request.phoneNumbers.isEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "phoneNumbers is empty")
                )
                return@post
            }
            if (request.simNumber != null && request.simNumber < 1) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "simNumber must be >= 1")
                )
                return@post
            }
            val skipPhoneValidation =
                call.request.queryParameters["skipPhoneValidation"]
                    ?.toBooleanStrict() ?: false

            // Create message content based on type
            val messageContent = when {
                request.message != null -> {
                    MessageContent.Text(request.message)
                }

                request.textMessage != null -> {
                    MessageContent.Text(request.textMessage.text)
                }

                request.dataMessage != null -> {
                    MessageContent.Data(
                        request.dataMessage.data,
                        request.dataMessage.port.toUShort()
                    )
                }

                else -> {
                    // This case should be caught by validation, but just in case
                    throw IllegalStateException("Unknown message type")
                }
            }

            val sendRequest = SendRequest(
                EntitySource.Local,
                Message(
                    request.id ?: NanoIdUtils.randomNanoId(),
                    content = messageContent,
                    phoneNumbers = request.phoneNumbers,
                    isEncrypted = request.isEncrypted ?: false,
                    createdAt = Date(),
                ),
                SendParams(
                    request.withDeliveryReport ?: true,
                    skipPhoneValidation = skipPhoneValidation,
                    simNumber = request.simNumber,
                    validUntil = request.validUntil,
                    priority = request.priority,
                )
            )
            messagesService.enqueueMessage(sendRequest)

            val messageId = sendRequest.message.id

            call.respond(
                HttpStatusCode.Accepted,
                PostMessageResponse(
                    id = messageId,
                    deviceId = requireNotNull(settings.deviceId),
                    state = ProcessingState.Pending,
                    recipients = request.phoneNumbers.map {
                        PostMessageResponse.Recipient(
                            it,
                            ProcessingState.Pending,
                            null
                        )
                    },
                    isEncrypted = request.isEncrypted ?: false,
                    states = mapOf(ProcessingState.Pending to Date())
                )
            )
        }
        get("{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val message = try {
                messagesService.getMessage(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
            } catch (e: Throwable) {
                return@get call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("message" to e.message)
                )
            }

            call.respond(
                PostMessageResponse(
                    message.message.id,
                    requireNotNull(settings.deviceId),
                    message.message.state,
                    message.recipients.map {
                        PostMessageResponse.Recipient(
                            it.phoneNumber,
                            it.state,
                            it.error
                        )
                    },
                    message.message.isEncrypted,
                    message.states.associate {
                        it.state to Date(it.updatedAt)
                    }
                )
            )
        }
    }

    private fun Route.inboxRoutes(context: Context) {
        post("export") {
            val request = call.receive<PostMessagesInboxExportRequest>().validate()
            try {
                receiverService.export(context, request.period)
                call.respond(HttpStatusCode.Accepted)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("message" to "Failed to export inbox: ${e.message}")
                )
            }
        }
    }
}